package com.izfaruqi.twreader.viewwork

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izfaruqi.twreader.library.LibraryModel
import com.izfaruqi.twreader.work.Work
import kotlinx.coroutines.launch

class ViewWorkViewModel: ViewModel() {
    val model = ViewWorkModel()
    var work = MutableLiveData(Work())
    var isLoading = MutableLiveData(false)

    fun initModel(context: Context){
        model.initDb(context)
    }

    fun loadFullWork(){
        viewModelScope.launch {
            isLoading.value = true
            work.value = model.getFullWork(work.value!!.id)
            isLoading.value = false
        }
    }

    fun insertWorkIntoDb(){
        viewModelScope.launch {
            model.insertIntoDb(work.value!!)
        }
    }
}