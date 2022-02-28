package com.uzlov.dating.lavada.storage

import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

interface IStorage {
    fun uploadVideo(videoPath: String): Pair<UploadTask, StorageReference>
    fun downloadVideo(videoPath: String)
}