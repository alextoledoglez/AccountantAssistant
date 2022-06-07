package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.CARDS_TABLE_NAME
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.ACTIVE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.AVAILABLE_VALUE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.DATE
import com.personal.accountantAssistant.data.entities.CardEntity.Companion.ID

@Dao
interface CardDao : BaseDao<CardEntity> {
    @Transaction
    @Query("SELECT * FROM $CARDS_TABLE_NAME ORDER BY $AVAILABLE_VALUE DESC")
    suspend fun selectAll(): Array<CardEntity>

    @Transaction
    @Query("SELECT * FROM $CARDS_TABLE_NAME WHERE ($DATE BETWEEN $DATE AND :dateStr)")
    suspend fun selectAllBy(dateStr: String): Array<CardEntity>

    @Transaction
    @Query(
        "SELECT *, SUM($AVAILABLE_VALUE) as $AVAILABLE_VALUE, COUNT() as $ACTIVE " +
                "FROM $CARDS_TABLE_NAME WHERE $ACTIVE=1"
    )
    suspend fun getSummary(): CardEntity

    @Query("UPDATE $CARDS_TABLE_NAME SET $ACTIVE=:active WHERE $ID=:id")
    suspend fun setActive(id: Int, active: Int): Int

    @Query("UPDATE $CARDS_TABLE_NAME SET $ACTIVE=:active")
    suspend fun activeAll(active: Int): Int

    @Query("DELETE FROM $CARDS_TABLE_NAME")
    suspend fun clearTable(): Int
}