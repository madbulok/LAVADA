package com.uzlov.dating.lavada.storage


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import javax.inject.Inject

class FirebaseStorageService @Inject constructor(val storage: FirebaseStorage) : IStorage {

    private var storageRef = storage.reference

    override fun uploadVideo(videoPath: String): Pair<UploadTask, StorageReference> {
        val file = Uri.fromFile(File(videoPath))
        val videoRef = storageRef.child("video/${file.lastPathSegment}")
        return Pair(videoRef.putFile(file), videoRef)
    }

    override fun uploadPhoto(photoPath: String): Pair<UploadTask, StorageReference> {
        val file = Uri.fromFile(File(photoPath))
        val photoRef = storageRef.child("photo/${file.lastPathSegment}")
        return Pair(photoRef.putFile(file), photoRef)
    }

    override fun downloadVideo(videoPath: String, context: Context, imageView: ImageView) {
        storageRef.child(videoPath).downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it.path)
                .into(imageView)
            Log.d("DOWNLOAD", it.path.toString())
        }.addOnFailureListener {
            Log.d("DOWNLOAD", "Error")
        }
    }
}