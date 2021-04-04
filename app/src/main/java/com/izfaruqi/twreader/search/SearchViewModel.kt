package com.izfaruqi.twreader.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izfaruqi.twreader.work.Work
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val model = SearchModel()
    val works = MutableLiveData(ArrayList<Work>())
    val totalResult = MutableLiveData(-1)
    val searchQuery = MutableLiveData<String>("")
    var searchedQuery = ""
    var currentPage = 0
    var totalPage = 0
    val isLoading = MutableLiveData(false)
    var isFirstSearch = true

    fun onSearchSubmit(){
        viewModelScope.launch {
            isLoading.value = true
            Log.d("search", searchQuery.value ?: "")
            searchedQuery = searchQuery.value ?: ""
            val searchResult = model.search(searchedQuery)
            isFirstSearch = false
            currentPage = searchResult.currentPage
            totalPage = searchResult.totalPages
            works.value = searchResult.results
            totalResult.value = searchResult.totalResults
            isLoading.value = false
        }
    }

    fun onSearchLoadNextPage(){
        viewModelScope.launch {
            isLoading.value = true
            val prevLength = works.value!!.size
            val nextPage = currentPage + 1
            if(nextPage <= totalPage){
                val searchResult = model.search(searchedQuery, nextPage)
                currentPage = nextPage
                works.value!!.addAll(searchResult.results)
                works.value = works.value
            } else {
                Log.d("AO3", "End reached!")
            }
            isLoading.value = false
        }
    }
}