package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.local.VideoEntity
import com.example.data.repository.MessageRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OfflineScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.EdmarRonaldoTokTheme
import com.example.util.NetworkMonitor
import com.example.util.OfflineVideoManager

sealed class ActiveSubScreen {
    data object None : ActiveSubScreen()
    data class CreatorProfile(val userId: String) : ActiveSubScreen()
    data class DirectChat(val recipientId: String? = null) : ActiveSubScreen()
    data object Notifications : ActiveSubScreen()
    data object Admin : ActiveSubScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val videoCacheRepository = com.example.data.repository.VideoCacheRepository(database.cachedVideoDao())
        val videoRepository = VideoRepository(database, applicationContext)
        val userRepository = UserRepository(database)
        val messageRepository = MessageRepository(database)
        val offlineVideoManager = OfflineVideoManager(applicationContext, database, videoCacheRepository)
        val networkMonitor = NetworkMonitor(applicationContext)

        setContent {
            EdmarRonaldoTokTheme {
                MainApp(
                    database = database,
                    videoRepository = videoRepository,
                    userRepository = userRepository,
                    messageRepository = messageRepository,
                    offlineVideoManager = offlineVideoManager,
                    networkMonitor = networkMonitor
                )
            }
        }
    }
}

@Composable
fun MainApp(
    database: AppDatabase,
    videoRepository: VideoRepository,
    userRepository: UserRepository,
    messageRepository: MessageRepository,
    offlineVideoManager: OfflineVideoManager,
    networkMonitor: NetworkMonitor
) {
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var activeSubScreen by remember { mutableStateOf<ActiveSubScreen>(ActiveSubScreen.None) }

    val isOnline by networkMonitor.isOnline.collectAsState(initial = networkMonitor.isCurrentlyOnline())
    val offlineVideos by videoRepository.getOfflineVideos().collectAsState(initial = emptyList())
    val unreadNotifications by database.notificationDao().getUnreadCount().collectAsState(initial = 0)

    LaunchedEffect(Unit) {
        videoRepository.seedInitialDataIfNeeded()
    }

    if (showSplash) {
        SplashScreen(
            isOnline = isOnline,
            onSplashFinished = { showSplash = false }
        )
        return
    }

    // Hardware / gesture back button handling
    BackHandler(enabled = activeSubScreen !is ActiveSubScreen.None || currentTab != NavTab.HOME) {
        if (activeSubScreen !is ActiveSubScreen.None) {
            activeSubScreen = ActiveSubScreen.None
        } else if (currentTab != NavTab.HOME) {
            currentTab = NavTab.HOME
        }
    }

    Scaffold(
        containerColor = DeepBlack,
        bottomBar = {
            // Only show bottom navigation on primary tabs
            if (activeSubScreen is ActiveSubScreen.None && currentTab != NavTab.CREATE) {
                BottomNavBar(
                    selectedTab = currentTab,
                    offlineCount = offlineVideos.size,
                    onTabSelected = { tab ->
                        currentTab = tab
                        activeSubScreen = ActiveSubScreen.None
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (activeSubScreen is ActiveSubScreen.None && currentTab != NavTab.CREATE) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            when (val sub = activeSubScreen) {
                is ActiveSubScreen.CreatorProfile -> {
                    ProfileScreen(
                        userRepository = userRepository,
                        videoRepository = videoRepository,
                        targetUserId = sub.userId,
                        onNavigateToAdmin = { activeSubScreen = ActiveSubScreen.Admin },
                        onNavigateToChat = { recId -> activeSubScreen = ActiveSubScreen.DirectChat(recId) },
                        onVideoSelected = {
                            currentTab = NavTab.HOME
                            activeSubScreen = ActiveSubScreen.None
                        }
                    )
                }

                is ActiveSubScreen.DirectChat -> {
                    MessagesScreen(
                        messageRepository = messageRepository,
                        userRepository = userRepository,
                        initialRecipientId = sub.recipientId,
                        onBack = { activeSubScreen = ActiveSubScreen.None }
                    )
                }

                is ActiveSubScreen.Notifications -> {
                    NotificationsScreen(
                        database = database,
                        onBack = { activeSubScreen = ActiveSubScreen.None }
                    )
                }

                is ActiveSubScreen.Admin -> {
                    AdminScreen(
                        database = database,
                        videoRepository = videoRepository,
                        onBack = { activeSubScreen = ActiveSubScreen.None }
                    )
                }

                is ActiveSubScreen.None -> {
                    when (currentTab) {
                        NavTab.HOME -> {
                            HomeScreen(
                                videoRepository = videoRepository,
                                offlineVideoManager = offlineVideoManager,
                                isOnline = isOnline,
                                unreadMessagesCount = 1,
                                unreadNotificationsCount = unreadNotifications,
                                onNavigateToSearch = { currentTab = NavTab.DISCOVER },
                                onNavigateToMessages = { activeSubScreen = ActiveSubScreen.DirectChat(null) },
                                onNavigateToNotifications = { activeSubScreen = ActiveSubScreen.Notifications },
                                onNavigateToCreatorProfile = { authorId ->
                                    activeSubScreen = ActiveSubScreen.CreatorProfile(authorId)
                                }
                            )
                        }

                        NavTab.DISCOVER -> {
                            DiscoverScreen(
                                videoRepository = videoRepository,
                                userRepository = userRepository,
                                onVideoSelected = { video ->
                                    currentTab = NavTab.HOME
                                },
                                onCreatorSelected = { creatorId ->
                                    activeSubScreen = ActiveSubScreen.CreatorProfile(creatorId)
                                }
                            )
                        }

                        NavTab.CREATE -> {
                            CreateScreen(
                                videoRepository = videoRepository,
                                onVideoPublished = {
                                    currentTab = NavTab.HOME
                                },
                                onCancel = {
                                    currentTab = NavTab.HOME
                                }
                            )
                        }

                        NavTab.OFFLINE -> {
                            OfflineScreen(
                                offlineVideoManager = offlineVideoManager,
                                videoRepository = videoRepository,
                                isOnline = isOnline
                            )
                        }

                        NavTab.PROFILE -> {
                            ProfileScreen(
                                userRepository = userRepository,
                                videoRepository = videoRepository,
                                targetUserId = null,
                                onNavigateToAdmin = { activeSubScreen = ActiveSubScreen.Admin },
                                onNavigateToChat = { recId -> activeSubScreen = ActiveSubScreen.DirectChat(recId) },
                                onVideoSelected = {
                                    currentTab = NavTab.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
