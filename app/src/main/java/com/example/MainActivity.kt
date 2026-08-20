package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AppNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.CardPaymentModal
import com.example.ui.components.CardTippingModal
import com.example.ui.screens.*
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import com.example.ui.theme.AmoraTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmoraTheme {
                AmoraAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AmoraAppContent(viewModel: MainViewModel) {
    val myProfile by viewModel.myProfile.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val activeWatchingStreamId by viewModel.activeWatchingStreamId.collectAsState()
    val activeChatPartnerId by viewModel.activeChatPartnerId.collectAsState()
    val showCardPaymentModal by viewModel.showCardPaymentModal.collectAsState()
    val pendingGift by viewModel.pendingGiftForPayment.collectAsState()
    val pendingRecipientName by viewModel.pendingRecipientName.collectAsState()

    var isHostingStream by remember { mutableStateOf(false) }
    var forceShowRegistration by remember { mutableStateOf(false) }

    // If profile is not registered, show Registration Screen first
    val isRegistered = myProfile?.isRegistered == true && !forceShowRegistration

    Scaffold(
        bottomBar = {
            if (isRegistered && activeWatchingStreamId == null && activeChatPartnerId == null && !isHostingStream) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppNavTab.DISCOVER,
                        onClick = { viewModel.currentTab.value = AppNavTab.DISCOVER },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Descubrir") },
                        label = { Text("Descubrir", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmoraRosePrimary,
                            selectedTextColor = AmoraRosePrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_discover")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.LIVE,
                        onClick = { viewModel.currentTab.value = AppNavTab.LIVE },
                        icon = { Icon(Icons.Default.Videocam, contentDescription = "En Vivo") },
                        label = { Text("En Vivo 🔴", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmoraRosePrimary,
                            selectedTextColor = AmoraRosePrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_live")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.CAMERA,
                        onClick = { viewModel.currentTab.value = AppNavTab.CAMERA },
                        icon = { Icon(Icons.Default.Camera, contentDescription = "Cámara") },
                        label = { Text("Cámara") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmoraRosePrimary,
                            selectedTextColor = AmoraRosePrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_camera")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.CHAT,
                        onClick = { viewModel.currentTab.value = AppNavTab.CHAT },
                        icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                        label = { Text("Chat") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmoraRosePrimary,
                            selectedTextColor = AmoraRosePrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_chat")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.PROFILE,
                        onClick = { viewModel.currentTab.value = AppNavTab.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Mi Perfil") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AmoraRosePrimary,
                            selectedTextColor = AmoraRosePrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isRegistered) {
                // Registration Flow with mandatory $100 MXN payment for men 20-32
                RegistrationScreen(
                    viewModel = viewModel,
                    onRegistrationCompleted = {
                        forceShowRegistration = false
                    }
                )
            } else if (activeWatchingStreamId != null) {
                // Live Stream Viewer
                LiveStreamViewerScreen(
                    streamId = activeWatchingStreamId!!,
                    viewModel = viewModel,
                    onCloseStream = {
                        viewModel.activeWatchingStreamId.value = null
                    }
                )
            } else if (isHostingStream) {
                // Live Stream Broadcast Studio Host
                HostStreamScreen(
                    viewModel = viewModel,
                    onStreamCreated = { streamId ->
                        isHostingStream = false
                        viewModel.activeWatchingStreamId.value = streamId
                    },
                    onCancel = {
                        isHostingStream = false
                    }
                )
            } else if (activeChatPartnerId != null) {
                // Direct Chat Screen
                ChatScreen(
                    partnerId = activeChatPartnerId!!,
                    viewModel = viewModel,
                    onBack = {
                        viewModel.activeChatPartnerId.value = null
                    }
                )
            } else {
                // Main Navigation Tabs
                when (currentTab) {
                    AppNavTab.DISCOVER -> {
                        DiscoverScreen(
                            viewModel = viewModel,
                            onOpenStream = { streamId ->
                                viewModel.activeWatchingStreamId.value = streamId
                            },
                            onOpenChat = { partnerId ->
                                viewModel.activeChatPartnerId.value = partnerId
                            }
                        )
                    }

                    AppNavTab.LIVE -> {
                        LiveStreamListScreen(
                            viewModel = viewModel,
                            onSelectStream = { streamId ->
                                viewModel.activeWatchingStreamId.value = streamId
                            },
                            onStartHostStream = {
                                isHostingStream = true
                            }
                        )
                    }

                    AppNavTab.CAMERA -> {
                        CameraStudioScreen(
                            viewModel = viewModel,
                            onPhotoCapturedForChat = { partnerId, photoUri ->
                                val targetPartner = partnerId ?: "p1"
                                viewModel.sendChatMessageText(targetPartner, "📷 Te compartí una foto", photoUri)
                                viewModel.activeChatPartnerId.value = targetPartner
                            },
                            onNavigateToLive = {
                                isHostingStream = true
                            }
                        )
                    }

                    AppNavTab.CHAT -> {
                        // Open chat list / default conversation
                        ChatScreen(
                            partnerId = activeChatPartnerId ?: "p1",
                            viewModel = viewModel,
                            onBack = {
                                viewModel.currentTab.value = AppNavTab.DISCOVER
                            }
                        )
                    }

                    AppNavTab.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToRegistration = {
                                forceShowRegistration = true
                            }
                        )
                    }
                }
            }

            // Card Tipping Payment Modal Overlay
            if (showCardPaymentModal && pendingGift != null) {
                CardTippingModal(
                    gift = pendingGift!!,
                    recipientName = pendingRecipientName,
                    onDismiss = { viewModel.closeCardPaymentModal() },
                    onConfirmPayment = { amount, last4, brand ->
                        viewModel.completeCardTipping(amount, last4, brand)
                    }
                )
            }
        }
    }
}
