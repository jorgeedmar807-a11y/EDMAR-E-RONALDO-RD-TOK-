package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val localFilePath: String? = null,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val isVerified: Boolean = false,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val favoritesCount: Int = 0,
    val viewsCount: Int = 0,
    val musicTitle: String = "Som Original - Edmar & Ronaldo",
    val musicAuthor: String = "EDMAR E RONALDO TOK",
    val category: String = "Para Ti",
    val hashtags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isLikedByMe: Boolean = false,
    val isFavoritedByMe: Boolean = false,
    val isFollowingAuthor: Boolean = false,
    val isOfflineAvailable: Boolean = false,
    val fileSizeMb: Double = 12.5
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val likesReceived: Int = 0,
    val isCurrentUser: Boolean = false,
    val isFollowing: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val userName: String,
    val userHandle: String,
    val userAvatar: String,
    val content: String,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val recipientId: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean = true,
    val status: String = "SENT" // SENT, PENDING, FAILED
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val senderAvatar: String,
    val isRead: Boolean = false,
    val targetVideoId: String? = null
)

@Entity(tableName = "offline_videos")
data class OfflineVideoEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val description: String,
    val localFilePath: String,
    val authorName: String,
    val authorHandle: String,
    val thumbnailUrl: String,
    val fileSizeBytes: Long,
    val downloadedAt: Long = System.currentTimeMillis()
)

/**
 * Room Entity to cache video metadata and local file paths for offline viewing.
 */
@Entity(tableName = "cached_videos")
data class CachedVideoEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val description: String,
    val remoteVideoUrl: String,
    val localFilePath: String,
    val thumbnailUrl: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val isVerified: Boolean = false,
    val category: String = "Para Ti",
    val musicTitle: String = "Som Original - Edmar & Ronaldo",
    val musicAuthor: String = "EDMAR E RONALDO TOK",
    val fileSizeBytes: Long = 0L,
    val durationSeconds: Int = 0,
    val cachedAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis()
)

