package com.damianogiusti.rxfiles.internal

import io.reactivex.Scheduler

/**
 * Created by Damiano Giusti on 08/10/17.
 */

data class Configuration internal constructor(
        var cacheEnabled: Boolean,
        var downloadPath: String,
        var scheduler: Scheduler
)