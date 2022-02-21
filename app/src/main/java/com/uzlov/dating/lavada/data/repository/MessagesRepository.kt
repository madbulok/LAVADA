package com.uzlov.dating.lavada.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.data.data_sources.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import java.lang.Exception
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Отправка и получение сообщений
 *
 */
class MessagesRepository(database: FirebaseDatabase) : IMessageDataSource {

    private val ref  by lazy { database.getReference(Constants.FIREBASE_PATH_CHATS) }

    override suspend fun sendMessage(uidChat: String, message: ChatMessage) {
        ref.child(uidChat).setValue(message)
    }

    override suspend fun observeMessages(uidChat: String): ChatMessage {
        return suspendCoroutine {
            ref.child(uidChat).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.getValue(ChatMessage::class.java)
                    if (result != null) {
                        it.resumeWith(Result.success(result))
                    } else {
                        it.resumeWithException(Exception("Firebase send empty result!"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    it.resumeWithException(error.toException())
                }
            })
        }
    }

    override suspend fun getChats(userId: String): List<Chat> {
        return suspendCoroutine { continuation ->
            ref.get()
                .addOnCompleteListener { task ->
                    try {
                        continuation.resumeWith(
                            Result.success(
                                task.result.children.map { value ->
                                    value.getValue(Chat::class.java)!!
                                }
                            )
                        )
                    } catch (e: Throwable) {
                        continuation.resumeWith(Result.failure(e))
                    }
                }.addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }
        }
    }
}