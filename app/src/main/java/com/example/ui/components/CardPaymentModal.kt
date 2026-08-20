package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedCardEntity
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPaymentModal(
    gift: VirtualGiftItem,
    recipientName: String,
    savedCards: List<SavedCardEntity> = emptyList(),
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, last4: String, brand: String) -> Unit
) {
    CardPaymentModal(
        recipientName = recipientName,
        giftName = gift.name,
        giftIconEmoji = gift.emoji,
        defaultAmountMxn = gift.priceMxn,
        onDismiss = onDismiss,
        onPaymentSuccess = onConfirmPayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPaymentModal(
    recipientName: String,
    giftName: String,
    giftIconEmoji: String,
    defaultAmountMxn: Double,
    onDismiss: () -> Unit,
    onPaymentSuccess: (amount: Double, last4: String, brand: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0: Tarjeta, 1: OXXO Pay / SPEI
    var cardNumber by remember { mutableStateOf("4532 8901 2345 9012") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvc by remember { mutableStateOf("882") }
    var cardHolder by remember { mutableStateOf("JUAN PÉREZ") }
    var customAmount by remember { mutableStateOf(defaultAmountMxn.toInt().toString()) }

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessReceipt by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("card_payment_modal")
        ) {
            if (showSuccessReceipt) {
                // Receipt view
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Pago Exitoso",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Pago Completado!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enviaste $giftIconEmoji $giftName a $recipientName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Monto:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${customAmount} MXN", fontWeight = FontWeight.Bold, color = AmoraGold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Método:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Tarjeta **** ${cardNumber.takeLast(4)}", fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estado:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Aprobado (BBVA Bancomer)", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amount = customAmount.toDoubleOrNull() ?: defaultAmountMxn
                            val last4 = cardNumber.filter { it.isDigit() }.takeLast(4).ifEmpty { "9012" }
                            onPaymentSuccess(amount, last4, "Visa/Mastercard")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("close_receipt_button")
                    ) {
                        Text("Ver Regalo en Vivo ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Payment Form
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pago seguro con Tarjeta 💳",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Regalo $giftIconEmoji $giftName para $recipientName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment method tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Tarjeta Débito/Crédito", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("OXXO / SPEI", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount adjustment
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it.filter { char -> char.isDigit() } },
                    label = { Text("Monto del Regalo / Propina (MXN $)") },
                    prefix = { Text("$ ", fontWeight = FontWeight.Bold, color = AmoraGold) },
                    suffix = { Text("MXN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // Card Form
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Número de Tarjeta") },
                        placeholder = { Text("4532 8901 2345 9012") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                                Text("VISA / MC", style = MaterialTheme.typography.labelSmall, color = AmoraRosePrimary, fontWeight = FontWeight.Bold)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { cardExpiry = it },
                            label = { Text("Vencimiento") },
                            placeholder = { Text("MM/AA") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = cardCvc,
                            onValueChange = { cardCvc = it },
                            label = { Text("CVC / CVV") },
                            placeholder = { Text("123") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Nombre en la Tarjeta") },
                        placeholder = { Text("TAL COMO APARECE EN LA TARJETA") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Protegido",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Procesamiento seguro 256-bit SSL en México",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // OXXO / SPEI option info
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🏧 Ficha de Depósito OXXO Pay / SPEI",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Genera tu referencia para pagar en cualquier tienda OXXO o por transferencia bancaria SPEI al instante.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Referencia OXXO: 8901-2345-9812",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmoraGold
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (cardNumber.isBlank() || cardExpiry.isBlank() || cardCvc.isBlank()) {
                            errorMessage = "Por favor completa todos los datos de la tarjeta."
                            return@Button
                        }
                        isProcessing = true
                        errorMessage = null

                        // Simulate processing
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1200)
                            isProcessing = false
                            showSuccessReceipt = true
                        }
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("process_card_payment_button")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        val amountStr = customAmount.ifEmpty { defaultAmountMxn.toInt().toString() }
                        Text(
                            text = "Pagar $giftIconEmoji $giftName ($${amountStr} MXN)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CardTippingModal(
    gift: VirtualGiftItem,
    recipientName: String,
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, last4: String, brand: String) -> Unit
) {
    CardPaymentModal(
        recipientName = recipientName,
        giftName = gift.name,
        giftIconEmoji = gift.emoji,
        defaultAmountMxn = gift.priceMxn,
        onDismiss = onDismiss,
        onPaymentSuccess = onConfirmPayment
    )
}
