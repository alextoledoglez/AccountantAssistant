package com.personal.accountantAssistant.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.migrations.CardTableMigrations
import com.personal.accountantAssistant.extensions.showToastLongText
import com.personal.accountantAssistant.extensions.toActivity
import com.personal.accountantAssistant.utils.PermissionsUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

@Database(entities = [CardEntity::class, ExpenseEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardsDao(): CardDao
    abstract fun expensesDao(): ExpenseDao

    companion object {

        private const val FILE_DIRECTORY_TYPE = ""
        private const val DB_BACKUP_FORMAT = "%s"
        const val DB_NAME = "ACCOUNTANT_ASSISTANT"
        const val CARD_TABLE = "CARD_TABLE"
        const val PAYMENTS_TABLE = "PAYMENTS_TABLE"

        @JvmStatic
        fun getInstance(context: Context) = Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, DB_NAME
        ).addMigrations(CardTableMigrations.MIGRATION_1_2).build()

        fun importDBFrom(
            context: Context,
            @StringRes db_successful_imported: Int,
            @StringRes db_importing_failure: Int
        ) {
            try {
                if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                    val sourceDirectory = context.getExternalFilesDir(FILE_DIRECTORY_TYPE)
                    val res = if (sourceDirectory?.canWrite() == true) {
                        val backupDBPath = java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                        val currentDB = File(sourceDirectory, backupDBPath)
                        val backupDB = context.getDatabasePath(DB_NAME)
                        val src = FileInputStream(currentDB).channel
                        FileOutputStream(backupDB).channel.apply {
                            transferFrom(src, 0, src.size())
                            src.close()
                            close()
                        }
                        db_successful_imported
                    } else {
                        db_importing_failure
                    }
                    context.showToastLongText(res)
                } else {
                    requestStoragePermissionsFrom(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun exportDBFrom(
            context: Context,
            @StringRes db_successful_exported: Int,
            @StringRes db_exporting_failure: Int
        ): File? {
            try {
                if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                    val currentDB: File = context.getDatabasePath(DB_NAME)
                    val src: FileChannel = FileInputStream(currentDB).channel
                    val sourceDirectory: File? = context.getExternalFilesDir(FILE_DIRECTORY_TYPE)
                    if (sourceDirectory?.canWrite() == true) {
                        val backupDBPath = java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                        val backupDB = File(sourceDirectory, backupDBPath)
                        FileOutputStream(backupDB).channel.apply {
                            transferFrom(src, 0, src.size())
                            src.close()
                            close()
                        }
                        context.showToastLongText(db_successful_exported)
                        return backupDB
                    } else
                        context.showToastLongText(db_exporting_failure)

                } else {
                    requestStoragePermissionsFrom(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun requestStoragePermissionsFrom(context: Context) {
            requestPermissions(
                context.toActivity(),
                PermissionsUtils.STORAGE_PERMISSIONS,
                PermissionsUtils.STORAGE_PERMISSION_CODE
            )
        }
    }
}