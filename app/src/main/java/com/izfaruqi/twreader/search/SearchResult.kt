package com.izfaruqi.twreader.search

import com.izfaruqi.twreader.work.Work

data class SearchResult(var results: ArrayList<Work> = ArrayList(), var totalResults: Int = 0, var currentPage: Int = 1, var totalPages: Int = 1)