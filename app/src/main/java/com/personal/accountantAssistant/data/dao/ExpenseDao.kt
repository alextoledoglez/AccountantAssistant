package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.EXPENSES_TABLE_NAME
import com.personal.accountantAssistant.data.entities.ExpenseEntity

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

    @Transaction
    @Query("SELECT * FROM $EXPENSES_TABLE_NAME WHERE type ==:typeName")
    suspend fun selectAll(typeName: String): Array<ExpenseEntity>

    @Query("UPDATE $EXPENSES_TABLE_NAME SET active=:active WHERE type ==:typeName")
    suspend fun activeAll(active: String, typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE_NAME WHERE type ==:typeName")
    suspend fun deleteByType(typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE_NAME")
    suspend fun clearTable(): Int
}