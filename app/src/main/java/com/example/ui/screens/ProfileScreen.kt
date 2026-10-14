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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    videoRepository: VideoRepository,
    targetUserId: String? = null,
    onNavigateToAdmin: () -> Unit,
    onNavigateToChat: (recipientId: String) -> Unit,
    onVideoSelected: (VideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val currentUser by userRepository.getCurrentUserFlow().collectAsState(initial = null)
    val allUsers by userRepository.getAllUsers().collectAsState(initial = emptyList())

    // If viewing another creator, pick that user, else current user
    val displayedUser = if (targetUserId != null) {
        allUsers.firstOrNull { it.id == targetUserId } ?: currentUser
    } else {
        currentUser
    }

    val isMyProfile = (displayedUser?.id == currentUser?.id)

    val publishedVideos by videoRepository.getVideosByAuthor(displayedUser?.id ?: "")
        .collectAsState(initial = emptyList())
    val favoriteVideos by videoRepository.getFavoriteVideos()
        .collectAsState(initial = emptyList())
    val likedVideos by videoRepository.getLikedVideos()
        .collectAsState(initial = emptyList())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .padding(bottom = 60.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayedUser?.displayName ?: "Perfil",
                color = BrightWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMyProfile) {
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier.testTag("admin_panel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Painel Admin & Criador",
                            tint = GoldCrown
                        )
                    }
                }
            }
        }

        // Profile Avatar, Username, Verification
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = displayedUser?.avatarUrl,
                    contentDescription = displayedUser?.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, ElectricBlue, CircleShape)
                )
                if (displayedUser?.isVerified == true) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verificado",
                            tint = DeepBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayedUser?.displayName ?: "",
                    color = BrightWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp
                )
            }

            Text(
                text = "@${displayedUser?.username ?: ""}",
                color = ElegantGrey,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bio
            Text(
                text = displayedUser?.bio ?: "Sem biografia ainda.",
                color = BrightWhite.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Row (Followers, Following, Likes)
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "A Seguir", count = displayedUser?.followingCount ?: 0)
                StatItem(label = "Seguidores", count = displayedUser?.followersCount ?: 0)
                StatItem(label = "Gostos", count = displayedUser?.likesReceived ?: 0)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (isMyProfile) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showEditProfileDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = BrightWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("edit_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar Perfil", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onNavigateToAdmin,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldCrown),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(GoldCrown)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Painel Criador", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isFollowing = displayedUser?.isFollowing == true
                    Button(
                        onClick = {
                            scope.launch {
                                displayedUser?.let {
                                    videoRepository.toggleFollowAuthor(it.id, isFollowing)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) DarkSurfaceElevated else VibrantRed,
                            contentColor = BrightWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text(if (isFollowing) "A Seguir" else "Seguir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            displayedUser?.let { onNavigateToChat(it.id) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = DeepBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mensagem", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs (Publicações, Favoritos, Gostos)
        val tabs = listOf("Publicações", "Favoritos", "Gostos")
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DeepBlack,
            contentColor = BrightWhite,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ElectricBlue,
                    height = 2.5.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) BrightWhite else ElegantGrey
                        )
                    }
                )
            }
        }

        // Tab Content Grid
        val currentList = when (selectedTabIndex) {
            0 -> publishedVideos
            1 -> favoriteVideos
            else -> likedVideos
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedTabIndex) {
                        0 -> "Ainda sem publicações no ER TOK."
                        1 -> "Nenhum vídeo guardado nos favoritos."
                        else -> "Nenhum vídeo marcado com gosto."
                    },
                    color = ElegantGrey,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentList, key = { it.id }) { video ->
                    VideoThumbnailCard(
                        video = video,
                        onClick = { onVideoSelected(video) }
                    )
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && currentUser != null) {
        var editName by remember { mutableStateOf(currentUser!!.displayName) }
        var editBio by remember { mutableStateOf(currentUser!!.bio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text("Editar Perfil ER TOK", color = BrightWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nome Apresentado") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrightWhite,
                            unfocusedTextColor = BrightWhite,
                            focusedBorderColor = ElectricBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Biografia") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrightWhite,
                            unfocusedTextColor = BrightWhite,
                            focusedBorderColor = ElectricBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            userRepository.updateProfile(editName, editBio)
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar", color = ElegantGrey)
                }
            }
        )
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatStat(count),
            color = BrightWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp
        )
        Text(
            text = label,
            color = ElegantGrey,
            fontSize = 12.sp
        )
    }
}

private fun formatStat(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
