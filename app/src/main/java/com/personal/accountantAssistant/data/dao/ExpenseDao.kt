package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.EXPENSES_TABLE
import com.personal.accountantAssistant.data.entities.ExpenseEntity

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

    @Transaction
    @Query("SELECT * FROM $EXPENSES_TABLE WHERE type ==:typeName")
    suspend fun selectAll(typeName: String): Array<ExpenseEntity>

    @Query("UPDATE $EXPENSES_TABLE SET active=:active WHERE type ==:typeName")
    suspend fun activeAll(active: String, typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE WHERE type ==:typeName")
    suspend fun deleteByType(typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE")
    suspend fun clearTable(): Int
}