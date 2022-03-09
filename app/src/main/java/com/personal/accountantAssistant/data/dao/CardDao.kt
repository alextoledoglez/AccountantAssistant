package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARD_TABLE
import com.personal.accountantAssistant.data.entities.CardEntity

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Transaction
    @Query("SELECT * FROM $CARD_TABLE")
    suspend fun selectAll(): Array<CardEntity>

    @Query("UPDATE ${AppDatabase.PAYMENTS_TABLE} SET active=:active")
    suspend fun activeAll(active: String): Int

    @Query("DELETE FROM $CARD_TABLE")
    suspend fun clearTable(): Int
}