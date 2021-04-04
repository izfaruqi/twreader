package com.izfaruqi.twreader.library

import android.content.Context
import com.izfaruqi.twreader.AppDatabase
import com.izfaruqi.twreader.TWReaderApplication
import com.izfaruqi.twreader.work.Work

class LibraryModel(var context: Context) {
    var db: AppDatabase = AppDatabase.getInstance(context)

    suspend fun getAllFromDb(): List<Work>{
        val works = db.workDao().getAll()
        return works
    }
}