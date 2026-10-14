package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.OfflineVideoEntity
import com.example.data.local.VideoEntity
import com.example.data.repository.VideoRepository
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed
import com.example.util.OfflineVideoManager
import kotlinx.coroutines.launch

@Composable
fun OfflineScreen(
    offlineVideoManager: OfflineVideoManager,
    videoRepository: VideoRepository,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val offlineVideos by videoRepository.getOfflineVideos().collectAsState(initial = emptyList())
    val allVideos by videoRepository.getAllVideos().collectAsState(initial = emptyList())
    val downloadProgressMap by offlineVideoManager.downloadProgressMap.collectAsState()

    var playingVideoIndex by remember { mutableStateOf<Int?>(null) }

    val usedBytes = offlineVideoManager.getUsedStorageBytes()
    val availableBytes = offlineVideoManager.getAvailableStorageBytes()
    val usedMb = usedBytes / (1024.0 * 1024.0)
    val availableGb = availableBytes / (1024.0 * 1024.0 * 1024.0)

    val currentOfflineCount = offlineVideos.size
    val maxOfflineQuota = OfflineVideoManager.MAX_OFFLINE_VIDEOS

    // Full screen offline video pager player
    if (playingVideoIndex != null && offlineVideos.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = playingVideoIndex!!.coerceIn(0, offlineVideos.size - 1),
            pageCount = { offlineVideos.size }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val video = offlineVideos[page]
                val isCurrent = pagerState.currentPage == page
                Box(modifier = Modifier.fillMaxSize()) {
                    ExoVideoPlayer(
                        videoUrl = video.localFilePath ?: video.videoUrl,
                        isPlaying = isCurrent,
                        thumbnailUrl = video.thumbnailUrl,
                        isMuted = false
                    )

                    // Video Info bottom overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        Text(
                            text = video.title,
                            color = BrightWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${video.authorName} • Modo Offline 100% Local 📴",
                            color = ElectricBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Close offline player button
            IconButton(
                onClick = { playingVideoIndex = null },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar Leitor Offline",
                    tint = BrightWhite
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .padding(bottom = 60.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "📥 OS MEUS 20 VÍDEOS OFFLINE",
                    color = BrightWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
                Text(
                    text = "Assiste aos teus vídeos sem gastar dados",
                    color = ElegantGrey,
                    fontSize = 12.sp
                )
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isOnline) Color(0x3300C853) else Color(0x33FF1744))
                    .border(1.dp, if (isOnline) Color(0xFF00E676) else VibrantRed, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.SignalWifiOff,
                        contentDescription = null,
                        tint = if (isOnline) Color(0xFF00E676) else VibrantRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        color = if (isOnline) Color(0xFF00E676) else VibrantRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quota & Storage Card
            item {
                StorageQuotaCard(
                    currentCount = currentOfflineCount,
                    maxQuota = maxOfflineQuota,
                    usedMb = usedMb,
                    availableGb = availableGb
                )
            }

            // Quick Play All button if videos exist
            if (offlineVideos.isNotEmpty()) {
                item {
                    Button(
                        onClick = { playingVideoIndex = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = DeepBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("play_offline_videos_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reproduzir os 20 Vídeos em Sequência ▶",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // List of Offline Videos
            if (offlineVideos.isEmpty()) {
                item {
                    EmptyOfflineState(
                        isOnline = isOnline,
                        onDownloadSample = {
                            scope.launch {
                                allVideos.firstOrNull()?.let {
                                    offlineVideoManager.downloadVideo(it)
                                }
                            }
                        }
                    )
                }
            } else {
                item {
                    Text(
                        text = "Vídeos Guardados no Telemóvel ($currentOfflineCount/$maxOfflineQuota)",
                        color = BrightWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                items(offlineVideos, key = { it.id }) { video ->
                    OfflineVideoRow(
                        video = video,
                        onPlayClick = {
                            val idx = offlineVideos.indexOf(video)
                            playingVideoIndex = if (idx != -1) idx else 0
                        },
                        onDeleteClick = {
                            scope.launch {
                                offlineVideoManager.removeOfflineVideo(video.id)
                            }
                        }
                    )
                }
            }

            // Download more videos suggestions (if online and quota available)
            if (isOnline && currentOfflineCount < maxOfflineQuota) {
                val nonOfflineVideos = allVideos.filter { !it.isOfflineAvailable }
                if (nonOfflineVideos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Adicionar Mais Vídeos aos 20 Offline 📥",
                            color = BrightWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    items(nonOfflineVideos.take(3), key = { "rec_${it.id}" }) { video ->
                        val progress = downloadProgressMap[video.id]
                        AddOfflineVideoRow(
                            video = video,
                            progress = progress,
                            onDownload = {
                                scope.launch {
                                    offlineVideoManager.downloadVideo(video)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageQuotaCard(
    currentCount: Int,
    maxQuota: Int,
    usedMb: Double,
    availableGb: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vagas Offline: $currentCount / $maxQuota",
                        color = BrightWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                if (currentCount >= maxQuota) {
                    Text(
                        text = "Vagas Esgotadas!",
                        color = VibrantRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quota progress
            val quotaRatio = (currentCount.toFloat() / maxQuota).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { quotaRatio },
                color = if (currentCount >= maxQuota) VibrantRed else ElectricBlue,
                trackColor = DeepBlack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Storage Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = ElegantGrey,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Espaço ocupado: ${String.format("%.1f", usedMb)} MB",
                        color = ElegantGrey,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "Disponível: ${String.format("%.1f", availableGb)} GB",
                    color = BrightWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OfflineVideoRow(
    video: VideoEntity,
    onPlayClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail with play button overlay
        Box(
            modifier = Modifier
                .size(70.dp, 90.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onPlayClick() }
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproduzir",
                    tint = BrightWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                color = BrightWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = video.authorName,
                color = ElegantGrey,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✓ Guardado Localmente • ${video.fileSizeMb} MB",
                color = ElectricBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.testTag("delete_offline_video_${video.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover do Offline",
                tint = VibrantRed.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AddOfflineVideoRow(
    video: VideoEntity,
    progress: com.example.util.DownloadProgress?,
    onDownload: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp, 65.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                color = BrightWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                text = "${video.authorName} • ~${video.fileSizeMb} MB",
                color = ElegantGrey,
                fontSize = 11.sp
            )
            if (progress?.isDownloading == true) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress.progress / 100f },
                    color = ElectricBlue,
                    modifier = Modifier.fillMaxWidth().height(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onDownload,
            enabled = progress?.isDownloading != true,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                contentColor = DeepBlack
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (progress?.isDownloading == true) "${progress.progress}%" else "Guardar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyOfflineState(
    isOnline: Boolean,
    onDownloadSample: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Ainda não tens vídeos offline",
                color = BrightWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Podes guardar até 20 vídeos para assistir em qualquer lugar de Angola sem gastar os teus dados de internet!",
                color = ElegantGrey,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (isOnline) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDownloadSample,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = DeepBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Descarregar Primeiro Vídeo 🚀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
