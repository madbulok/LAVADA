package com.uzlov.dating.lavada.storage

import android.content.Context
import android.widget.ImageView
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

interface IStorage {
    fun uploadVideo(videoPath: String): Pair<UploadTask, StorageReference>
    fun uploadPhoto(photoPath: String): Pair<UploadTask, StorageReference>
    fun downloadVideo(videoPath: String, context: Context, imageView: ImageView)
}