package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARDS_TABLE_NAME
import com.personal.accountantAssistant.data.entities.CardEntity

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Transaction
    @Query("SELECT * FROM $CARDS_TABLE_NAME ORDER BY value DESC")
    suspend fun selectAll(): Array<CardEntity>

    @Query("UPDATE $CARDS_TABLE_NAME SET active=:isActive WHERE id=:id")
    suspend fun setActive(id: Long, isActive: Boolean): Int

    @Query("UPDATE $CARDS_TABLE_NAME SET active=:isActive")
    suspend fun activeAll(isActive: Boolean): Int

    @Query("DELETE FROM $CARDS_TABLE_NAME")
    suspend fun clearTable(): Int
}