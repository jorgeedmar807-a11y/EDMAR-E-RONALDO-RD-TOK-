package com.example.data.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val likesReceived: Int = 0,
    val isFollowing: Boolean = false
)

data class VideoItem(
    val id: String,
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

data class CommentItem(
    val id: String,
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

data class MessageItem(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val recipientId: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean = true,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    FAILED
}

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val description: String,
    val timestamp: Long,
    val senderAvatar: String,
    val isRead: Boolean = false,
    val targetVideoId: String? = null
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    SYSTEM,
    LIVE
}
