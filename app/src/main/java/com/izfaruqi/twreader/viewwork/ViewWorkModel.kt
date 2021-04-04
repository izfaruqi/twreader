package com.izfaruqi.twreader.viewwork

import android.app.Application
import android.content.Context
import com.izfaruqi.twreader.AppDatabase
import com.izfaruqi.twreader.api.AO3
import com.izfaruqi.twreader.work.Work

class ViewWorkModel() {
    lateinit var db: AppDatabase

    fun initDb(context: Context){
        db = AppDatabase.getInstance(context)
    }

    suspend fun getFullWork(id: Int): Work {
        return AO3.API.fetchFullWork(id)
    }

    suspend fun insertIntoDb(work: Work){
        db.workDao().insertAll(work)
    }
}