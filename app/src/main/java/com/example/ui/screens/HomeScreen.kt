package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoEntity
import com.example.data.repository.VideoRepository
import com.example.data.sample.InitialContent
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.components.TopCategoryBar
import com.example.ui.components.VideoOverlay
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.VibrantRed
import com.example.util.OfflineVideoManager
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    videoRepository: VideoRepository,
    offlineVideoManager: OfflineVideoManager,
    isOnline: Boolean,
    unreadMessagesCount: Int,
    unreadNotificationsCount: Int,
    onNavigateToSearch: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCreatorProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedCategory by remember { mutableStateOf("Para Ti") }
    val videos by videoRepository.getVideosByCategory(selectedCategory)
        .collectAsState(initial = emptyList())

    val downloadProgressMap by offlineVideoManager.downloadProgressMap.collectAsState()

    val pagerState = rememberPagerState(pageCount = { videos.size })

    // Audio / play states
    var isMuted by remember { mutableStateOf(false) }
    var manuallyPausedVideoId by remember { mutableStateOf<String?>(null) }

    // Comment Bottom Sheet state
    var activeCommentVideoId by remember { mutableStateOf<String?>(null) }
    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val comments = activeCommentVideoId?.let { videoId ->
        videoRepository.getCommentsForVideo(videoId).collectAsState(initial = emptyList()).value
    } ?: emptyList()

    // Automatically record views when a page settles
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page in videos.indices) {
                    val currentVideo = videos[page]
                    videoRepository.recordView(currentVideo.id)
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A carregar vídeos do EDMAR E RONALDO TOK...",
                    color = BrightWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val video = videos[page]
                val isCurrentPage = (pagerState.currentPage == page)
                val isPlaying = isCurrentPage && (manuallyPausedVideoId != video.id)
                val downloadProgress = downloadProgressMap[video.id]

                Box(modifier = Modifier.fillMaxSize()) {
                    // Video player layer (only instantiate ExoPlayer on active page to prevent codec exhaustion)
                    if (isCurrentPage) {
                        val effectiveUrl = video.localFilePath ?: video.videoUrl
                        ExoVideoPlayer(
                            videoUrl = effectiveUrl,
                            isPlaying = isPlaying,
                            thumbnailUrl = video.thumbnailUrl,
                            isMuted = isMuted
                        )
                    } else {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Overlay layer
                    VideoOverlay(
                        video = video,
                        isPlaying = isPlaying,
                        isMuted = isMuted,
                        downloadProgress = downloadProgress,
                        onTogglePlay = {
                            manuallyPausedVideoId = if (isPlaying) video.id else null
                        },
                        onToggleMute = {
                            isMuted = !isMuted
                        },
                        onLikeClick = {
                            scope.launch {
                                videoRepository.toggleLike(video.id, video.isLikedByMe)
                            }
                        },
                        onCommentClick = {
                            activeCommentVideoId = video.id
                        },
                        onShareClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Vê este vídeo no EDMAR E RONALDO TOK!"
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Olha este vídeo brutal de ${video.authorName}: '${video.title}' no EDMAR E RONALDO TOK! A tua diversão não tem limites! 🔥🇦🇴\n${video.videoUrl}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partilhar vídeo"))
                        },
                        onFavoriteClick = {
                            scope.launch {
                                videoRepository.toggleFavorite(video.id, video.isFavoritedByMe)
                            }
                        },
                        onFollowClick = {
                            scope.launch {
                                videoRepository.toggleFollowAuthor(video.authorId, video.isFollowingAuthor)
                            }
                        },
                        onDownloadClick = {
                            scope.launch {
                                if (video.isOfflineAvailable) {
                                    snackbarHostState.showSnackbar("Este vídeo já está guardado nos teus 20 vídeos offline!")
                                } else {
                                    val result = offlineVideoManager.downloadVideo(video)
                                    if (result.isSuccess) {
                                        snackbarHostState.showSnackbar("Vídeo guardado com sucesso nos teus 20 vídeos offline! 📥")
                                    } else {
                                        val msg = result.exceptionOrNull()?.localizedMessage ?: "Erro ao descarregar"
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                            }
                        },
                        onCreatorClick = {
                            onNavigateToCreatorProfile(video.authorId)
                        },
                        onReportClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Denúncia enviada à equipa de moderação. Obrigado por manteres o ER TOK seguro!")
                            }
                        },
                        onNotInterestedClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Conteúdo ocultado das tuas recomendações.")
                            }
                        }
                    )
                }
            }
        }

        // Top Category Bar
        TopCategoryBar(
            categories = InitialContent.CATEGORIES,
            selectedCategory = selectedCategory,
            unreadMessagesCount = unreadMessagesCount,
            unreadNotificationsCount = unreadNotificationsCount,
            isOnline = isOnline,
            onCategorySelected = { category ->
                selectedCategory = category
            },
            onSearchClick = onNavigateToSearch,
            onMessagesClick = onNavigateToMessages,
            onNotificationsClick = onNavigateToNotifications,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Offline notice banner if disconnected
        if (!isOnline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp)
                    .background(VibrantRed.copy(alpha = 0.9f))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Estás offline. A reproduzir vídeos em cache ou visita 'Offline' para os teus 20 vídeos guardados.",
                    color = BrightWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Comment Bottom Sheet
        if (activeCommentVideoId != null) {
            CommentBottomSheet(
                comments = comments,
                sheetState = commentSheetState,
                onDismiss = { activeCommentVideoId = null },
                onSendComment = { text ->
                    scope.launch {
                        activeCommentVideoId?.let { vId ->
                            videoRepository.addComment(vId, text)
                        }
                    }
                },
                onToggleCommentLike = { commentId, isLiked ->
                    scope.launch {
                        videoRepository.toggleCommentLike(commentId, isLiked)
                    }
                }
            )
        }

        // Snackbar host for feedback messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }
}
