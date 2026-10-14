package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()

    fun getCurrentUserFlow(): Flow<UserEntity?> = userDao.getCurrentUserFlow()

    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getCurrentUser()
    }

    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun updateProfile(displayName: String, bio: String, avatarUrl: String? = null) = withContext(Dispatchers.IO) {
        val current = userDao.getCurrentUser() ?: return@withContext
        val updated = current.copy(
            displayName = displayName,
            bio = bio,
            avatarUrl = avatarUrl ?: current.avatarUrl
        )
        userDao.updateUser(updated)
    }
}
