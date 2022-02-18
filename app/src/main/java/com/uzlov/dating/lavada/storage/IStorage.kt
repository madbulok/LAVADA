package com.uzlov.dating.lavada.storage

import android.content.Context

interface IStorage {
    fun uploadVideo(videoPath: String, context: Context): String
    fun downloadVideo(videoPath: String)
}