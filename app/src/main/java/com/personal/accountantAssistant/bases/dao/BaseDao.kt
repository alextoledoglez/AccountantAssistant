package com.personal.accountantAssistant.bases.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: List<T>): List<Long>

    @Update
    suspend fun update(t: T): Int

    @Update
    suspend fun update(t: List<T>)

    @Delete
    suspend fun delete(t: T): Int

    @Delete
    suspend fun delete(t: List<T>)
}