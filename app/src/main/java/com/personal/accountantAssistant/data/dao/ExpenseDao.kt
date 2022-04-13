package com.personal.accountantAssistant.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.personal.accountantAssistant.bases.dao.BaseDao
import com.personal.accountantAssistant.data.AppDatabase.Companion.EXPENSES_TABLE_NAME
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.ACTIVE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.DATE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.ID
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.TOTAL_VALUE
import com.personal.accountantAssistant.data.entities.ExpenseEntity.Companion.TYPE

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

    @Transaction
    @Query("SELECT * FROM $EXPENSES_TABLE_NAME WHERE $TYPE ==:typeName")
    suspend fun selectAll(typeName: String): Array<ExpenseEntity>

    @Transaction
    @Query(
        "SELECT *, SUM($TOTAL_VALUE) as $TOTAL_VALUE, COUNT() as $ACTIVE " +
                "FROM $EXPENSES_TABLE_NAME WHERE $TYPE ==:typeName AND $ACTIVE=1"
    )
    suspend fun getSummary(typeName: String): ExpenseEntity

    @Transaction
    @Query(
        "SELECT SUM($TOTAL_VALUE) as $TOTAL_VALUE " +
                "FROM $EXPENSES_TABLE_NAME " +
                "WHERE ($DATE BETWEEN $DATE AND :dateStr) AND $TYPE ==:typeName AND $ACTIVE=1"
    )
    suspend fun getTotalValueUntil(dateStr: String, typeName: String): ExpenseEntity

    @Query("UPDATE $EXPENSES_TABLE_NAME SET $ACTIVE=:active WHERE $ID=:id")
    suspend fun setActive(id: Int, active: Int): Int

    @Query("UPDATE $EXPENSES_TABLE_NAME SET $ACTIVE=:active WHERE $TYPE ==:typeName")
    suspend fun activeAll(active: Int, typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE_NAME WHERE $TYPE ==:typeName")
    suspend fun deleteByType(typeName: String): Int

    @Query("DELETE FROM $EXPENSES_TABLE_NAME")
    suspend fun clearTable(): Int
}