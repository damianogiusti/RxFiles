package com.damianogiusti.rxfiles

import android.content.Context
import android.net.Uri
import com.damianogiusti.rxfiles.internal.Configuration
import com.damianogiusti.rxfiles.internal.moveTo
import com.damianogiusti.rxfiles.models.DownloadTask
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.util.*

/**
 * Created by Damiano Giusti on 08/10/17.
 */

class RxFiles(
        context: Context,
        val downloadService: DownloadService,
        val uploadService: UploadService
) {

    val configuration = Configuration(
            cacheEnabled = true,
            downloadPath = context.applicationContext.externalCacheDir.absolutePath + "/RxFiles/",
            scheduler = Schedulers.io()
    )

    ///////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////

    fun filePathForRemoteUrl(url: String): String {
        val name = Uri.parse(url)?.lastPathSegment?.toString() ?: ""
        return configuration.downloadPath + name
    }

    /**
     * Obtains the file represented by the given remote URL.
     *
     * @param url URL of the file to obtain
     */
    fun remoteFile(url: String): Flowable<DownloadTask> {
        return Flowable.defer {

            // Create the download task that will handle this operation.
            val downloadTask = DownloadTask(
                    downloadId = UUID.randomUUID(),
                    downloadRemoteUrl = url
            )
            // Calculate the local download path.
            // If the file has already been downloaded, the download operation can be skipped
            // and the file can be returned immediately.

            val localPath = filePathForRemoteUrl(url)
            val localFile = File(localPath)
            val fileExists = localFile.exists() && localFile.isFile

            if (fileExists) {
                downloadTask.downloadLocalUrl = localPath
                Flowable.just(downloadTask)
            } else {

                // The file does not exist, so delegate the download to the DownloadService
                // implementation.

                downloadService.downloadFromUrl(url)
                        .map {
                            // Set the downloaded path and the file size into the DownloadTask
                            // object.
                            downloadTask.downloadLocalUrl = it.absolutePath
                            downloadTask.downloadTotalSizeBytes = it.totalSpace
                            downloadTask
                        }
                        .map {
                            // The downloaded path may not be the expected one since the
                            // DownloadService can have different implementations.
                            // Just move the file if the url does not match the expected one.
                            if (it.downloadLocalUrl != localPath) {
                                File(it.downloadLocalUrl).moveTo(destinationPath = localPath)
                            }
                            it
                        }
                        .toFlowable()
            }
        }.subscribeOn(configuration.scheduler)
    }

    /**
     * Obtains a local file represented by the given path.
     *
     * @param path Path to the file to load.
     */
    fun localFile(path: String): Single<File> {
        return Single.defer {

            val file = File(path)
            val fileExists = file.exists() && file.isFile

            if (fileExists) {
                Single.just(file)
            } else {
                Single.error(FileNotFoundException())
            }
        }.subscribeOn(configuration.scheduler)
    }
}