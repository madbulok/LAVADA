package com.uzlov.dating.lavada.data.data_sources.implementation

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GiftRemoteDataSourceImpl @Inject constructor(var database: FirebaseDatabase) :
    IGiftsDataSource {

    private val ref = database.getReference(Constants.FIREBASE_PATH_GIFTS)


    @ExperimentalCoroutinesApi
    override suspend fun getCategoryGifts(): Flow<List<CategoryGifts>> = callbackFlow {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(CategoryGifts::class.java)!!
                }.also { this@callbackFlow.trySendBlocking(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getCategoryByID(id: String): Flow<CategoryGifts?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(CategoryGifts::class.java)?.let { this@callbackFlow.trySendBlocking(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }
        ref.child(id).addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}