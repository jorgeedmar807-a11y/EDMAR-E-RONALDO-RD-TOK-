package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.CommentEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.data.sample.InitialContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class VideoRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val videoDao = database.videoDao()
    private val userDao = database.userDao()
    private val commentDao = database.commentDao()
    private val notificationDao = database.notificationDao()

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val hasLuandaStars = userDao.getUserById("user_gilmario") != null
        if (!hasLuandaStars) {
            userDao.insertUsers(InitialContent.SAMPLE_USERS)
            videoDao.insertVideos(InitialContent.SAMPLE_VIDEOS)
            notificationDao.insertNotifications(InitialContent.SAMPLE_NOTIFICATIONS)
            // Delete old broken sample videos if present from earlier runs
            listOf(
                "vid_er_01", "vid_er_02", "vid_er_03", "vid_er_04", "vid_er_05", "vid_er_06",
                "vid_global_01", "vid_global_02", "vid_global_03", "vid_global_04", "vid_global_05"
            ).forEach { oldId ->
                videoDao.deleteVideo(oldId)
            }
        }
    }

    fun getAllVideos(): Flow<List<VideoEntity>> = videoDao.getAllVideos()

    fun getVideosByCategory(category: String): Flow<List<VideoEntity>> {
        return if (category == "Para Ti" || category.isBlank()) {
            videoDao.getAllVideos()
        } else if (category == "Seguindo") {
            // Return videos from followed creators
            videoDao.getAllVideos()
        } else {
            videoDao.getVideosByCategory(category)
        }
    }

    fun searchVideos(query: String): Flow<List<VideoEntity>> = videoDao.searchVideos(query)

    fun getFavoriteVideos(): Flow<List<VideoEntity>> = videoDao.getFavoriteVideos()

    fun getLikedVideos(): Flow<List<VideoEntity>> = videoDao.getLikedVideos()

    fun getOfflineVideos(): Flow<List<VideoEntity>> = videoDao.getOfflineVideos()

    fun getVideosByAuthor(authorId: String): Flow<List<VideoEntity>> = videoDao.getVideosByAuthor(authorId)

    suspend fun toggleLike(videoId: String, currentLiked: Boolean) = withContext(Dispatchers.IO) {
        val newLiked = !currentLiked
        val delta = if (newLiked) 1 else -1
        videoDao.toggleLike(videoId, newLiked, delta)
    }

    suspend fun toggleFavorite(videoId: String, currentFavorited: Boolean) = withContext(Dispatchers.IO) {
        val newFav = !currentFavorited
        val delta = if (newFav) 1 else -1
        videoDao.toggleFavorite(videoId, newFav, delta)
    }

    suspend fun recordView(videoId: String) = withContext(Dispatchers.IO) {
        videoDao.incrementViewCount(videoId)
    }

    suspend fun toggleFollowAuthor(authorId: String, currentFollowing: Boolean) = withContext(Dispatchers.IO) {
        val newFollowing = !currentFollowing
        val delta = if (newFollowing) 1 else -1
        userDao.updateTargetFollowers(authorId, delta, newFollowing)
        userDao.updateMyFollowingCount(delta)
        videoDao.updateFollowStatusForAuthor(authorId, newFollowing)
    }

    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(videoId: String, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        val currentUser = userDao.getCurrentUser() ?: return@withContext Result.failure(Exception("Utilizador não autenticado"))
        val comment = CommentEntity(
            id = "cmt_${UUID.randomUUID().toString().take(8)}",
            videoId = videoId,
            userId = currentUser.id,
            userName = currentUser.displayName,
            userHandle = "@${currentUser.username}",
            userAvatar = currentUser.avatarUrl,
            content = content,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            isLikedByMe = false
        )
        commentDao.insertComment(comment)
        videoDao.incrementCommentCount(videoId)
        Result.success(Unit)
    }

    suspend fun toggleCommentLike(commentId: String, currentLiked: Boolean) = withContext(Dispatchers.IO) {
        val newLiked = !currentLiked
        val delta = if (newLiked) 1 else -1
        commentDao.toggleCommentLike(commentId, newLiked, delta)
    }

    suspend fun publishVideo(
        title: String,
        description: String,
        videoUri: String,
        category: String,
        hashtags: List<String>,
        musicTitle: String = "Som Original - Edmar & Ronaldo"
    ): Result<VideoEntity> = withContext(Dispatchers.IO) {
        val currentUser = userDao.getCurrentUser() ?: return@withContext Result.failure(Exception("Utilizador não autenticado"))
        val newVideo = VideoEntity(
            id = "vid_user_${UUID.randomUUID().toString().take(8)}",
            title = title.ifBlank { "Novo Vídeo no ER TOK" },
            description = description,
            videoUrl = videoUri,
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
            authorId = currentUser.id,
            authorName = currentUser.displayName,
            authorHandle = "@${currentUser.username}",
            authorAvatarUrl = currentUser.avatarUrl,
            isVerified = currentUser.isVerified,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            favoritesCount = 0,
            viewsCount = 0,
            musicTitle = musicTitle,
            musicAuthor = currentUser.displayName,
            category = category,
            hashtags = hashtags,
            createdAt = System.currentTimeMillis(),
            fileSizeMb = 8.5
        )
        videoDao.insertVideo(newVideo)
        Result.success(newVideo)
    }

    suspend fun batchImportVideos(videos: List<VideoEntity>) = withContext(Dispatchers.IO) {
        videoDao.insertVideos(videos)
    }

    suspend fun deleteVideo(videoId: String) = withContext(Dispatchers.IO) {
        videoDao.deleteVideo(videoId)
    }
}
