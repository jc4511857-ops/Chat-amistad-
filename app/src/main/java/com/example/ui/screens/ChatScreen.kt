package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.MessageEntity
import com.example.data.local.ProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AMORA_GIFTS
import com.example.ui.components.GiftSelectorSheet
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    partnerId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val profiles by viewModel.profiles.collectAsState()
    val partner = profiles.find { it.id == partnerId } ?: ProfileEntity(
        id = partnerId,
        name = "Valentina López",
        age = 23,
        region = "CDMX",
        bio = "Apasionada del café y la música.",
        imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
        galleryImages = "",
        occupation = "Diseñadora UX",
        interests = "Café, Arte, Música",
        isOnline = true,
        isStreaming = false,
        likesCount = 340,
        distanceKm = 3,
        verifiedBadge = true
    )

    val messages by viewModel.currentChatMessages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var showGiftSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.selectedProfileDetail.value = partner
                        }
                    ) {
                        AsyncImage(
                            model = partner.imageUrl,
                            contentDescription = partner.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = partner.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (partner.verifiedBadge) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verificado",
                                        tint = AmoraGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "📍 ${partner.region}, México",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.openCardPaymentForGift(
                                gift = AMORA_GIFTS[0],
                                recipientId = partner.id,
                                recipientName = partner.name
                            )
                        }
                    ) {
                        Text("🎁", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("chat_screen_$partnerId")
        ) {
            // Messages List
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💬 ¡Da el primer paso!", fontWeight = FontWeight.Bold)
                                Text(
                                    "Envía un saludo o un regalo especial con tarjeta a ${partner.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(messages) { msg ->
                        ChatMessageBubble(message = msg)
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { showGiftSheet = true }) {
                        Text("🎁", fontSize = 24.sp)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe un mensaje...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendChatMessageText(partnerId, messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .background(AmoraRosePrimary, CircleShape)
                            .testTag("send_chat_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    }

    if (showGiftSheet) {
        GiftSelectorSheet(
            recipientName = partner.name,
            onDismiss = { showGiftSheet = false },
            onGiftSelectedForCardPayment = { gift ->
                showGiftSheet = false
                viewModel.openCardPaymentForGift(
                    gift = gift,
                    recipientId = partner.id,
                    recipientName = partner.name
                )
            }
        )
    }
}

@Composable
fun ChatMessageBubble(message: MessageEntity) {
    val isMe = message.isFromMe
    val isGift = !message.giftType.isNullOrBlank()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isGift) {
                AmoraGold.copy(alpha = 0.9f)
            } else if (isMe) {
                AmoraRosePrimary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!message.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = message.imageUri,
                        contentDescription = "Foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = message.text,
                    color = if (isGift) Color.Black else if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isGift) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}
