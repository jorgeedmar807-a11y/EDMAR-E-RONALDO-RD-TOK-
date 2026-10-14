package com.example

import com.example.data.sample.InitialContent
import com.example.util.OfflineVideoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOfflineMaxVideosQuotaIs20() {
        assertEquals(20, OfflineVideoManager.MAX_OFFLINE_VIDEOS)
    }

    @Test
    fun testInitialCategoriesContainMandatoryTopics() {
        val categories = InitialContent.CATEGORIES
        assertTrue(categories.contains("Para Ti"))
        assertTrue(categories.contains("Seguindo"))
        assertTrue(categories.contains("Luanda 🇦🇴"))
        assertTrue(categories.contains("Famosos Angola"))
        assertTrue(categories.contains("Kuduro"))
        assertTrue(categories.contains("Semba"))
        assertTrue(categories.contains("Dança"))
        assertTrue(categories.contains("Música"))
    }

    @Test
    fun testPreloadedCreatorsAreVerifiedAndPresent() {
        val users = InitialContent.SAMPLE_USERS
        val gilmario = users.firstOrNull { it.id == "user_gilmario" }
        val pretoshow = users.firstOrNull { it.id == "user_pretoshow" }
        val gerilson = users.firstOrNull { it.id == "user_gerilson" }

        assertTrue(gilmario != null && gilmario.isVerified)
        assertTrue(pretoshow != null && pretoshow.isVerified)
        assertTrue(gerilson != null && gerilson.isVerified)
        assertEquals("gilmariovemba", gilmario?.username)
        assertEquals("pretoshow", pretoshow?.username)
        assertEquals("gerilsoninsrael", gerilson?.username)
    }
}
