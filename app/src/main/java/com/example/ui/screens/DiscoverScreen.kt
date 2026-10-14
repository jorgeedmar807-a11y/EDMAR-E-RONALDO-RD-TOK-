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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
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
import kotlinx.coroutines.launch

@Composable
fun DiscoverScreen(
    videoRepository: VideoRepository,
    userRepository: UserRepository,
    onVideoSelected: (VideoEntity) -> Unit,
    onCreatorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val allVideos by videoRepository.getAllVideos().collectAsState(initial = emptyList())
    val searchResults by videoRepository.searchVideos(searchQuery).collectAsState(initial = emptyList())
    val creators by userRepository.getAllUsers().collectAsState(initial = emptyList())

    val trendingTags = listOf("Luanda", "GilmarioVemba", "GerilsonInsrael", "PretoShow", "ScroQCuia", "Titica", "AnselmoRalph", "MatiasDamasio", "Kuduro", "Semba", "Dança")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .padding(bottom = 60.dp)
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Pesquisar famosos de Luanda, @gilmariovemba, #kuduro...",
                        color = ElegantGrey,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = ElectricBlue
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpar",
                                tint = ElegantGrey
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BrightWhite,
                    unfocusedTextColor = BrightWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("discover_search_input")
            )
        }

        if (searchQuery.isNotBlank()) {
            // Search Results Grid
            Text(
                text = "Resultados para \"$searchQuery\" (${searchResults.size})",
                color = BrightWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum vídeo encontrado.", color = ElegantGrey, fontSize = 14.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults, key = { it.id }) { video ->
                        VideoThumbnailCard(
                            video = video,
                            onClick = { onVideoSelected(video) }
                        )
                    }
                }
            }
        } else {
            // Discover Sections: Trending Banner, Tags, Recommended Creators, Highlights Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Featured Angola Stars Banner
                item {
                    FeaturedHeroBanner(
                        title = "Famosos de Luanda & Angola 🇦🇴",
                        subtitle = "Assiste aos maiores sucessos de Gilmário Vemba, Preto Show, Gerilson Insrael, Scró Q Cuia e muito mais!",
                        onClick = {
                            allVideos.firstOrNull()?.let { onVideoSelected(it) }
                        }
                    )
                }

                // Trending Hashtags
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Tendências",
                            tint = VibrantRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tendências Populares",
                            color = BrightWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trendingTags) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                    .clickable { searchQuery = tag }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("trending_tag_$tag")
                            ) {
                                Text(
                                    text = "#$tag",
                                    color = ElectricBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Recommended Creators
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Criadores Recomendados",
                        color = BrightWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(creators.filter { !it.isCurrentUser }) { creator ->
                            CreatorCard(
                                user = creator,
                                onFollowToggle = {
                                    scope.launch {
                                        videoRepository.toggleFollowAuthor(creator.id, creator.isFollowing)
                                    }
                                },
                                onCardClick = { onCreatorSelected(creator.id) }
                            )
                        }
                    }
                }

                // Popular Videos Grid
                item {
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = "Vídeos em Destaque 🔥",
                        color = BrightWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Videos Grid Rows
                val chunkedVideos = allVideos.chunked(3)
                items(chunkedVideos) { rowVideos ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (video in rowVideos) {
                            Box(modifier = Modifier.weight(1f)) {
                                VideoThumbnailCard(
                                    video = video,
                                    onClick = { onVideoSelected(video) }
                                )
                            }
                        }
                        // Fill extra space if row has less than 3
                        for (i in 0 until (3 - rowVideos.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedHeroBanner(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DeepBlack,
                        TechPurple.copy(alpha = 0.85f),
                        ElectricBlue.copy(alpha = 0.9f)
                    )
                )
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.75f)) {
            Text(
                text = title,
                color = BrightWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = BrightWhite.copy(alpha = 0.85f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrightWhite)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ver Destaques ▶",
                    color = DeepBlack,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CreatorCard(
    user: UserEntity,
    onFollowToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .clickable { onCardClick() }
            .padding(10.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = user.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .border(1.5.dp, ElectricBlue, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.displayName,
                color = BrightWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (user.isVerified) {
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Text(
            text = "@${user.username}",
            color = ElegantGrey,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onFollowToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.isFollowing) DarkSurfaceElevated else VibrantRed,
                contentColor = BrightWhite
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        ) {
            Text(
                text = if (user.isFollowing) "A seguir" else "Seguir",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VideoThumbnailCard(
    video: VideoEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .clip(RoundedCornerShape(8.dp))
            .background(CinemaBlack)
            .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for bottom stats
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.6f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.8f)
                    )
                )
        )

        // Views count badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = BrightWhite,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = formatViews(video.viewsCount),
                color = BrightWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatViews(views: Int): String {
    return when {
        views >= 1_000_000 -> String.format("%.1fM", views / 1_000_000.0)
        views >= 1_000 -> String.format("%.1fK", views / 1_000.0)
        else -> views.toString()
    }
}
