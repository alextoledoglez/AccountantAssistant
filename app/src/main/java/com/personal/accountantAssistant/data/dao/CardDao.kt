package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARD_TABLE
import com.personal.accountantAssistant.data.entities.CardEntity

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Transaction
    @Query("SELECT * FROM $CARD_TABLE ORDER BY value DESC")
    suspend fun selectAll(): Array<CardEntity>

    @Query("UPDATE $CARD_TABLE SET active=:isActive WHERE id=:id")
    suspend fun setActive(id: Long, isActive: Boolean): Int

    @Query("UPDATE $CARD_TABLE SET active=:isActive")
    suspend fun activeAll(isActive: Boolean): Int

    @Query("DELETE FROM $CARD_TABLE")
    suspend fun clearTable(): Int
}