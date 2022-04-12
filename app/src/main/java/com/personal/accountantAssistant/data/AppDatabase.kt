package com.personal.accountantAssistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.migrations.CardTableMigrations
import com.personal.accountantAssistant.data.migrations.ExpensesTableMigrations

@Database(entities = [CardEntity::class, ExpenseEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardsDao(): CardDao
    abstract fun expensesDao(): ExpenseDao

    companion object {

        const val DB_NAME = "ACCOUNTANT_ASSISTANT"
        const val CARDS_TABLE_NAME = "CARD_TABLE"
        const val PAYMENTS_TABLE_NAME = "PAYMENTS_TABLE"
        const val EXPENSES_TABLE_NAME = "EXPENSES_TABLE"

        private fun getMigrations() = arrayOf(
            CardTableMigrations.MIGRATION_1_2,
            CardTableMigrations.MIGRATION_2_3,
            ExpensesTableMigrations.MIGRATION_3_4,
            ExpensesTableMigrations.MIGRATION_4_5
        )

        @JvmStatic
        fun getInstance(context: Context) = Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, DB_NAME
        ).addMigrations(*getMigrations()).build()
    }
}