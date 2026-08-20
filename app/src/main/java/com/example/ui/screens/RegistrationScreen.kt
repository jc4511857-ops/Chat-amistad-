package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.MyProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.AmoraGold
import com.example.ui.theme.AmoraRosePrimary
import com.example.ui.theme.OnlineGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: MainViewModel,
    onRegistrationCompleted: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("24") }
    var selectedGender by remember { mutableStateOf("Hombre") }
    var selectedRegion by remember { mutableStateOf("Copándaro de Galeana") }
    var bio by remember { mutableStateOf("¡Hola! Me alegra estar en Amora México 🇲🇽") }

    // Navigation step within Registration: 0 = Profile Info Form, 1 = Payment Screen ($100 MXN)
    var currentStep by remember { mutableStateOf(0) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Payment Form States
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardBrand by remember { mutableStateOf("Visa") }
    var saveCardChecked by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val parsedAge = ageText.toIntOrNull() ?: 20
    val isMaleAge20To32 = selectedGender.equals("Hombre", ignoreCase = true) && parsedAge in 20..32
    val isUnder20Free = parsedAge in 11..19

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("registration_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Logo Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    color = AmoraRosePrimary.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🇲🇽", fontSize = 28.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Amora México",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = AmoraRosePrimary
                    )
                    Text(
                        text = "Citas y Transmisiones En Vivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Step Indicator Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentStep == 0) "Paso 1: Información de Perfil" else "Paso 2: Membresía de Registro ($100 MXN)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentStep == 0) {
                                if (isMaleAge20To32) "⚠️ Hombres de 20 a 32 años requieren cuota de $100 MXN"
                                else if (isUnder20Free) "🎉 Menores de 20 a 11 años: ¡GRATUITO!"
                                else "✨ Registro Gratuito"
                            } else "Ingresa tu tarjeta de crédito o débito para procesar tu registro",
                            fontSize = 12.sp,
                            color = if (isMaleAge20To32 && currentStep == 0) AmoraRosePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = if (currentStep == 1) AmoraGold else AmoraRosePrimary,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${currentStep + 1}/2",
                            color = if (currentStep == 1) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = currentStep == 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Crea tu Cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("Edad (años)") },
                            leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reg_age_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Region Selector dropdown/field
                        OutlinedTextField(
                            value = selectedRegion,
                            onValueChange = { selectedRegion = it },
                            label = { Text("Región México") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reg_region_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // Gender Selection
                    Text(
                        text = "Género",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Hombre", "Mujer", "Otro").forEach { g ->
                            val selected = selectedGender.equals(g, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = { selectedGender = g },
                                label = { Text(g, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    if (selected) Icon(Icons.Default.Check, contentDescription = null)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("gender_chip_$g"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmoraRosePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Automatic Notice Banner according to age & gender rules
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMaleAge20To32) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMaleAge20To32) "💳" else "🎁",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isMaleAge20To32) {
                                        "Membresía Hombres (20 a 32 años): $100 pesos MXN"
                                    } else if (isUnder20Free) {
                                        "Membresía Menores (11 a 19 años): GRATUITO ($0 pesos)"
                                    } else {
                                        "Membresía Promocional: GRATUITO ($0 pesos)"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isMaleAge20To32) Color(0xFFE65100) else Color(0xFF2E7D32)
                                )
                                Text(
                                    text = if (isMaleAge20To32) {
                                        "Al presionar Continuar serás redirigido automáticamente a la pasarela de pago para ingresar tu tarjeta de crédito o débito."
                                    } else {
                                        "Tu registro es 100% gratuito. ¡Puedes comenzar a chatear y ver transmisiones en vivo de inmediato!"
                                    },
                                    fontSize = 11.sp,
                                    color = if (isMaleAge20To32) Color(0xFFBF360C) else Color(0xFF1B5E20)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Acerca de ti (Biografía)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Por favor ingresa tu nombre completo"
                                return@Button
                            }
                            errorMessage = null
                            coroutineScope.launch {
                                val requiresPayment = viewModel.saveRegistrationProfile(
                                    name = name,
                                    age = parsedAge,
                                    gender = selectedGender,
                                    region = selectedRegion,
                                    bio = bio
                                )

                                if (requiresPayment) {
                                    // Redirect automatically to payment page
                                    cardHolder = name
                                    currentStep = 1
                                } else {
                                    // Free registration complete!
                                    onRegistrationCompleted()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmoraRosePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("reg_continue_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isMaleAge20To32) "Continuar al Pago de $100 MXN 💳" else "Completar Registro Gratuito ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            AnimatedVisibility(visible = currentStep == 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Pago de Membresía Amora 🇲🇽",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Obligatorio para hombres de 20 a 32 años",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$100 MXN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = AmoraGold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Método de Pago con Tarjeta de Crédito / Débito",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Visa", "Mastercard", "AMEX").forEach { brand ->
                            val isSelected = cardBrand == brand
                            OutlinedButton(
                                onClick = { cardBrand = brand },
                                modifier = Modifier.weight(1f),
                                border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(AmoraRosePrimary, AmoraGold))) else ButtonDefaults.outlinedButtonBorder,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) AmoraRosePrimary.copy(alpha = 0.15f) else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = brand,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AmoraRosePrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Nombre del Titular de la Tarjeta") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_card_holder_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it.take(16) },
                        label = { Text("Número de Tarjeta (16 dígitos)") },
                        placeholder = { Text("4242 4242 4242 4242") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_card_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { cardExpiry = it.take(5) },
                            label = { Text("Exp. (MM/AA)") },
                            placeholder = { Text("12/28") },
                            modifier = Modifier.weight(1f).testTag("reg_card_expiry_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = { cardCvv = it.take(4) },
                            label = { Text("CVV") },
                            placeholder = { Text("123") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f).testTag("reg_card_cvv_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { saveCardChecked = !saveCardChecked }
                    ) {
                        Checkbox(
                            checked = saveCardChecked,
                            onCheckedChange = { saveCardChecked = it }
                        )
                        Text(
                            text = "Guardar esta tarjeta para depósitos y envío de regalos en mi monedero",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = {
                                if (cardNumber.length < 4) {
                                    errorMessage = "Por favor ingresa un número de tarjeta válido"
                                    return@Button
                                }
                                isProcessing = true
                                val last4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else "4242"
                                val currentProf = viewModel.myProfile.value ?: MyProfileEntity(
                                    name = name,
                                    age = parsedAge,
                                    gender = selectedGender,
                                    region = selectedRegion,
                                    bio = bio
                                )

                                viewModel.completeRegistrationPayment(
                                    cardHolder = if (cardHolder.isBlank()) name else cardHolder,
                                    cardNumberLast4 = last4,
                                    cardBrand = cardBrand,
                                    expiry = if (cardExpiry.isBlank()) "12/28" else cardExpiry,
                                    currentProfile = currentProf,
                                    saveCard = saveCardChecked,
                                    onSuccess = {
                                        isProcessing = false
                                        onRegistrationCompleted()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmoraGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("process_100_mxn_payment_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Procesar Pago de $100 MXN 🔒",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }

                    TextButton(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Regresar al Formulario de Perfil")
                    }
                }
            }
        }
    }
}
