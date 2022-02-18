package com.uzlov.dating.lavada.storage


import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uzlov.dating.lavada.domain.models.User
import java.io.File
import javax.inject.Inject

class FirebaseStorageService @Inject constructor(val storage: FirebaseStorage) : IStorage {

    var storageRef = storage.reference

    lateinit var uploadTask: UploadTask
    lateinit var user: User


    override fun uploadVideo(videoPath: String, context:Context): String {

        val file = Uri.fromFile(File(videoPath))
        val videoRef = storageRef.child("video/${file.lastPathSegment}")
        uploadTask = videoRef.putFile(file)
        uploadTask.addOnFailureListener {
            Log.d("UPLOAD", "Error")
        }.addOnSuccessListener {
            Log.d("UPLOAD", "Success")
            Toast.makeText(context, "Загрузка вашего видео прошла успешно", Toast.LENGTH_SHORT).show()
        }
        return videoRef.toString()
    }

    override fun downloadVideo(videoPath: String) {
        storageRef.child(videoPath).downloadUrl.addOnSuccessListener {
            Log.d("DOWNLOAD", "Success")
        }.addOnFailureListener {
            Log.d("DOWNLOAD", "Error")
        }
    }
}