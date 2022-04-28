package com.uzlov.dating.lavada.data.data_sources.interfaces


interface IMessageDataSource {

    /**чаты*/
    suspend fun createMessage(token: String, map: Map<String, String>)
    suspend fun getMessage(token: String, chatId: String)
    suspend fun updateStatus(token: String, map: Map<String, String>)
    suspend fun getListChats(token: String, offset: String, limit: String)
    suspend fun createRemoteChat(token: String, map: Map<String, String>)
    suspend fun getChatById(token: String, chatId: String)
    suspend fun checkChat(token: String, firebaseUid: String)
}