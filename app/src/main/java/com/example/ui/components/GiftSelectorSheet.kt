package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary

data class VirtualGiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val priceMxn: Double
)

val AMORA_GIFTS = listOf(
    VirtualGiftItem("g1", "Rosa", "🌹", 20.0),
    VirtualGiftItem("g2", "Corazón", "💖", 50.0),
    VirtualGiftItem("g3", "Diamante", "💎", 150.0),
    VirtualGiftItem("g4", "Corona Real", "👑", 300.0),
    VirtualGiftItem("g5", "Auto Deportivo", "🏎️", 800.0),
    VirtualGiftItem("g6", "Cohete Amora", "🚀", 2000.0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftSelectorSheet(
    recipientName: String,
    onDismiss: () -> Unit,
    onGiftSelectedForCardPayment: (gift: VirtualGiftItem) -> Unit
) {
    var selectedGift by remember { mutableStateOf(AMORA_GIFTS[0]) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("gift_selector_sheet")
        ) {
            Text(
                text = "Regalos Virtuales en Vivo 🎁",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Demuestra tu apoyo a $recipientName enviándole un regalo con tarjeta de crédito o débito.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AMORA_GIFTS) { gift ->
                    val isSelected = gift.id == selectedGift.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) AmoraRosePrimary.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) AmoraRosePrimary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedGift = gift }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = gift.emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${gift.priceMxn.toInt()} MXN",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmoraGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onGiftSelectedForCardPayment(selectedGift)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("send_gift_card_button")
            ) {
                Text(
                    text = "Pagar ${selectedGift.emoji} ${selectedGift.name} ($${selectedGift.priceMxn.toInt()} MXN) con Tarjeta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
