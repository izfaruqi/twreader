package com.izfaruqi.twreader.search

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.izfaruqi.twreader.AppDatabase
import com.izfaruqi.twreader.api.AO3
import com.izfaruqi.twreader.work.Work
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SearchModel {

    suspend fun search(query: String, page: Int = 1): SearchResult{
        return AO3.API.search(query, page)
    }



}