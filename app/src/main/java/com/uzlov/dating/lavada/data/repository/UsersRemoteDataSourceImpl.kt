package com.uzlov.dating.lavada.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.uzlov.dating.lavada.data.data_sources.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.User

class UsersRemoteDataSourceImpl : IRemoteDataSource {

    private val mDatabase by lazy {
        FirebaseDatabase.getInstance("https://lavada-7777-default-rtdb.europe-west1.firebasedatabase.app/")
    }
    private val userReference = mDatabase.getReference("Users")

    private val allUsers = MutableLiveData<List<User>>()
    private val user = MutableLiveData<User>()

    override fun getUsers(): LiveData<List<User>> {
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lis = snapshot.children.map {
                    it.getValue<User>()!!
                }
                allUsers.value = lis
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })

        return allUsers
    }

    override fun getUser(id: String): LiveData<User?> {
        userReference.child(id).get().addOnSuccessListener {
            user.value = it.getValue<User>()
        }
        return user
    }

    override fun putUser(user: User) {
        userReference.child(user.id).setValue(user)
    }

    override fun getUsersWithUserID(id: String): LiveData<List<User>> {
        val result = MutableLiveData<List<User>>()
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    result.value = snapshot.children.map {
                        it.getValue<User>()!!
                    }.filter {
                        it.id == id
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
        return result
    }

    override fun removeUser(id: String) {
        userReference.child(id).removeValue()
    }


}