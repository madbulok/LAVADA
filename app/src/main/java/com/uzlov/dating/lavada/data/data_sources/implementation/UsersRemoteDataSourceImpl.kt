package com.uzlov.dating.lavada.data.data_sources.implementation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.uzlov.dating.lavada.data.data_sources.interfaces.IRemoteDataSource
import com.uzlov.dating.lavada.domain.models.RemoteUser
import com.uzlov.dating.lavada.domain.models.User
import com.uzlov.dating.lavada.retrofit.RemoteDataSource
import com.uzlov.dating.lavada.service.MatchesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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

    override suspend fun getUser(id: String): User? {
        return suspendCoroutine { continuation ->
            userReference.child(id)
                .get()
                .addOnSuccessListener {
                    try {
                        continuation.resumeWith(Result.success(it.getValue<User>()))
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override fun putUser(user: User) {
        user.uid.let { userReference.child(it).setValue(user) }
    }

    override fun getUsersWithUserID(id: String): LiveData<List<User>> {
        val result = MutableLiveData<List<User>>()
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    result.value = snapshot.children.map {
                        it.getValue<User>()!!
                    }.filter {
                        it.uid == id
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
        return result
    }

    override fun updateUser(id: String, field: String, value: Any) {
        userReference.child(id).child(field).setValue(value)
    }

    override fun observeMatches(uid: String, matchesCallback: MatchesService.MatchesStateListener) {

        /* тестовое что-то чтобы проверить работоспособность */
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lis = snapshot.children.map {
                    it.getValue<User>()!!
                }
                for (list in lis) {
                    if (list.matches[uid] == true) {
                        matchesCallback.mutualMatch(list)
                    }
                    if (list.matches[uid] == false ){
                        matchesCallback.match(list)
                    }
                }

                allUsers.value = lis
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }

    override suspend fun getRemoteUsers() {
        RemoteDataSource().getData()
//        getData().enqueue(object : Callback<List<RemoteUser>?> {
//            override fun onResponse(
//                call: Call<List<RemoteUser>?>,
//                response: Response<List<RemoteUser>?>
//
//            ) {
//                Log.d("CORRECT", response.body().toString())
//            }
//
//            override fun onFailure(call: Call<List<RemoteUser>?>, t: Throwable) {
//                Log.d("NO_CORRECT", "fail((((((((((")
//            }
//        })
    }

    override fun removeUser(id: String) {
        userReference.child(id).removeValue()
    }
}
