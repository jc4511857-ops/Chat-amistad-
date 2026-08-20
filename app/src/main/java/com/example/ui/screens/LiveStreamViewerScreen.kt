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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.LiveStreamEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AMORA_GIFTS
import com.example.ui.components.GiftSelectorSheet
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import com.example.ui.theme.LiveRed
import kotlinx.coroutines.launch

@Composable
fun LiveStreamViewerScreen(
    streamId: String,
    viewModel: MainViewModel,
    onCloseStream: () -> Unit
) {
    val liveStreams by viewModel.liveStreams.collectAsState()
    val stream = liveStreams.find { it.streamId == streamId } ?: LiveStreamEntity(
        streamId = streamId,
        hostId = "h1",
        hostName = "Sofia Ramírez",
        hostAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
        hostRegion = "CDMX",
        title = "🎙️ Transmisión en Vivo",
        category = "Música",
        viewersCount = 1240,
        totalEarningsMxn = 3850.0,
        coverImageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4"
    )

    val comments by viewModel.currentStreamComments.collectAsState()
    var commentInput by remember { mutableStateOf("") }
    var showGiftSheet by remember { mutableStateOf(false) }
    var floatingHeartsCount by remember { mutableStateOf(0) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_stream_viewer_screen")
    ) {
        // Video stream background simulation
        AsyncImage(
            model = stream.coverImageUrl,
            contentDescription = "Video Stream",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    floatingHeartsCount++
                }
        )

        // Dark gradient overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
        )

        // Top Stream Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = stream.hostAvatarUrl,
                        contentDescription = stream.hostName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stream.hostName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "📍 ${stream.hostRegion} • 👁️ ${stream.viewersCount}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onCloseStream,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }

        // Live Floating Comments Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 80.dp)
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(comments) { comment ->
                    val isGift = !comment.giftType.isNullOrBlank()
                    Surface(
                        color = if (isGift) AmoraRosePrimary.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${comment.senderName}: ",
                                color = AmoraGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = comment.message,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Floating Heart tapping counter overlay
        if (floatingHeartsCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("❤️ x$floatingHeartsCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AmoraRosePrimary)
                }
            }
        }

        // Bottom Input & Card Tipping Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = commentInput,
                onValueChange = { commentInput = it },
                placeholder = { Text("Comentar en vivo...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    focusedBorderColor = AmoraRosePrimary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                viewModel.sendStreamCommentText(commentInput)
                                commentInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )

            // Heart button
            IconButton(
                onClick = { floatingHeartsCount++ },
                modifier = Modifier.background(AmoraRosePrimary, CircleShape)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Me gusta", tint = Color.White)
            }

            // Regalo con Tarjeta button
            Button(
                onClick = { showGiftSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = AmoraGold),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("🎁 Regalar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    if (showGiftSheet) {
        GiftSelectorSheet(
            recipientName = stream.hostName,
            onDismiss = { showGiftSheet = false },
            onGiftSelectedForCardPayment = { gift ->
                showGiftSheet = false
                viewModel.openCardPaymentForGift(
                    gift = gift,
                    recipientId = stream.hostId,
                    recipientName = stream.hostName,
                    streamId = stream.streamId
                )
            }
        )
    }
}
