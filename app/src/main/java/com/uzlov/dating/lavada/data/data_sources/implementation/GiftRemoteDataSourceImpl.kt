package com.uzlov.dating.lavada.data.data_sources.implementation

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.data.data_sources.interfaces.IGiftsDataSource
import com.uzlov.dating.lavada.domain.models.CategoryGifts
import com.uzlov.dating.lavada.retrofit.RemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GiftRemoteDataSourceImpl @Inject constructor(database: FirebaseDatabase, val remoteDataSource: RemoteDataSource) :
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

    override suspend fun sendGift(token: String, map: Map<String, String>) {
        remoteDataSource.sendGift(token, map)
    }

    override suspend fun getALlGifts(token: String) {
        remoteDataSource.getALlGifts(token)
    }

    override suspend fun postPurchase(token: String, map: Map<String, String>) {
        remoteDataSource.postPurchase(token, map)
    }

    override suspend fun getListGifts(
        token: String,
        limit: String,
        offset: String,
        status: String
    ) {
        remoteDataSource.getListGifts(token, limit, offset, status)
    }

    override suspend fun getListReceivedGifts(token: String, limit: String, offset: String) {
        remoteDataSource.getListReceivedGifts(token, limit, offset)
    }
}