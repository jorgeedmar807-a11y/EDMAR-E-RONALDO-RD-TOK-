package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.MessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class MessageRepository(private val database: AppDatabase) {
    private val messageDao = database.messageDao()
    private val userDao = database.userDao()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(conversationId)

    fun getConversations(): Flow<List<MessageEntity>> =
        messageDao.getLatestConversations()

    suspend fun sendMessage(
        conversationId: String,
        recipientId: String,
        text: String
    ): Result<MessageEntity> = withContext(Dispatchers.IO) {
        val currentUser = userDao.getCurrentUser() ?: return@withContext Result.failure(Exception("Utilizador não autenticado"))
        val msg = MessageEntity(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = currentUser.id,
            senderName = currentUser.displayName,
            senderAvatar = currentUser.avatarUrl,
            recipientId = recipientId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isMe = true,
            status = "SENT"
        )
        messageDao.insertMessage(msg)
        Result.success(msg)
    }

    suspend fun seedInitialMessagesIfEmpty() = withContext(Dispatchers.IO) {
        val user = userDao.getCurrentUser() ?: return@withContext
        val creator = userDao.getUserById("user_brasil") ?: return@withContext
        val convId = "conv_brasil"
        val welcome = MessageEntity(
            id = "msg_welcome_brasil",
            conversationId = convId,
            senderId = creator.id,
            senderName = creator.displayName,
            senderAvatar = creator.avatarUrl,
            recipientId = user.id,
            text = "Olá! Bem-vindo à nossa comunidade global de vídeos! 🇧🇷🚀 Se quiseres gravar um dueto ou trocar ideias sobre dança e música, manda mensagem aqui!",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3,
            isMe = false,
            status = "DELIVERED"
        )
        messageDao.insertMessage(welcome)
    }
}
