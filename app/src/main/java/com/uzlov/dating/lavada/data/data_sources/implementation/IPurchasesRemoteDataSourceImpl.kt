package com.uzlov.dating.lavada.data.data_sources.implementation

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.data.data_sources.interfaces.IPurchasesDataSource
import com.uzlov.dating.lavada.domain.models.Purchase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class IPurchasesRemoteDataSourceImpl @Inject constructor(var database: FirebaseDatabase) :
    IPurchasesDataSource {

    private val ref = database.getReference(Constants.FIREBASE_PATH_PURCHASES)

    @ExperimentalCoroutinesApi
    override suspend fun getPurchases(uidUser: String): Flow<List<Purchase>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(Purchase::class.java)!!
                }.also { this@callbackFlow.trySendBlocking(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun putPurchase(purchase: Purchase) {

    }
}