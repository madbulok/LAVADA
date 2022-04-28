package com.uzlov.dating.lavada.storage


interface IStorage {
    fun uploadVideo(videoPath: String)
    fun uploadPhoto(photoPath: String)
    fun downloadVideo(videoPath: String)
}