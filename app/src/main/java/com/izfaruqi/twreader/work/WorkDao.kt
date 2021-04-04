package com.izfaruqi.twreader.work

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkDao {
    @Query("SELECT * FROM work")
    suspend fun getAll(): List<Work>

    @Query("SELECT * FROM work WHERE id IN (:ids)")
    suspend fun getAllByIds(ids: IntArray): List<Work>

    @Insert
    suspend fun insertAll(vararg works: Work)

    @Delete
    suspend fun delete(work: Work)
}