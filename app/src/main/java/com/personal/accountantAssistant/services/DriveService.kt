package com.personal.accountantAssistant.services

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.model.File
import java.io.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveService {

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
            val googleFile: File = SignInService.drive?.files()?.create(
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
            val driveFile: File = SignInService.drive?.files()?.get(driveFileId)?.execute()
                ?: throw Exception("Unable to open drive file.")
            val file = File(directoryFile, driveFile.name)
            val fileOutputStream = FileOutputStream(file)
            SignInService.drive?.files()?.get(driveFileId)
                ?.executeMediaAndDownloadTo(fileOutputStream)
                ?: throw Exception("Unable to download drive file.")
        }
    }

    companion object {
        private val executor: Executor = Executors.newSingleThreadExecutor()
    }
}