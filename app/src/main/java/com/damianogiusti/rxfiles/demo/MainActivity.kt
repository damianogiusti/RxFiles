package com.damianogiusti.rxfiles.demo

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.damianogiusti.rxfiles.DownloadService
import com.damianogiusti.rxfiles.RxFiles
import com.damianogiusti.rxfiles.UploadService
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.downloadManager
import org.jetbrains.anko.info
import java.io.*

class MainActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileService = RxFiles(
                context = applicationContext,
                downloadService = AndroidDownloadService(applicationContext),
                uploadService = OkHttpUploadService()
        )

        fileService.remoteFile("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    info(it.downloadLocalUrl)
                }, {
                    it.printStackTrace()
                    error(it)
                })
    }
}

class AndroidDownloadService(val context: Context) : DownloadService {

    val downloadManager: DownloadManager = context.downloadManager

    override fun downloadFromUrl(url: String): Single<File> {
        return Single.create { emitter ->
            var downloadId: Long = Long.MIN_VALUE

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, intent: Intent?) {
                    val downloadedId = intent?.extras?.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                    if (downloadedId != null && downloadId == downloadedId) {

                        val uri = downloadManager.getUriForDownloadedFile(downloadedId)
                        val path = uri.getPath(context)
                        if (path != null) {
                            emitter.onSuccess(File(path))
                        } else {
                            emitter.onError(FileNotFoundException())
                        }
                    }
                }
            }
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

            val request = DownloadManager.Request(Uri.parse(url))
            downloadId = downloadManager.enqueue(request)

            emitter.setCancellable {
                context.unregisterReceiver(receiver)
                downloadManager.remove(downloadId)
            }
        }
    }
}

class OkHttpUploadService : UploadService {

}
