package com.izfaruqi.twreader.work

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity
data class Work(@PrimaryKey var id: Int = -1, var title: String = "", var author: String = "", var summary: String = "", var ratings: ArrayList<String> = ArrayList(), var warnings: ArrayList<String> = ArrayList(),
                var categories: ArrayList<String> = ArrayList(), var fandoms: ArrayList<String> = ArrayList(), var relationships: ArrayList<String> = ArrayList(),
                var characters: ArrayList<String> = ArrayList(), var tags: ArrayList<String> = ArrayList(), var language: String = "", var published: Date? = null, var updated: Date? = null, var words: Int = 0,
                var chapterLatest: Int = 0, var chapterTotal: Int = 0, var kudos: Int = 0, var hits: Int = 0, var bookmarks: Int = 0): Serializable