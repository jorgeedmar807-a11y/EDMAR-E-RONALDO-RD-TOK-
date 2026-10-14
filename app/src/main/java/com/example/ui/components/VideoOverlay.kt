package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.VideoEntity
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.VibrantRed
import com.example.util.DownloadProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoOverlay(
    video: VideoEntity,
    isPlaying: Boolean,
    isMuted: Boolean,
    downloadProgress: DownloadProgress?,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onFollowClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCreatorClick: () -> Unit,
    onReportClick: () -> Unit,
    onNotInterestedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    // Rotating vinyl animation for music
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(video.id) {
                detectTapGestures(
                    onDoubleTap = {
                        scope.launch {
                            showDoubleTapHeart = true
                            if (!video.isLikedByMe) {
                                onLikeClick()
                            }
                            delay(900)
                            showDoubleTapHeart = false
                        }
                    },
                    onTap = {
                        onTogglePlay()
                    }
                )
            }
    ) {
        // Subtle top and bottom gradient for high contrast readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.4f),
                        0.2f to Color.Transparent,
                        0.65f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.85f)
                    )
                )
        )

        // Pause icon overlay if video is paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproduzir",
                    tint = BrightWhite,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Animated double tap heart
        AnimatedVisibility(
            visible = showDoubleTapHeart,
            enter = scaleIn(initialScale = 0.3f) + fadeIn(),
            exit = scaleOut(targetScale = 1.3f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Gosto!",
                tint = VibrantRed,
                modifier = Modifier.size(110.dp)
            )
        }

        // Top right mute & options
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 64.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .size(38.dp)
                    .testTag("mute_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Outlined.VolumeMute else Icons.Outlined.VolumeUp,
                    contentDescription = "Alternar som",
                    tint = BrightWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .size(38.dp)
                        .testTag("more_options_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = BrightWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(CinemaBlack)
                ) {
                    DropdownMenuItem(
                        text = { Text("Não tenho interesse", color = BrightWhite) },
                        onClick = {
                            menuExpanded = false
                            onNotInterestedClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Denunciar vídeo", color = VibrantRed) },
                        onClick = {
                            menuExpanded = false
                            onReportClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Copiar ligação", color = ElectricBlue) },
                        onClick = {
                            menuExpanded = false
                            onShareClick()
                        }
                    )
                }
            }
        }

        // Bottom Content (Author, Caption, Music)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 24.dp)
        ) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onCreatorClick() }
                    .testTag("author_name_row")
            ) {
                Text(
                    text = video.authorName,
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                if (video.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verificado",
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = video.authorHandle,
                    color = ElegantGrey,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption
            Text(
                text = video.description,
                color = BrightWhite.copy(alpha = 0.95f),
                fontSize = 14.sp,
                maxLines = if (isDescriptionExpanded) 10 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                    .testTag("video_description")
            )

            // Category tag
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricBlue.copy(alpha = 0.2f))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.category,
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                video.hashtags.take(2).forEach { tag ->
                    Text(
                        text = "#$tag",
                        color = BrightWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Music / Audio info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Áudio",
                    tint = BrightWhite,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${video.musicTitle} • ${video.musicAuthor}",
                    color = BrightWhite.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right Action Bar (Creator Avatar, Like, Comment, Fav, Download, Share, Vinyl)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with Follow button
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                AsyncImage(
                    model = video.authorAvatarUrl,
                    contentDescription = "Perfil de ${video.authorName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, BrightWhite, CircleShape)
                        .clickable { onCreatorClick() }
                        .testTag("creator_avatar")
                )

                if (!video.isFollowingAuthor) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomCenter)
                            .clip(CircleShape)
                            .background(VibrantRed)
                            .clickable { onFollowClick() }
                            .testTag("follow_plus_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = BrightWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Like Action
            ActionIconItem(
                icon = if (video.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                label = formatCount(video.likesCount),
                tint = if (video.isLikedByMe) VibrantRed else BrightWhite,
                testTag = "like_action_button",
                onClick = onLikeClick
            )

            // Comment Action
            ActionIconItem(
                icon = Icons.Outlined.Comment,
                label = formatCount(video.commentsCount),
                tint = BrightWhite,
                testTag = "comment_action_button",
                onClick = onCommentClick
            )

            // Favorite Action
            ActionIconItem(
                icon = if (video.isFavoritedByMe) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                label = formatCount(video.favoritesCount),
                tint = if (video.isFavoritedByMe) GoldCrown else BrightWhite,
                testTag = "favorite_action_button",
                onClick = onFavoriteClick
            )

            // 20 Offline Videos Download Action
            val isDownloading = downloadProgress?.isDownloading == true
            val isDownloaded = video.isOfflineAvailable
            Box(contentAlignment = Alignment.Center) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        progress = { (downloadProgress?.progress ?: 0) / 100f },
                        modifier = Modifier.size(36.dp),
                        color = ElectricBlue,
                        strokeWidth = 3.dp
                    )
                }
                ActionIconItem(
                    icon = if (isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                    label = if (isDownloaded) "Guardado" else "Offline",
                    tint = if (isDownloaded) ElectricBlue else BrightWhite,
                    testTag = "offline_download_button",
                    onClick = onDownloadClick
                )
            }

            // Share Action
            ActionIconItem(
                icon = Icons.Default.Share,
                label = formatCount(video.sharesCount),
                tint = BrightWhite,
                testTag = "share_action_button",
                onClick = onShareClick
            )

            // Rotating Vinyl Disc
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DeepBlack)
                    .border(2.dp, Color(0xFF2C2C34), CircleShape)
                    .rotate(if (isPlaying) rotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = video.authorAvatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Composable
private fun ActionIconItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = BrightWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
