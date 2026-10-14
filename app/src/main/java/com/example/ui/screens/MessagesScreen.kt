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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.MessageEntity
import com.example.data.local.UserEntity
import com.example.data.repository.MessageRepository
import com.example.data.repository.UserRepository
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessagesScreen(
    messageRepository: MessageRepository,
    userRepository: UserRepository,
    initialRecipientId: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allUsers by userRepository.getAllUsers().collectAsState(initial = emptyList())
    val currentUser by userRepository.getCurrentUserFlow().collectAsState(initial = null)

    var activeRecipientId by remember { mutableStateOf(initialRecipientId) }
    var textMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        messageRepository.seedInitialMessagesIfEmpty()
    }

    val conversationId = activeRecipientId?.let { "conv_$it" } ?: "conv_brasil"

    val activeMessages by (if (activeRecipientId != null) {
        messageRepository.getMessages(conversationId)
    } else {
        messageRepository.getMessages("conv_brasil")
    }).collectAsState(initial = emptyList())

    val activeRecipient = allUsers.firstOrNull { it.id == activeRecipientId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (activeRecipientId != null && initialRecipientId == null) {
                    activeRecipientId = null
                } else {
                    onBack()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = BrightWhite
                )
            }

            if (activeRecipient != null) {
                AsyncImage(
                    model = activeRecipient.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, ElectricBlue, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = activeRecipient.displayName,
                        color = BrightWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Online agora • ER TOK Direto",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp
                    )
                }
            } else {
                Text(
                    text = "Mensagens Diretas",
                    color = BrightWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(GlassBorder))

        if (activeRecipientId == null) {
            // Conversations / Contacts List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Criadores e Amigos",
                        color = ElegantGrey,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                items(allUsers.filter { !it.isCurrentUser }, key = { it.id }) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceCard)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable { activeRecipientId = user.id }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = user.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.displayName,
                                color = BrightWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = user.bio,
                                color = ElegantGrey,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                    }
                }
            }
        } else {
            // Chat Message Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(activeMessages, key = { it.id }) { msg ->
                    val isMyMsg = (msg.senderId == currentUser?.id) || msg.isMe
                    MessageBubble(message = msg, isMe = isMyMsg)
                }
            }

            // Send Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textMessage,
                    onValueChange = { textMessage = it },
                    placeholder = {
                        Text(
                            text = "Envia uma mensagem direta...",
                            color = ElegantGrey,
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrightWhite,
                        unfocusedTextColor = BrightWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("chat_text_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textMessage.isNotBlank()) {
                            val msgToSend = textMessage
                            textMessage = ""
                            scope.launch {
                                messageRepository.sendMessage(
                                    conversationId = conversationId,
                                    recipientId = activeRecipientId!!,
                                    text = msgToSend
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (textMessage.isNotBlank()) ElectricBlue else DarkSurfaceElevated)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (textMessage.isNotBlank()) DeepBlack else ElegantGrey,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: MessageEntity, isMe: Boolean) {
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = formatter.format(Date(message.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 2.dp,
                        bottomEnd = if (isMe) 2.dp else 16.dp
                    )
                )
                .background(
                    if (isMe) Brush.horizontalGradient(listOf(TechPurple, ElectricBlue))
                    else Brush.horizontalGradient(listOf(DarkSurfaceElevated, DarkSurfaceCard))
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = BrightWhite,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeStr,
                        color = BrightWhite.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Entregue",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
