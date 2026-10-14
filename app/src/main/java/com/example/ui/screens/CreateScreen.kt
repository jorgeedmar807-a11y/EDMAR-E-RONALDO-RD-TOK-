package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.VideoRepository
import com.example.data.sample.InitialContent
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    videoRepository: VideoRepository,
    onVideoPublished: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Dança") }
    var hashtagsInput by remember { mutableStateOf("Luanda, Angola, Kuduro, Danca") }
    var selectedAudio by remember { mutableStateOf("Som Original - Luanda Hits") }

    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var isSuccess by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker for Videos
    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri.toString()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = BrightWhite
                )
            }
            Text(
                text = "Estúdio de Criação ER TOK",
                color = BrightWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Box(modifier = Modifier.size(48.dp)) // Spacer balance
        }

        if (selectedVideoUri == null) {
            // Source Selection (Gallery vs Studio Sample)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(ElectricBlue, TechPurple, VibrantRed))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = BrightWhite,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Cria ou Partilha o Teu Talento",
                    color = BrightWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Seleciona um vídeo da tua galeria ou grava uma criação para a comunidade EDMAR E RONALDO TOK!",
                    color = ElegantGrey,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Gallery Button
                Button(
                    onClick = {
                        pickVideoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = DeepBlack
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("import_gallery_video_button")
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Importar Vídeo da Galeria",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Studio Sample Capture
                OutlinedButton(
                    onClick = {
                        // High Definition authorized creator sample (working 200 OK stream)
                        selectedVideoUri = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4"
                        title = "Vídeo do Estúdio Luanda 2026"
                        description = "Criação de alta definição gravada no estúdio para todos os fãs de Luanda e Angola! 🚀🇦🇴 #Luanda #Angola #Kuduro"
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrightWhite
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(ElectricBlue, VibrantRed))
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("record_studio_video_button")
                ) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = VibrantRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gravar no Estúdio ER TOK",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Video Editor & Publishing Form
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Video Preview Card (Compact 9:16)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CinemaBlack)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                ) {
                    ExoVideoPlayer(
                        videoUrl = selectedVideoUri!!,
                        isPlaying = true,
                        isMuted = true
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { selectedVideoUri = null }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Alterar Vídeo", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título do Vídeo") },
                    placeholder = { Text("Ex: A melhor dança de Angola!") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrightWhite,
                        unfocusedTextColor = BrightWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = ElectricBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_video_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição e menções") },
                    placeholder = { Text("Descreve o teu vídeo e marca amigos...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrightWhite,
                        unfocusedTextColor = BrightWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = ElectricBlue
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_video_description_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hashtags
                OutlinedTextField(
                    value = hashtagsInput,
                    onValueChange = { hashtagsInput = it },
                    label = { Text("Hashtags (separadas por vírgula)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrightWhite,
                        unfocusedTextColor = BrightWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = ElectricBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category selector
                Text(
                    text = "Categoria:",
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val quickCategories = listOf("Dança", "Música", "Comédia", "Angola 🇦🇴")
                    quickCategories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = DeepBlack,
                                containerColor = DarkSurfaceElevated,
                                labelColor = ElegantGrey
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Music / Audio Track
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = VibrantRed
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Áudio Selecionado",
                            color = ElegantGrey,
                            fontSize = 11.sp
                        )
                        Text(
                            text = selectedAudio,
                            color = BrightWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Upload Progress Bar
                AnimatedVisibility(visible = isUploading) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "A processar e publicar vídeo: ${(uploadProgress * 100).toInt()}%",
                            color = ElectricBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            color = ElectricBlue,
                            trackColor = DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Success message
                AnimatedVisibility(visible = isSuccess) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.2f))
                            .padding(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publicado com sucesso! Já está no feed do ER TOK! 🚀",
                            color = BrightWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons (Draft vs Publish)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                // Save as draft
                                val tags = hashtagsInput.split(",").map { it.trim() }
                                videoRepository.publishVideo(
                                    title = "[Rascunho] $title",
                                    description = description,
                                    videoUri = selectedVideoUri!!,
                                    category = category,
                                    hashtags = tags,
                                    musicTitle = selectedAudio
                                )
                                onVideoPublished()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElegantGrey),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Rascunho", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isUploading = true
                                uploadProgress = 0.2f
                                delay(300)
                                uploadProgress = 0.6f
                                delay(300)
                                uploadProgress = 1.0f
                                delay(200)

                                val tags = hashtagsInput.split(",").map { it.trim() }
                                videoRepository.publishVideo(
                                    title = title,
                                    description = description,
                                    videoUri = selectedVideoUri!!,
                                    category = category,
                                    hashtags = tags,
                                    musicTitle = selectedAudio
                                )

                                isUploading = false
                                isSuccess = true
                                delay(600)
                                onVideoPublished()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantRed,
                            contentColor = BrightWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isUploading,
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("publish_video_button")
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(color = BrightWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Publicar Agora 🔥", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
