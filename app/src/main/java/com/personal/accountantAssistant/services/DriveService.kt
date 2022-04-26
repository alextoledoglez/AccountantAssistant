package com.personal.accountantAssistant.services

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.InputStreamContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.providers.AnalyticsProvider
import com.personal.accountantAssistant.providers.CrashlyticsProvider
import java.io.*
import java.util.*
import java.util.concurrent.Executor

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveService(
    private val context: Context,
    private val executor: Executor,
    private val jsonFactory: GsonFactory,
    private val httpTransport: HttpTransport,
    private val credential: GoogleAccountCredential,
    private val analytics: AnalyticsProvider?,
    private val crashlytics: CrashlyticsProvider?
) {

    var drive: Drive? = null

    fun buildDrive(): Drive? {
        //credential.selectedAccount=account
        return Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(context.getString(R.string.app_name))
            .build()
    }

    /**
     * Creates a file in the user's My Drive folder based on a local File and returns its file ID.
     */
    fun uploadToDrive(file: java.io.File): Task<String?> {
        return Tasks.call(executor) {
            val driveFile: File = File()
                .setParents(Collections.singletonList("root"))
                .setMimeType("text/plain")
                .setName(file.name)
            val content = InputStreamContent(null, file.inputStream())
            val googleFile: File = drive?.files()?.create(
                driveFile, content
            )?.execute() ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        }
    }

    /**
     * Get the file identified by `fileId` and download its content into a local folder
     */
    fun downloadFromDrive(driveFileId: String?, directoryFile: java.io.File): Task<*> {
        return Tasks.call(executor) {
            val driveFile: File = drive?.files()?.get(driveFileId)?.execute()
                ?: throw Exception("Unable to open drive file.")
            val file = File(directoryFile, driveFile.name)
            val fileOutputStream = FileOutputStream(file)
            drive?.files()?.get(driveFileId)
                ?.executeMediaAndDownloadTo(fileOutputStream)
                ?: throw Exception("Unable to download drive file.")
        }
    }

}