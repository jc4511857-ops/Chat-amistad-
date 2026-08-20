package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val region: String, // e.g. CDMX, Guadalajara, Monterrey, Cancún, Puebla, Tijuana, Mérida
    val bio: String,
    val imageUrl: String,
    val galleryImages: String, // Comma-separated list of image URLs/res
    val occupation: String,
    val interests: String, // Comma-separated
    val isOnline: Boolean,
    val isStreaming: Boolean,
    val activeStreamId: String? = null,
    val likesCount: Int,
    val isLikedByMe: Boolean = false,
    val isMatched: Boolean = false,
    val distanceKm: Int,
    val verifiedBadge: Boolean = true,
    val tiktokHandle: String? = null,
    val instagramHandle: String? = null,
    val facebookHandle: String? = null
)

@Entity(tableName = "live_streams")
data class LiveStreamEntity(
    @PrimaryKey val streamId: String,
    val hostId: String,
    val hostName: String,
    val hostAvatarUrl: String,
    val hostRegion: String,
    val title: String,
    val category: String, // Música, Charla, Baile, Estilo de Vida
    val viewersCount: Int,
    val totalEarningsMxn: Double,
    val isLive: Boolean = true,
    val coverImageUrl: String
)

@Entity(tableName = "stream_comments")
data class StreamCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val streamId: String,
    val senderName: String,
    val senderAvatarUrl: String,
    val message: String,
    val giftType: String? = null,
    val giftAmountMxn: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationPartnerId: String,
    val senderId: String,
    val text: String,
    val imageUri: String? = null,
    val giftType: String? = null,
    val giftAmountMxn: Double = 0.0,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val transactionId: String,
    val recipientId: String,
    val recipientName: String,
    val giftName: String,
    val giftIconEmoji: String,
    val amountMxn: Double,
    val paymentMethod: String, // e.g., "Tarjeta Crédito (**** 4242)", "Tarjeta Débito (**** 8819)"
    val status: String = "Completado",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "my_profile")
data class MyProfileEntity(
    @PrimaryKey val id: String = "me",
    val name: String,
    val age: Int,
    val gender: String, // "Hombre", "Mujer", "Otro"
    val region: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val isRegistered: Boolean = false,
    val hasPaidRegistrationFee: Boolean = false,
    val walletBalanceMxn: Double = 0.0
)

@Entity(tableName = "saved_cards")
data class SavedCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardHolder: String,
    val last4: String,
    val expiry: String,
    val brand: String, // "Visa", "Mastercard", "AMEX"
    val isDefault: Boolean = true
)
