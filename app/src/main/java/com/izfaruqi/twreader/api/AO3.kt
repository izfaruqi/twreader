package com.izfaruqi.twreader.api

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.izfaruqi.twreader.search.SearchResult
import com.izfaruqi.twreader.work.Work
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.select.QueryParser
import java.lang.NumberFormatException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AO3 {
    object QuerySelectors {
        init {
            Log.d("AO3", "initializing query selectors")
        }

        val SEARCH_ITEM_BODY = QueryParser.parse(".work.blurb.group")
        val SEARCH_ITEM_TITLE_AUTHOR = QueryParser.parse("div h4 a")
        val SEARCH_ITEM_SUMMARY = QueryParser.parse("blockquote")
        val SEARCH_TOTAL_RESULTS = QueryParser.parse("#main > h3:nth-child(5)")
        val SEARCH_PAGINATION = QueryParser.parse("#main > ol.pagination.actions > li")

        val FULLWORK_TITLE = QueryParser.parse("#workskin > div:nth-child(1) > h2")
        val FULLWORK_AUTHOR = QueryParser.parse("#workskin > div:nth-child(1) > h3 > a")
        val FULLWORK_SUMMARY = QueryParser.parse("#workskin > div:nth-child(1) > div.summary.module > blockquote")

        val FULLWORK_METAGROUP = QueryParser.parse("dl.work.meta.group")
        val FULLWORK_RATINGS = QueryParser.parse("dd.rating.tags > ul > li")
        val FULLWORK_WARNINGS = QueryParser.parse("dd.warning.tags > ul > li")
        val FULLWORK_CATEGORIES = QueryParser.parse("dd.category.tags > ul > li")
        val FULLWORK_FANDOMS = QueryParser.parse("dd.fandom.tags > ul > li")
        val FULLWORK_RELATIONSHIPS = QueryParser.parse("dd.relationship.tags > ul > li")
        val FULLWORK_CHARACTERS = QueryParser.parse("dd.character.tags > ul > li")
        val FULLWORK_TAGS = QueryParser.parse("dd.freeform.tags > ul > li")
        val FULLWORK_LANGUAGE = QueryParser.parse("dd.language")
        val FULLWORK_STATS = QueryParser.parse("dd.stats")
        val FULLWORK_PUBLISHED = QueryParser.parse("dl > dd.published")
        val FULLWORK_UPDATED = QueryParser.parse("dl > dd.status")
        val FULLWORK_WORDS = QueryParser.parse("dl > dd.words")
        val FULLWORK_CHAPTERS = QueryParser.parse("dl > dd.chapters")
        val FULLWORK_KUDOS = QueryParser.parse("dl > dd.kudos")
        val FULLWORK_BOOKMARKS = QueryParser.parse("dl > dd.bookmarks")
        val FULLWORK_HITS = QueryParser.parse("dl > dd.hits")
    }

    object API {

        val cookieJar = object : CookieJar {
            val allowAdult = Cookie.Builder().name("view_adult").value("true").domain("archiveofourown.org").build()

            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {

            }

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                return mutableListOf(allowAdult)
            }
        }

        val httpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()

        suspend fun search(query: String, page: Int = 1) = withContext(Dispatchers.IO) {
            return@withContext parseSearchPage(getSearchPage(query, page))
        }

        private suspend fun getSearchPage(query: String, page: Int) = withContext(Dispatchers.IO) {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            return@withContext suspendCoroutine<String> {
                AndroidNetworking.get("https://archiveofourown.org/works/search?page=$page&utf8=%E2%9C%93&work_search%5Bquery%5D=${encodedQuery}")
                    .setOkHttpClient(httpClient)
                        .build()
                        .getAsString(object : StringRequestListener {
                            override fun onResponse(response: String?) {
                                it.resume(response ?: "")
                            }

                            override fun onError(anError: ANError?) {
                                it.resume(anError.toString())
                            }
                        })
            }
        }

        private suspend fun parseSearchPage(rawPage: String) = withContext(Dispatchers.Default){
            val doc = Jsoup.parse(rawPage)
            val body = doc.body()
            val workElms = body.select(QuerySelectors.SEARCH_ITEM_BODY)
            val workList = ArrayList<Work>()
            workElms.forEach { workElm ->
                val id = workElm.id().substring(5).toInt()
                val titleAuthorContainer = workElm.select(QuerySelectors.SEARCH_ITEM_TITLE_AUTHOR)
                val title = titleAuthorContainer.getOrNull(0)?.text() ?: ""
                val author = titleAuthorContainer.getOrNull(1)?.text() ?: ""
                val summary = workElm.select(QuerySelectors.SEARCH_ITEM_SUMMARY).text()
                workList.add(Work(id, title, author, summary))
            }

            val totalResults: Int = try {
                body.select(QuerySelectors.SEARCH_TOTAL_RESULTS)?.text()?.dropLast(8)?.toInt() ?: 0
            } catch (e: NumberFormatException) {
                0
            }

            var totalPages = 1
            val paginationElms = body.select(QuerySelectors.SEARCH_PAGINATION)
            if(paginationElms != null){
                totalPages = paginationElms.getOrNull(paginationElms.size - 2)?.text()?.toInt() ?: 1
            }
            Log.d("AO3", totalPages.toString())

            return@withContext SearchResult(workList, totalResults, totalPages = totalPages)
        }

        suspend fun fetchFullWork(id: Int) = withContext(Dispatchers.IO){
            val work = parseFullWorkPage(fetchFullWorkPage(id))
            work.id = id
            return@withContext work
        }

        suspend fun fetchFullWorkPage(id: Int) = withContext(Dispatchers.IO){
            return@withContext suspendCoroutine<String> {
                AndroidNetworking.get("https://archiveofourown.org/works/$id")
                        .setOkHttpClient(httpClient)
                        .build()
                        .getAsString(object : StringRequestListener {
                            override fun onResponse(response: String?) {
                                it.resume(response ?: "")
                            }

                            override fun onError(anError: ANError?) {
                                it.resume(anError.toString())
                            }
                        })
            }
        }

        suspend fun parseFullWorkPage(rawPage: String) = withContext(Dispatchers.Default){
            var work = Work()
            val doc = Jsoup.parse(rawPage)
            val body = doc.body()
            work.title = body.select(QuerySelectors.FULLWORK_TITLE)?.text() ?: ""
            work.author = body.select(QuerySelectors.FULLWORK_AUTHOR)?.text() ?: ""
            work.summary = body.select(QuerySelectors.FULLWORK_SUMMARY)?.text() ?: ""

            val metagroupElm = body.select(QuerySelectors.FULLWORK_METAGROUP)[0]

            val ratingsElm = metagroupElm.select(QuerySelectors.FULLWORK_RATINGS)
            if(ratingsElm != null){
                val ratings = ArrayList<String>()
                ratingsElm.forEach {
                    ratings.add(it.select("a").text())
                }
                work.ratings = ratings
            }

            val warningsElm = metagroupElm.select(QuerySelectors.FULLWORK_WARNINGS)
            if(warningsElm != null){
                val warnings = ArrayList<String>()
                warningsElm.forEach {
                    warnings.add(it.select("a").text())
                }
                work.warnings = warnings
            }

            val categoriesElm = metagroupElm.select(QuerySelectors.FULLWORK_CATEGORIES)
            if(categoriesElm != null){
                val categories = ArrayList<String>()
                categoriesElm.forEach {
                    categories.add(it.select("a").text())
                }
                work.categories = categories
            }

            val fandomsElm = metagroupElm.select(QuerySelectors.FULLWORK_FANDOMS)
            if(fandomsElm != null){
                val fandoms = ArrayList<String>()
                fandomsElm.forEach {
                    fandoms.add(it.select("a").text())
                }
                work.fandoms = fandoms
            }

            val relationshipsElm = metagroupElm.select(QuerySelectors.FULLWORK_RELATIONSHIPS)
            if(relationshipsElm != null){
                val relationships = ArrayList<String>()
                relationshipsElm.forEach {
                    relationships.add(it.select("a").text())
                }
                work.relationships = relationships
            }

            val charactersElm = metagroupElm.select(QuerySelectors.FULLWORK_CHARACTERS)
            if(charactersElm != null){
                val characters = ArrayList<String>()
                charactersElm.forEach {
                    characters.add(it.select("a").text())
                }
                work.characters = characters
            }

            val tagsElm = metagroupElm.select(QuerySelectors.FULLWORK_TAGS)
            if(tagsElm != null){
                val tags = ArrayList<String>()
                tagsElm.forEach {
                    tags.add(it.select("a").text())
                }
                work.tags = tags
            }

            work.language = metagroupElm.select(QuerySelectors.FULLWORK_LANGUAGE)[0].text()

            val statsElm = metagroupElm.select(QuerySelectors.FULLWORK_STATS)[0]
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            work.published = dateFormatter.parse(statsElm.select(QuerySelectors.FULLWORK_PUBLISHED)[0].text())!!
            val updatedElm = statsElm.select(QuerySelectors.FULLWORK_UPDATED).getOrNull(0)
            if(updatedElm != null){
                work.updated = dateFormatter.parse(updatedElm.text())
            }
            work.words = statsElm.select(QuerySelectors.FULLWORK_WORDS).getOrNull(0)?.text()?.toInt() ?: 0
            work.kudos = statsElm.select(QuerySelectors.FULLWORK_KUDOS).getOrNull(0)?.text()?.toInt() ?: 0
            work.bookmarks = statsElm.select(QuerySelectors.FULLWORK_BOOKMARKS).getOrNull(0)?.text()?.toInt() ?: 0
            work.hits = statsElm.select(QuerySelectors.FULLWORK_HITS).getOrNull(0)?.text()?.toInt() ?: 0

            

            return@withContext work
        }
    }
}