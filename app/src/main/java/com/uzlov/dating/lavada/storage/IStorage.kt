package com.uzlov.dating.lavada.storage

interface IStorage {
    fun uploadVideo(videoPath: String): String
    fun downloadVideo(videoPath: String)
}