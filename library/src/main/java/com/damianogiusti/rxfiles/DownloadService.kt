package com.damianogiusti.rxfiles

import io.reactivex.Flowable
import io.reactivex.Single
import java.io.File

/**
 * Created by Damiano Giusti on 08/10/17.
 */
interface DownloadService {

    fun downloadFromUrl(url: String): Single<File>
}