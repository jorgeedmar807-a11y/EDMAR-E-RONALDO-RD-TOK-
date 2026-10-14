package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CachedVideoDao
import com.example.data.repository.VideoCacheRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VideoCacheRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var cachedVideoDao: CachedVideoDao
    private lateinit var repository: VideoCacheRepository
    private lateinit var context: Context
    private lateinit var testVideoFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cachedVideoDao = database.cachedVideoDao()
        repository = VideoCacheRepository(cachedVideoDao)

        // Create a temporary video file on disk
        testVideoFile = File(context.cacheDir, "test_video_1.mp4")
        testVideoFile.writeBytes(ByteArray(1024 * 50)) // 50 KB dummy video content
    }

    @After
    fun tearDown() {
        if (testVideoFile.exists()) {
            testVideoFile.delete()
        }
        database.close()
    }

    @Test
    fun testCacheVideoMetadataAndFilePath() = runBlocking {
        repository.cacheVideo(
            videoId = "vid_test_01",
            title = "Vídeo de Teste Offline",
            description = "Descrição detalhada do vídeo em cache",
            remoteVideoUrl = "https://example.com/videos/test.mp4",
            localFilePath = testVideoFile.absolutePath,
            thumbnailUrl = "https://example.com/thumb.jpg",
            authorId = "user_edmar",
            authorName = "Edmar 👑",
            authorHandle = "@edmar_official",
            authorAvatarUrl = "https://example.com/avatar.jpg",
            isVerified = true,
            category = "Dança",
            musicTitle = "Kuduro É Vida",
            musicAuthor = "Edmar & Ronaldo",
            fileSizeBytes = testVideoFile.length()
        )

        // Verify count
        val count = repository.cachedVideosCount.first()
        assertEquals(1, count)

        // Verify video is cached and returns local file path
        val isCached = repository.isVideoCached("vid_test_01")
        assertTrue(isCached)

        val retrievedPath = repository.getLocalVideoPath("vid_test_01")
        assertEquals(testVideoFile.absolutePath, retrievedPath)

        // Verify observed entity
        val entity = repository.observeCachedVideo("vid_test_01").first()
        assertNotNull(entity)
        assertEquals("Vídeo de Teste Offline", entity?.title)
        assertEquals(testVideoFile.absolutePath, entity?.localFilePath)
        assertEquals("Dança", entity?.category)
        assertTrue(entity?.isVerified == true)
    }

    @Test
    fun testEvictVideoRemovesDbRowAndFile() = runBlocking {
        repository.cacheVideo(
            videoId = "vid_to_evict",
            title = "Vídeo para Remover",
            description = "Teste de remoção de cache",
            remoteVideoUrl = "https://example.com/videos/evict.mp4",
            localFilePath = testVideoFile.absolutePath,
            thumbnailUrl = "https://example.com/thumb.jpg",
            authorId = "user_ronaldo",
            authorName = "Ronaldo RD 🔥",
            authorHandle = "@ronaldo_tok",
            authorAvatarUrl = "https://example.com/avatar.jpg",
            fileSizeBytes = testVideoFile.length()
        )

        assertTrue(repository.isVideoCached("vid_to_evict"))

        val evicted = repository.evictVideo("vid_to_evict")
        assertTrue(evicted)

        val countAfter = repository.cachedVideosCount.first()
        assertEquals(0, countAfter)

        assertFalse(repository.isVideoCached("vid_to_evict"))
        assertFalse(testVideoFile.exists())
    }

    @Test
    fun testStaleCacheEntryAutoDeletedIfFileMissing() = runBlocking {
        // Cache with a non-existent file path
        repository.cacheVideo(
            videoId = "vid_stale",
            title = "Vídeo Inexistente",
            description = "Arquivo não está no disco",
            remoteVideoUrl = "https://example.com/videos/stale.mp4",
            localFilePath = "/path/to/missing/file.mp4",
            thumbnailUrl = "https://example.com/thumb.jpg",
            authorId = "user_test",
            authorName = "Test User",
            authorHandle = "@test",
            authorAvatarUrl = "https://example.com/avatar.jpg"
        )

        // getLocalVideoPath should detect missing physical file and clean up
        val path = repository.getLocalVideoPath("vid_stale")
        assertNull(path)

        val isCached = repository.isVideoCached("vid_stale")
        assertFalse(isCached)
    }
}
