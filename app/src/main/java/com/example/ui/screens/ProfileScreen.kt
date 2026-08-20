package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.SavedCardEntity
import com.example.data.local.TransactionEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import com.example.ui.theme.OnlineGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToRegistration: () -> Unit
) {
    val myProfile by viewModel.myProfile.collectAsState()
    val savedCards by viewModel.savedCards.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    var showAddCardModal by remember { mutableStateOf(false) }
    var showDepositModal by remember { mutableStateOf(false) }

    // New card input fields
    var newCardHolder by remember { mutableStateOf("") }
    var newCardNumber by remember { mutableStateOf("") }
    var newCardExpiry by remember { mutableStateOf("") }
    var newCardBrand by remember { mutableStateOf("Visa") }

    // Deposit amount
    var depositAmountText by remember { mutableStateOf("200") }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen")
    ) {
        item {
            // Profile Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AmoraRosePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (myProfile != null) myProfile!!.name.take(1).uppercase() else "A",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = myProfile?.name ?: "Usuario Amora",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${myProfile?.gender ?: "Hombre"} • ${myProfile?.age ?: 24} años • 📍 ${myProfile?.region ?: "CDMX"}, México",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Registration & Membership Badge
                    val requiresPayment = myProfile?.gender.equals("Hombre", ignoreCase = true) && (myProfile?.age ?: 24) in 20..32
                    val hasPaid = myProfile?.hasPaidRegistrationFee ?: false

                    Surface(
                        color = if (hasPaid || !requiresPayment) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasPaid || !requiresPayment) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (hasPaid || !requiresPayment) OnlineGreen else Color(0xFFE65100),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasPaid && requiresPayment) "Membresía Pagada ($100 MXN Activo)"
                                else if (!requiresPayment) "Registro Gratuito Activo"
                                else "Pendiente de Pago ($100 MXN)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasPaid || !requiresPayment) Color(0xFF2E7D32) else Color(0xFFBF360C)
                            )
                        }
                    }

                    if (requiresPayment && !hasPaid) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToRegistration,
                            colors = ButtonDefaults.buttonColors(containerColor = AmoraGold)
                        ) {
                            Text("Completar Pago de $100 MXN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Wallet / Monedero Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AmoraGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Monedero Digital Amora",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showDepositModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("➕ Depositar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Saldo Disponible: $${(myProfile?.walletBalanceMxn ?: 0.0).toInt()} MXN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Usa tu saldo para enviar regalos virtuales y super me gustas en transmisiones",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            // Saved Payment Cards Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Tarjetas Guardadas 💳",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { showAddCardModal = true }) {
                    Text("➕ Agregar Tarjeta", color = AmoraRosePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (savedCards.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes tarjetas de crédito o débito guardadas aún.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(savedCards) { card ->
                SavedCardItem(card = card)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Historial de Transacciones 📜",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay transacciones registradas.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(transactions) { tx ->
                TransactionRowItem(tx = tx)
            }
        }
    }

    // Modal to Add Credit Card
    if (showAddCardModal) {
        AlertDialog(
            onDismissRequest = { showAddCardModal = false },
            title = { Text("Agregar Tarjeta de Crédito / Débito") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Visa", "Mastercard", "AMEX").forEach { brand ->
                            FilterChip(
                                selected = newCardBrand == brand,
                                onClick = { newCardBrand = brand },
                                label = { Text(brand) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newCardHolder,
                        onValueChange = { newCardHolder = it },
                        label = { Text("Titular de la tarjeta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCardNumber,
                        onValueChange = { newCardNumber = it.take(16) },
                        label = { Text("Número de Tarjeta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCardExpiry,
                        onValueChange = { newCardExpiry = it.take(5) },
                        label = { Text("Expiración (MM/AA)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val last4 = if (newCardNumber.length >= 4) newCardNumber.takeLast(4) else "1234"
                        val holder = if (newCardHolder.isBlank()) (myProfile?.name ?: "Titular") else newCardHolder
                        viewModel.addNewCreditCard(
                            cardHolder = holder,
                            last4 = last4,
                            expiry = if (newCardExpiry.isBlank()) "12/28" else newCardExpiry,
                            brand = newCardBrand
                        )
                        showAddCardModal = false
                        newCardNumber = ""
                        newCardHolder = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary)
                ) {
                    Text("Guardar Tarjeta")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal to Deposit to Wallet
    if (showDepositModal) {
        AlertDialog(
            onDismissRequest = { showDepositModal = false },
            title = { Text("Hacer Depósito a Monedero") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ingresa el monto a depositar en pesos mexicanos (MXN):")

                    OutlinedTextField(
                        value = depositAmountText,
                        onValueChange = { depositAmountText = it },
                        label = { Text("Monto ($ MXN)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val defaultCard = savedCards.firstOrNull()
                    if (defaultCard != null) {
                        Text(
                            text = "Se cargará a: ${defaultCard.brand} (**** ${defaultCard.last4})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmoraRosePrimary
                        )
                    } else {
                        Text(
                            text = "Se usará tu tarjeta de crédito o débito registrada.",
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = depositAmountText.toDoubleOrNull() ?: 200.0
                        val cardLast4 = savedCards.firstOrNull()?.last4 ?: "4242"
                        val brand = savedCards.firstOrNull()?.brand ?: "Visa"
                        viewModel.depositToWallet(amount, cardLast4, brand)
                        showDepositModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoraGold)
                ) {
                    Text("Confirmar Depósito", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SavedCardItem(card: SavedCardEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = AmoraRosePrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${card.brand} **** ${card.last4}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Titular: ${card.cardHolder} • Exp: ${card.expiry}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (card.isDefault) {
                Surface(
                    color = AmoraGold,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Principal",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(tx: TransactionEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = tx.giftIconEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = tx.recipientName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = tx.paymentMethod, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                text = "$${tx.amountMxn.toInt()} MXN",
                fontWeight = FontWeight.Bold,
                color = AmoraRosePrimary,
                fontSize = 14.sp
            )
        }
    }
}
