package com.uzlov.dating.lavada.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.uzlov.dating.lavada.app.Constants
import com.uzlov.dating.lavada.data.data_sources.interfaces.IMessageDataSource
import com.uzlov.dating.lavada.domain.models.Chat
import com.uzlov.dating.lavada.domain.models.ChatMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Отправка и получение сообщений
 *
 */
class MessagesRepository @Inject constructor(mDatabase: FirebaseDatabase) : IMessageDataSource {

    private val ref = mDatabase.getReference(Constants.FIREBASE_PATH_CHATS)

    override suspend fun sendMessage(uidChat: String, message: ChatMessage) {
        ref.child(uidChat).setValue(message)
    }

    @ExperimentalCoroutinesApi
    override suspend fun observeMessages(uidChat: String) = callbackFlow {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.getValue(Chat::class.java)
                if (result != null) {
                    this@callbackFlow.trySendBlocking(result)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                    it.resumeWithException(error.toException())
                error.toException().printStackTrace()
            }
        }
        ref.child(uidChat).addValueEventListener(listener)
        awaitClose {
//            ref.child(uidChat).removeEventListener(listener)
        }

    }

    /**
     * Проверяет, существует ли чат с такими собеседниками
     * @param companionId  id собеседника
     * @param selfId собственный id
     */
    override suspend fun hasChat(companionId: String, selfId: String): Boolean {
        return suspendCoroutine { continuation ->
            ref.get()
                .addOnCompleteListener { task->
                    try {
                        val countChats = task.result.children.map { value ->
                            value.getValue(Chat::class.java) ?: Chat()
                        }.count {
                            it.members?.containsAll(listOf(companionId, selfId)) == true // because null safety =)
                        }
                        continuation.resumeWith(Result.success(countChats == 1))
                    } catch (e: Exception){
                        continuation.resumeWithException(e)
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    /**
     * Ищет чат с указанными собеседниками
     * @return Chat if exists
     * @throws RuntimeException if chat not found
     */
    override suspend fun getChat(companionId: String, selfId: String): Chat {
        return suspendCoroutine { continuation ->
            ref.get()
                .addOnCompleteListener { task->
                    try {
                        val chat = task.result.children.map { value ->
                            value.getValue(Chat::class.java)!!
                        }.firstOrNull {
                            it.members?.containsAll(listOf(companionId, selfId)) == true // because null safety =)
                        }
                        if (chat != null){
                            continuation.resumeWith(Result.success(chat))
                        } else {
                            throw RuntimeException("Chat not found!")
                        }
                    } catch (e: RuntimeException){
                        continuation.resumeWithException(e)
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun sendMessage(uidChat: String, message: Chat) {
        ref.child(uidChat).setValue(message)
    }

    override suspend fun getChats(userId: String): List<Chat> {
        return suspendCoroutine { continuation ->
            ref.get()
                .addOnCompleteListener { task ->
                    try {
                        val result: List<Chat> = task.result.children.map { value ->
                            value.getValue(Chat::class.java)!!
                        }.filter {
                            it.members?.contains(userId) ?: false
                        }
                        continuation.resumeWith(
                            Result.success(result)
                        )
                    } catch (e: Throwable) {
                        continuation.resumeWithException(e)
                    }
                }
        }
    }

    override fun createChat(selfId: String, companionId: String): String {
        val uid = UUID.randomUUID().toString()
        ref.child(uid).setValue(
            Chat(
                uuid = uid,
                members = arrayListOf(selfId, companionId),
                messages = arrayListOf()
            )
        )

        return uid
    }

    override suspend fun getChat(uid: String): Chat {
        return suspendCoroutine { continuation ->
            ref.child(uid).get().addOnSuccessListener {
                try {
                    val result = it.getValue<Chat>()
                    if (result != null) {
                        continuation.resumeWith(Result.success(result))
                    } else {
                        throw RuntimeException()
                    }
                } catch (e: Throwable) {
                    continuation.resumeWith(Result.failure(e))
                }
            }
        }
    }
}