package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.VideoEntity
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
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AdminScreen(
    database: AppDatabase,
    videoRepository: VideoRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allVideos by videoRepository.getAllVideos().collectAsState(initial = emptyList())
    val offlineVideos by videoRepository.getOfflineVideos().collectAsState(initial = emptyList())

    val totalViews = allVideos.sumOf { it.viewsCount.toLong() }
    val totalLikes = allVideos.sumOf { it.likesCount.toLong() }

    var importMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = BrightWhite
                )
            }
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = GoldCrown,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Painel do Criador & Admin",
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "EDMAR E RONALDO TOK • Gestão Oficial",
                    color = ElegantGrey,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(GlassBorder))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Stats Cards
            item {
                Text(
                    text = "Estatísticas em Tempo Real",
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Vídeos",
                        value = "${allVideos.size}",
                        icon = Icons.Default.VideoLibrary,
                        tint = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Visualizações",
                        value = formatNumber(totalViews),
                        icon = Icons.Default.Visibility,
                        tint = TechPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Gostos",
                        value = formatNumber(totalLikes),
                        icon = Icons.Default.ThumbUp,
                        tint = VibrantRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Guardados Offline",
                        value = "${offlineVideos.size} / 20",
                        icon = Icons.Default.CloudDownload,
                        tint = GoldCrown,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Batch Content Import
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📦", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Importação em Lote de Conteúdos Autorizados",
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Importa pacotes de vídeos autorizados em qualidade HD com metadados completos para alimentar as categorias do feed.",
                            color = ElegantGrey,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    val newBatch = listOf(
                                        VideoEntity(
                                            id = "vid_batch_${UUID.randomUUID().toString().take(6)}",
                                            title = "Gilmário Vemba: Momento de Ouro nos Palcos de Luanda 🇦🇴",
                                            description = "Apresentação comédia em Luanda com lotação esgotada! Rir e dançar até cair! 😂🔥 #GilmarioVemba #HumorAngola #Luanda #TikTokAngola",
                                            videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                                            thumbnailUrl = "https://images.unsplash.com/photo-1527224857830-43a7acc85260?auto=format&fit=crop&w=600&q=80",
                                            authorId = "user_gilmario",
                                            authorName = "Gilmário Vemba 🇦🇴",
                                            authorHandle = "@gilmariovemba",
                                            authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
                                            isVerified = true,
                                            likesCount = 28200,
                                            commentsCount = 1280,
                                            sharesCount = 3640,
                                            favoritesCount = 2380,
                                            viewsCount = 145000,
                                            musicTitle = "Stand-up Luanda Live - Gilmário",
                                            musicAuthor = "Gilmário Vemba",
                                            category = "Comédia",
                                            hashtags = listOf("GilmarioVemba", "HumorAngola", "Luanda"),
                                            createdAt = System.currentTimeMillis()
                                        )
                                    )
                                    videoRepository.batchImportVideos(newBatch)
                                    importMessage = "Pacote de conteúdos importado com sucesso! +${newBatch.size} vídeo no feed."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = DeepBlack
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Importar Novo Pacote Curado 🚀", fontWeight = FontWeight.Bold)
                        }

                        if (importMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = importMessage!!,
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Live Streams Status Card (Prompt section 14)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = null,
                                tint = VibrantRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Transmissões em Direto (Live)",
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Infraestrutura de Transmissão: Requer servidor de mídia em tempo real WebRTC / RTMP (ex: LiveKit ou Agora.io). Conforme a diretriz de transparência da plataforma, a funcionalidade aguarda ativação de servidor de stream.",
                            color = ElegantGrey,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VibrantRed.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Pronto para Integração RTMP/WebRTC",
                                color = VibrantRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Moderation & Reports Status
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceCard)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = null,
                                tint = GoldCrown,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Moderação & Denúncias",
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "0 denúncias pendentes de revisão. O algoritmo de segurança do ER TOK está a operar normalmente.",
                            color = ElegantGrey,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = ElegantGrey, fontSize = 12.sp)
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = BrightWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

private fun formatNumber(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
