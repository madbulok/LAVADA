package com.uzlov.dating.lavada.storage


import android.net.Uri
import android.util.Log
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

    override fun downloadVideo(videoPath: String) {
        storageRef.child(videoPath).downloadUrl.addOnSuccessListener {
            Log.d("DOWNLOAD", "Success")
        }.addOnFailureListener {
            Log.d("DOWNLOAD", "Error")
        }
    }
}