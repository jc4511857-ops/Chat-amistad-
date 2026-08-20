package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AMORA_GIFTS
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen

val MEXICO_REGIONS = listOf(
    "Todas",
    "Copándaro de Galeana",
    "Santa Rita de Casia",
    "Nispo",
    "La Cañada de la Yerbabuena",
    "San Agustín Arúmbaro",
    "Morelia",
    "CDMX",
    "Guadalajara",
    "Monterrey"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    onOpenStream: (streamId: String) -> Unit,
    onOpenChat: (partnerId: String) -> Unit
) {
    val profiles by viewModel.profiles.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    var selectedProfileForDetail by remember { mutableStateOf<ProfileEntity?>(null) }
    var showInstallShareDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("discover_screen")
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Amora",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = AmoraRosePrimary,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "🇲🇽 Michoacán",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmoraGold
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable {
                    viewModel.selectRegion("Copándaro de Galeana")
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = AmoraRosePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedRegion == "Todas") "Copándaro de Galeana" else selectedRegion,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Region selector chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MEXICO_REGIONS) { region ->
                val isSelected = region == selectedRegion
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectRegion(region) },
                    label = {
                        Text(
                            text = if (region == "Todas") "🇲🇽 Todas las Regiones" else "📍 $region",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AmoraRosePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Profiles list
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Banner Copándaro de Galeana & Social Instalar Amora
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable { showInstallShareDialog = true }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_mexico_banner_1785346604614),
                            contentDescription = "Michoacán Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                Text(
                                    text = "Copándaro de Galeana, Michoacán 🇲🇽",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Encuentra perfiles en Santa Rita de Casia, Nispo, La Cañada de la Yerbabuena, San Agustín Arúmbaro.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { showInstallShareDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmoraGold),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Anuncios & Botón 'Instalar Amora' 📲", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (profiles.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay perfiles activos en esta región por el momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(profiles) { profile ->
                    ProfileCardItem(
                        profile = profile,
                        onLikeClick = { viewModel.toggleLike(profile.id, profile.isLikedByMe) },
                        onChatClick = { onOpenChat(profile.id) },
                        onWatchLiveClick = {
                            profile.activeStreamId?.let { streamId -> onOpenStream(streamId) }
                        },
                        onGiftClick = {
                            viewModel.openCardPaymentForGift(
                                gift = AMORA_GIFTS[0],
                                recipientId = profile.id,
                                recipientName = profile.name
                            )
                        },
                        onCardClick = { selectedProfileForDetail = profile }
                    )
                }
            }
        }
    }

    // Detailed Profile Bottom Sheet
    selectedProfileForDetail?.let { profile ->
        ModalBottomSheet(
            onDismissRequest = { selectedProfileForDetail = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${profile.name}, ${profile.age}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (profile.verifiedBadge) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verificado", tint = AmoraGold)
                            }
                        }
                        Text(
                            text = "📍 ${profile.region}, México • a ${profile.distanceKm} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { selectedProfileForDetail = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image gallery
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sobre mí",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Ocupación: ${profile.occupation}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Redes Sociales",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    profile.tiktokHandle?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text("🎵 TikTok: $it", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                    profile.instagramHandle?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text("📸 IG: $it", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                    profile.facebookHandle?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text("📘 FB: $it", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Intereses",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    profile.interests.split(",").forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag.trim(), fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedProfileForDetail = null
                            onOpenChat(profile.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Iniciar Chat")
                    }

                    OutlinedButton(
                        onClick = {
                            selectedProfileForDetail = null
                            viewModel.openCardPaymentForGift(
                                gift = AMORA_GIFTS[0],
                                recipientId = profile.id,
                                recipientName = profile.name
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("🎁 Enviar Regalo")
                    }
                }
            }
        }
    }

    if (showInstallShareDialog) {
        AlertDialog(
            onDismissRequest = { showInstallShareDialog = false },
            icon = { Icon(Icons.Default.Share, contentDescription = null, tint = AmoraRosePrimary) },
            title = { Text("Anuncios & Botón 'Instalar Amora'", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Promociona la aplicación Amora México en Copándaro de Galeana, Santa Rita de Casia, Nispo, La Cañada de la Yerbabuena y San Agustín Arúmbaro.",
                        fontSize = 13.sp
                    )
                    Text("Comparte el enlace oficial con botón de instalación instantánea en:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AssistChip(onClick = { showInstallShareDialog = false }, label = { Text("🎵 TikTok") })
                        AssistChip(onClick = { showInstallShareDialog = false }, label = { Text("📸 Instagram") })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AssistChip(onClick = { showInstallShareDialog = false }, label = { Text("📘 Facebook") })
                        AssistChip(onClick = { showInstallShareDialog = false }, label = { Text("💬 WhatsApp") })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInstallShareDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary)
                ) {
                    Text("📲 Instalar Amora / Copiar Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInstallShareDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ProfileCardItem(
    profile: ProfileEntity,
    onLikeClick: () -> Unit,
    onChatClick: () -> Unit,
    onWatchLiveClick: () -> Unit,
    onGiftClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("profile_card_${profile.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top bar overlay badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Region & Online status
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (profile.isOnline) OnlineGreen else Color.Gray,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = profile.region,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (profile.isStreaming) {
                        Surface(
                            color = LiveRed,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { onWatchLiveClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "En Vivo",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "EN VIVO",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Bottom name & distance gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${profile.name}, ${profile.age}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            if (profile.verifiedBadge) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verificado",
                                    tint = AmoraGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "📍 ${profile.region}, México • a ${profile.distanceKm} km",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Card Body & Action Buttons
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                if (profile.tiktokHandle != null || profile.instagramHandle != null || profile.facebookHandle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        profile.tiktokHandle?.let {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🎵 $it", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        profile.instagramHandle?.let {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("📸 $it", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        profile.facebookHandle?.let {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("📘 $it", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like button
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier
                            .background(
                                if (profile.isLikedByMe) AmoraRosePrimary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (profile.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Me gusta",
                            tint = if (profile.isLikedByMe) AmoraRosePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Direct Chat
                    OutlinedButton(
                        onClick = onChatClick,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat", fontSize = 13.sp)
                    }

                    // Send Gift with Card
                    Button(
                        onClick = onGiftClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("🎁 Regalo Tarjeta", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
