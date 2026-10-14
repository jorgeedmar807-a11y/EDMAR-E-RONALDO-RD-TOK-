package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY createdAt DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE category = :category ORDER BY createdAt DESC")
    fun getVideosByCategory(category: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getVideosByAuthor(authorId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isFavoritedByMe = 1 ORDER BY createdAt DESC")
    fun getFavoriteVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLikedByMe = 1 ORDER BY createdAt DESC")
    fun getLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isOfflineAvailable = 1 ORDER BY createdAt DESC")
    fun getOfflineVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR authorName LIKE '%' || :query || '%' OR authorHandle LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR hashtags LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("UPDATE videos SET likesCount = likesCount + :delta, isLikedByMe = :isLiked WHERE id = :videoId")
    suspend fun toggleLike(videoId: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE videos SET favoritesCount = favoritesCount + :delta, isFavoritedByMe = :isFav WHERE id = :videoId")
    suspend fun toggleFavorite(videoId: String, isFav: Boolean, delta: Int)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementViewCount(videoId: String)

    @Query("UPDATE videos SET commentsCount = commentsCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentCount(videoId: String)

    @Query("UPDATE videos SET isFollowingAuthor = :isFollowing WHERE authorId = :authorId")
    suspend fun updateFollowStatusForAuthor(authorId: String, isFollowing: Boolean)

    @Query("UPDATE videos SET isOfflineAvailable = :isOffline, localFilePath = :path WHERE id = :videoId")
    suspend fun setOfflineStatus(videoId: String, isOffline: Boolean, path: String?)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY followersCount DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET followingCount = followingCount + :delta WHERE isCurrentUser = 1")
    suspend fun updateMyFollowingCount(delta: Int)

    @Query("UPDATE users SET followersCount = followersCount + :delta, isFollowing = :isFollowing WHERE id = :userId")
    suspend fun updateTargetFollowers(userId: String, delta: Int, isFollowing: Boolean)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE comments SET likesCount = likesCount + :delta, isLikedByMe = :isLiked WHERE id = :commentId")
    suspend fun toggleCommentLike(commentId: String, isLiked: Boolean, delta: Int)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages GROUP BY conversationId ORDER BY timestamp DESC")
    fun getLatestConversations(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface OfflineVideoDao {
    @Query("SELECT * FROM offline_videos ORDER BY downloadedAt DESC")
    fun getAllOfflineVideos(): Flow<List<OfflineVideoEntity>>

    @Query("SELECT COUNT(*) FROM offline_videos")
    fun getOfflineCount(): Flow<Int>

    @Query("SELECT * FROM offline_videos WHERE videoId = :videoId LIMIT 1")
    suspend fun getOfflineVideo(videoId: String): OfflineVideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineVideo(video: OfflineVideoEntity)

    @Query("DELETE FROM offline_videos WHERE videoId = :videoId")
    suspend fun deleteOfflineVideo(videoId: String)
}

@Dao
interface CachedVideoDao {
    @Query("SELECT * FROM cached_videos ORDER BY cachedAt DESC")
    fun getAllCachedVideos(): Flow<List<CachedVideoEntity>>

    @Query("SELECT * FROM cached_videos WHERE videoId = :videoId LIMIT 1")
    fun observeCachedVideoById(videoId: String): Flow<CachedVideoEntity?>

    @Query("SELECT * FROM cached_videos WHERE videoId = :videoId LIMIT 1")
    suspend fun getCachedVideoById(videoId: String): CachedVideoEntity?

    @Query("SELECT localFilePath FROM cached_videos WHERE videoId = :videoId LIMIT 1")
    suspend fun getLocalFilePath(videoId: String): String?

    @Query("SELECT COUNT(*) > 0 FROM cached_videos WHERE videoId = :videoId")
    suspend fun isVideoCached(videoId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cachedVideo: CachedVideoEntity)

    @Query("UPDATE cached_videos SET localFilePath = :localFilePath, fileSizeBytes = :fileSizeBytes, lastAccessedAt = :timestamp WHERE videoId = :videoId")
    suspend fun updateLocalFilePath(videoId: String, localFilePath: String, fileSizeBytes: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE cached_videos SET lastAccessedAt = :timestamp WHERE videoId = :videoId")
    suspend fun markLastAccessed(videoId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM cached_videos WHERE videoId = :videoId")
    suspend fun deleteCachedVideo(videoId: String)

    @Query("DELETE FROM cached_videos")
    suspend fun clearAllCache()

    @Query("SELECT COUNT(*) FROM cached_videos")
    fun getCachedVideosCount(): Flow<Int>

    @Query("SELECT SUM(fileSizeBytes) FROM cached_videos")
    fun getTotalCacheSizeBytes(): Flow<Long?>
}

