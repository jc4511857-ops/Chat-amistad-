package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CameraPreview
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary

@Composable
fun CameraStudioScreen(
    viewModel: MainViewModel,
    onPhotoCapturedForChat: (partnerId: String?, photoUri: String) -> Unit,
    onNavigateToLive: () -> Unit
) {
    var isFrontCamera by remember { mutableStateOf(true) }
    var flashMode by remember { mutableStateOf(false) }
    var capturedPhotoUri by remember { mutableStateOf<String?>(null) }
    var showSuccessToast by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_studio_screen")
    ) {
        // Live Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            useFrontCamera = isFrontCamera
        )

        // Top Control Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { flashMode = !flashMode },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (flashMode) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (flashMode) AmoraGold else Color.White
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "ESTUDIO CÁMARA AMORA 📸",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            IconButton(
                onClick = { isFrontCamera = !isFrontCamera },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Girar cámara", tint = Color.White)
            }
        }

        // Bottom Capture Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(bottom = 90.dp, top = 20.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Go To Live Stream
                IconButton(
                    onClick = onNavigateToLive,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "En Vivo", tint = Color.White)
                }

                // Shutter Capture Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(AmoraRosePrimary)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable {
                            // Capture simulated photo
                            val photoUri = "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
                            capturedPhotoUri = photoUri
                            viewModel.lastCapturedMediaUri.value = photoUri
                            showSuccessToast = true
                        }
                        .testTag("shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                // Gallery filter preview
                IconButton(
                    onClick = {
                        val photoUri = "https://images.unsplash.com/photo-1517841905240-472988babdf9"
                        capturedPhotoUri = photoUri
                        viewModel.lastCapturedMediaUri.value = photoUri
                        showSuccessToast = true
                    },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toca el botón central para tomar foto o seleccionar foto para chat",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        // Success snackbar / bottom toast when captured
        if (showSuccessToast) {
            Snackbar(
                action = {
                    TextButton(
                        onClick = {
                            val activePartner = viewModel.activeChatPartnerId.value ?: "p1"
                            capturedPhotoUri?.let { uri ->
                                onPhotoCapturedForChat(activePartner, uri)
                            }
                            showSuccessToast = false
                        }
                    ) {
                        Text("Enviar a Chat ✉️", color = AmoraGold, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 160.dp, start = 16.dp, end = 16.dp)
            ) {
                Text("¡Foto capturada con éxito en Amora!")
            }
        }
    }
}
