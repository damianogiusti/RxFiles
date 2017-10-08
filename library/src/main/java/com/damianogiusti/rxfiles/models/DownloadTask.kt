package com.damianogiusti.rxfiles.models

import java.util.*

/**
 * Created by Damiano Giusti on 08/10/17.
 */

data class DownloadTask(
        val downloadId: UUID,
        val downloadRemoteUrl: String,
        var downloadTotalSizeBytes: Long = 0L
) {
    lateinit var downloadLocalUrl: String
}