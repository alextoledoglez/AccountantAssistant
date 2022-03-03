package com.personal.accountantAssistant.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.personal.accountantAssistant.data.dao.ExpenseDao
import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.utils.ActivityUtils
import com.personal.accountantAssistant.utils.PermissionsUtils
import com.personal.accountantAssistant.utils.ToastUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {

    abstract fun expensesDao(): ExpenseDao

    companion object {

        private const val DB_NAME = "ACCOUNTANT_ASSISTANT"
        private const val FILE_DIRECTORY_TYPE = ""
        private const val DB_BACKUP_FORMAT = "%s"

        @JvmStatic
        fun getInstance(context: Context) = Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, DB_NAME
        ).build()

        fun importDBFrom(
            context: Context,
            @StringRes db_successful_imported: Int,
            @StringRes db_importing_failure: Int
        ) {
            try {
                if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                    val sourceDirectory = context.getExternalFilesDir(FILE_DIRECTORY_TYPE)
                    ToastUtils.showLongText(
                        context,
                        if (sourceDirectory?.canWrite() == true) {
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
                    )
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
                        ToastUtils.showLongText(context, db_successful_exported)
                        return backupDB
                    } else {
                        ToastUtils.showLongText(context, db_exporting_failure)
                    }
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
                ActivityUtils.parse(context),
                PermissionsUtils.STORAGE_PERMISSIONS,
                PermissionsUtils.STORAGE_PERMISSION_CODE
            )
        }
    }
}