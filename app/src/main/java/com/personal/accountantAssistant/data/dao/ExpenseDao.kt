package com.personal.accountantAssistant.data.dao

import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity

interface ExpenseDao : BaseDao<ExpenseEntity> {
    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    fun selectAll(): List<ExpenseEntity>

    @Query("DELETE FROM ExpenseEntity")
    fun clearTable()
}