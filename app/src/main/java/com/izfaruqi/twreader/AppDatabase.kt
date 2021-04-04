package com.izfaruqi.twreader

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.izfaruqi.twreader.work.Work
import com.izfaruqi.twreader.work.WorkDao

@Database(entities = arrayOf(Work::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun workDao(): WorkDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
                }
                return INSTANCE!!
            }
        }
    }
}