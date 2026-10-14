package com.example.util

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.data.local.AppDatabase
import com.example.data.local.OfflineVideoEntity
import com.example.data.local.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class DownloadProgress(
    val videoId: String,
    val progress: Int, // 0 - 100
    val isDownloading: Boolean,
    val error: String? = null
)

class OfflineVideoManager(
    private val context: Context,
    private val database: AppDatabase,
    val videoCacheRepository: com.example.data.repository.VideoCacheRepository = 
        com.example.data.repository.VideoCacheRepository(database.cachedVideoDao())
) {
    companion object {
        const val MAX_OFFLINE_VIDEOS = 20
    }

    private val _downloadProgressMap = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadProgressMap: StateFlow<Map<String, DownloadProgress>> = _downloadProgressMap.asStateFlow()

    private val offlineDir: File by lazy {
        val dir = File(context.filesDir, "offline_videos")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    fun getAvailableStorageBytes(): Long {
        return try {
            val stat = StatFs(context.filesDir.path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            1024L * 1024L * 1024L * 2 // Fallback estimate ~2GB
        }
    }

    fun getUsedStorageBytes(): Long {
        return try {
            offlineDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun downloadVideo(video: VideoEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val currentCount = database.offlineVideoDao().getOfflineVideo(video.id)
        if (currentCount != null) {
            return@withContext Result.success(Unit) // Already downloaded
        }

        // Check 20-video limit
        val existing = database.videoDao().getVideoById(video.id)
        val allOffline = database.offlineVideoDao().getOfflineVideo(video.id)
        // Check total count
        var totalOffline = 0
        try {
            // Count existing files
            totalOffline = offlineDir.listFiles()?.filter { it.extension == "mp4" }?.size ?: 0
        } catch (_: Exception) {}

        if (totalOffline >= MAX_OFFLINE_VIDEOS) {
            return@withContext Result.failure(
                IllegalStateException("Limite atingido! Já tens 20 vídeos guardados offline. Remove um vídeo para descarregar outro.")
            )
        }

        // Check storage space
        val availableBytes = getAvailableStorageBytes()
        if (availableBytes < 50 * 1024 * 1024) { // Less than 50MB
            return@withContext Result.failure(
                IllegalStateException("Espaço de armazenamento insuficiente no telemóvel!")
            )
        }

        // Start download with temp file
        val tempFile = File(offlineDir, "temp_${video.id}.part")
        val finalFile = File(offlineDir, "${video.id}.mp4")

        updateProgress(video.id, 0, true, null)

        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val url = URL(video.videoUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 30000
                instanceFollowRedirects = true
                connect()
            }

            if (connection.responseCode !in 200..299) {
                updateProgress(video.id, 0, false, "Erro do servidor: ${connection.responseCode}")
                return@withContext Result.failure(Exception("HTTP error ${connection.responseCode}"))
            }

            val fileLength = connection.contentLength
            inputStream = connection.inputStream
            outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(8192)
            var totalBytesRead = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (fileLength > 0) {
                    val percent = ((totalBytesRead * 100) / fileLength).toInt().coerceIn(0, 99)
                    updateProgress(video.id, percent, true, null)
                }
            }

            outputStream.flush()
            outputStream.close()
            outputStream = null

            // Atomic rename to final file only upon 100% completion
            if (tempFile.exists()) {
                if (finalFile.exists()) finalFile.delete()
                tempFile.renameTo(finalFile)
            }

            val downloadedBytes = finalFile.length()

            // Update database
            val offlineEntity = OfflineVideoEntity(
                videoId = video.id,
                title = video.title,
                description = video.description,
                localFilePath = finalFile.absolutePath,
                authorName = video.authorName,
                authorHandle = video.authorHandle,
                thumbnailUrl = video.thumbnailUrl,
                fileSizeBytes = downloadedBytes,
                downloadedAt = System.currentTimeMillis()
            )
            database.offlineVideoDao().insertOfflineVideo(offlineEntity)
            database.videoDao().setOfflineStatus(video.id, true, finalFile.absolutePath)

            // Cache metadata and local file path in Room via VideoCacheRepository
            videoCacheRepository.cacheVideo(video, finalFile.absolutePath, downloadedBytes)

            updateProgress(video.id, 100, false, null)
            Result.success(Unit)
        } catch (e: Exception) {
            tempFile.delete()
            updateProgress(video.id, 0, false, e.localizedMessage ?: "Erro na transferência")
            Result.failure(e)
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
                connection?.disconnect()
            } catch (_: Exception) {}
        }
    }

    suspend fun removeOfflineVideo(videoId: String) = withContext(Dispatchers.IO) {
        val file = File(offlineDir, "$videoId.mp4")
        if (file.exists()) {
            file.delete()
        }
        database.offlineVideoDao().deleteOfflineVideo(videoId)
        database.videoDao().setOfflineStatus(videoId, false, null)
        videoCacheRepository.evictVideo(videoId)
    }

    private fun updateProgress(videoId: String, progress: Int, isDownloading: Boolean, error: String?) {
        val current = _downloadProgressMap.value.toMutableMap()
        current[videoId] = DownloadProgress(videoId, progress, isDownloading, error)
        _downloadProgressMap.value = current
    }
}
