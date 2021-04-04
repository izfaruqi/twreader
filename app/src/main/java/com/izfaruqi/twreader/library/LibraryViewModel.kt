package com.izfaruqi.twreader.library

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izfaruqi.twreader.AppDatabase
import com.izfaruqi.twreader.TWReaderApplication
import com.izfaruqi.twreader.work.Work
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    lateinit var model: LibraryModel
    val works = MutableLiveData(ArrayList<Work>())

    fun getAllFromLibrary(){
        viewModelScope.launch {
            works.value = ArrayList(model.getAllFromDb())
        }
    }

    fun initModel(context: Context){
        model = LibraryModel(context)
    }
}