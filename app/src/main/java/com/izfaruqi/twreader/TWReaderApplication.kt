package com.izfaruqi.twreader

import android.app.Application

class TWReaderApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}