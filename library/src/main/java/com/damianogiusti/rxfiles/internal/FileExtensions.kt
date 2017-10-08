package com.damianogiusti.rxfiles.internal

import java.io.File

/**
 * Created by Damiano Giusti on 08/10/17.
 */

fun File.moveTo(destinationPath: String, overwrite: Boolean = false): File {
    val outputFile = File(destinationPath)

    if (outputFile.exists() && overwrite) {
        outputFile.delete()
    }

    outputFile.mkdirs()
    outputFile.createNewFile()
    copyTo(outputFile, overwrite = true)
    delete()
    return outputFile
}