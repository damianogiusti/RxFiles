package com.damianogiusti.rxfiles

import android.content.Context
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Damiano Giusti on 08/10/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class RxFilesTest {

    @Mock lateinit var context: Context
    @Mock lateinit var downloadService: DownloadService
    @Mock lateinit var uploadService: UploadService


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun filePathForRemoteUrl() {
        val rxFiles = RxFiles(context, downloadService, uploadService)
        print(rxFiles.configuration.downloadPath)
    }

}