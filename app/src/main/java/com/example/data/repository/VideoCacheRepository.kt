package com.example.data.repository

import com.example.data.local.CachedVideoDao
import com.example.data.local.CachedVideoEntity
import com.example.data.local.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository to manage locally cached video metadata and video file paths for offline viewing support.
 * Implements clean architecture and the repository pattern to abstract DAO operations.
 */
class VideoCacheRepository(
    private val cachedVideoDao: CachedVideoDao
) {
    /**
     * Flow of all locally cached videos ordered by cache timestamp descending.
     */
    val cachedVideos: Flow<List<CachedVideoEntity>> = cachedVideoDao.getAllCachedVideos()

    /**
     * Reactive count of cached videos.
     */
    val cachedVideosCount: Flow<Int> = cachedVideoDao.getCachedVideosCount()

    /**
     * Total storage size in megabytes occupied by cached videos.
     */
    val totalCacheSizeMb: Flow<Double> = cachedVideoDao.getTotalCacheSizeBytes().map { bytes ->
        (bytes ?: 0L) / (1024.0 * 1024.0)
    }

    /**
     * Caches metadata and local file path for a video.
     */
    suspend fun cacheVideo(
        videoId: String,
        title: String,
        description: String,
        remoteVideoUrl: String,
        localFilePath: String,
        thumbnailUrl: String,
        authorId: String,
        authorName: String,
        authorHandle: String,
        authorAvatarUrl: String,
        isVerified: Boolean = false,
        category: String = "Para Ti",
        musicTitle: String = "Som Original - Edmar & Ronaldo",
        musicAuthor: String = "EDMAR E RONALDO TOK",
        fileSizeBytes: Long = 0L,
        durationSeconds: Int = 0
    ) = withContext(Dispatchers.IO) {
        val entity = CachedVideoEntity(
            videoId = videoId,
            title = title,
            description = description,
            remoteVideoUrl = remoteVideoUrl,
            localFilePath = localFilePath,
            thumbnailUrl = thumbnailUrl,
            authorId = authorId,
            authorName = authorName,
            authorHandle = authorHandle,
            authorAvatarUrl = authorAvatarUrl,
            isVerified = isVerified,
            category = category,
            musicTitle = musicTitle,
            musicAuthor = musicAuthor,
            fileSizeBytes = fileSizeBytes,
            durationSeconds = durationSeconds,
            cachedAt = System.currentTimeMillis(),
            lastAccessedAt = System.currentTimeMillis()
        )
        cachedVideoDao.insertOrUpdate(entity)
    }

    /**
     * Convenience method to cache metadata directly from a VideoEntity along with local file details.
     */
    suspend fun cacheVideo(video: VideoEntity, localFilePath: String, fileSizeBytes: Long) = withContext(Dispatchers.IO) {
        cacheVideo(
            videoId = video.id,
            title = video.title,
            description = video.description,
            remoteVideoUrl = video.videoUrl,
            localFilePath = localFilePath,
            thumbnailUrl = video.thumbnailUrl,
            authorId = video.authorId,
            authorName = video.authorName,
            authorHandle = video.authorHandle,
            authorAvatarUrl = video.authorAvatarUrl,
            isVerified = video.isVerified,
            category = video.category,
            musicTitle = video.musicTitle,
            musicAuthor = video.musicAuthor,
            fileSizeBytes = fileSizeBytes
        )
    }

    /**
     * Retrieves the local cached file path for a video if it exists on disk.
     * Updates the lastAccessedAt timestamp to record viewing activity.
     */
    suspend fun getLocalVideoPath(videoId: String): String? = withContext(Dispatchers.IO) {
        val cached = cachedVideoDao.getCachedVideoById(videoId) ?: return@withContext null
        val file = File(cached.localFilePath)
        if (file.exists() && file.length() > 0) {
            cachedVideoDao.markLastAccessed(videoId)
            cached.localFilePath
        } else {
            // Remove stale cache entry if file was deleted from filesystem
            cachedVideoDao.deleteCachedVideo(videoId)
            null
        }
    }

    /**
     * Checks whether a video is cached and ready for offline playback.
     */
    suspend fun isVideoCached(videoId: String): Boolean = withContext(Dispatchers.IO) {
        getLocalVideoPath(videoId) != null
    }

    /**
     * Observes a specific cached video reactively.
     */
    fun observeCachedVideo(videoId: String): Flow<CachedVideoEntity?> {
        return cachedVideoDao.observeCachedVideoById(videoId)
    }

    /**
     * Removes a cached video entry from Room and deletes its local file from disk.
     */
    suspend fun evictVideo(videoId: String): Boolean = withContext(Dispatchers.IO) {
        val cached = cachedVideoDao.getCachedVideoById(videoId)
        if (cached != null) {
            try {
                val file = File(cached.localFilePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) {}
            cachedVideoDao.deleteCachedVideo(videoId)
            true
        } else {
            false
        }
    }

    /**
     * Clears all cached videos from Room database.
     */
    suspend fun clearAllCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            cachedVideoDao.clearAllCache()
            true
        } catch (_: Exception) {
            false
        }
    }
}
