package com.example.data.local

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AppRepository(private val db: AppDatabase) {

    val allProfiles: Flow<List<ProfileEntity>> = db.profileDao().getAllProfiles()
    val allTransactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val myProfile: Flow<MyProfileEntity?> = db.myProfileDao().getMyProfile()
    val savedCards: Flow<List<SavedCardEntity>> = db.savedCardDao().getSavedCards()

    fun getProfilesByRegion(region: String): Flow<List<ProfileEntity>> {
        return if (region == "Todas") {
            db.profileDao().getAllProfiles()
        } else {
            db.profileDao().getProfilesByRegion(region)
        }
    }

    fun getStreamsByRegion(region: String): Flow<List<LiveStreamEntity>> {
        return if (region == "Todas") {
            db.liveStreamDao().getActiveLiveStreams()
        } else {
            db.liveStreamDao().getLiveStreamsByRegion(region)
        }
    }

    fun getStreamComments(streamId: String): Flow<List<StreamCommentEntity>> {
        return db.streamCommentDao().getCommentsForStream(streamId)
    }

    fun getMessagesForPartner(partnerId: String): Flow<List<MessageEntity>> {
        return db.messageDao().getMessagesForPartner(partnerId)
    }

    suspend fun toggleLikeProfile(profileId: String, currentLiked: Boolean) {
        val newLiked = !currentLiked
        db.profileDao().updateLikeStatus(profileId, isLiked = newLiked, isMatched = newLiked)
    }

    suspend fun processCardTippingPayment(
        recipientId: String,
        recipientName: String,
        giftName: String,
        giftIconEmoji: String,
        amountMxn: Double,
        cardNumberLast4: String,
        cardBrand: String,
        streamId: String? = null
    ) {
        val tx = TransactionEntity(
            transactionId = "TX-" + System.currentTimeMillis().toString().takeLast(8),
            recipientId = recipientId,
            recipientName = recipientName,
            giftName = giftName,
            giftIconEmoji = giftIconEmoji,
            amountMxn = amountMxn,
            paymentMethod = "$cardBrand (**** $cardNumberLast4)",
            status = "Completado"
        )
        db.transactionDao().insertTransaction(tx)

        if (streamId != null) {
            db.liveStreamDao().addEarnings(streamId, amountMxn)
            db.streamCommentDao().insertComment(
                StreamCommentEntity(
                    streamId = streamId,
                    senderName = "Tú",
                    senderAvatarUrl = "",
                    message = "¡Envió un regalo $giftIconEmoji $giftName ($${amountMxn.toInt()} MXN)!",
                    giftType = giftName,
                    giftAmountMxn = amountMxn
                )
            )
        }
    }

    suspend fun sendStreamComment(streamId: String, senderName: String, message: String) {
        db.streamCommentDao().insertComment(
            StreamCommentEntity(
                streamId = streamId,
                senderName = senderName,
                senderAvatarUrl = "",
                message = message
            )
        )
    }

    suspend fun sendDirectMessage(
        partnerId: String,
        text: String,
        imageUri: String? = null,
        giftType: String? = null,
        giftAmountMxn: Double = 0.0
    ) {
        db.messageDao().insertMessage(
            MessageEntity(
                conversationPartnerId = partnerId,
                senderId = "me",
                text = text,
                imageUri = imageUri,
                giftType = giftType,
                giftAmountMxn = giftAmountMxn,
                isFromMe = true
            )
        )
    }

    // Save profile and check if payment is required
    // Male 20-32 => needs $100 MXN payment
    // Under 20 (11-19) or Women or outside 20-32 => FREE ($0)
    suspend fun checkAndSaveRegistrationProfile(
        name: String,
        age: Int,
        gender: String,
        region: String,
        bio: String
    ): Boolean {
        val requiresPayment = gender.equals("Hombre", ignoreCase = true) && age in 20..32

        val profile = MyProfileEntity(
            id = "me",
            name = name,
            age = age,
            gender = gender,
            region = region,
            bio = bio,
            isRegistered = !requiresPayment, // If requires payment, not fully registered until payment completes
            hasPaidRegistrationFee = !requiresPayment
        )

        db.myProfileDao().saveMyProfile(profile)
        return requiresPayment
    }

    suspend fun processRegistrationPayment(
        cardHolder: String,
        cardNumberLast4: String,
        cardBrand: String,
        expiry: String,
        currentProfile: MyProfileEntity,
        saveCardForFuture: Boolean = true
    ) {
        // Record registration transaction of $100 MXN
        val tx = TransactionEntity(
            transactionId = "REG-" + System.currentTimeMillis().toString().takeLast(8),
            recipientId = "AMORA_MEXICO",
            recipientName = "Membresía Amora México (Hombres 20-32)",
            giftName = "Membresía de Registro",
            giftIconEmoji = "💳",
            amountMxn = 100.0,
            paymentMethod = "$cardBrand (**** $cardNumberLast4)",
            status = "Completado"
        )
        db.transactionDao().insertTransaction(tx)

        if (saveCardForFuture) {
            db.savedCardDao().insertCard(
                SavedCardEntity(
                    cardHolder = cardHolder,
                    last4 = cardNumberLast4,
                    expiry = expiry,
                    brand = cardBrand,
                    isDefault = true
                )
            )
        }

        // Update profile as registered & paid
        val updatedProfile = currentProfile.copy(
            isRegistered = true,
            hasPaidRegistrationFee = true
        )
        db.myProfileDao().saveMyProfile(updatedProfile)
    }

    suspend fun addSavedCard(cardHolder: String, last4: String, expiry: String, brand: String) {
        db.savedCardDao().insertCard(
            SavedCardEntity(
                cardHolder = cardHolder,
                last4 = last4,
                expiry = expiry,
                brand = brand,
                isDefault = true
            )
        )
    }

    suspend fun depositFundsToWallet(amountMxn: Double, cardLast4: String, brand: String, currentProfile: MyProfileEntity) {
        val tx = TransactionEntity(
            transactionId = "DEP-" + System.currentTimeMillis().toString().takeLast(8),
            recipientId = "ME",
            recipientName = "Depósito a Monedero Amora",
            giftName = "Saldo en Monedero",
            giftIconEmoji = "💰",
            amountMxn = amountMxn,
            paymentMethod = "$brand (**** $cardLast4)",
            status = "Completado"
        )
        db.transactionDao().insertTransaction(tx)

        val updated = currentProfile.copy(
            walletBalanceMxn = currentProfile.walletBalanceMxn + amountMxn
        )
        db.myProfileDao().saveMyProfile(updated)
    }

    suspend fun createLiveStream(hostName: String, region: String, title: String, category: String): String {
        val streamId = "stream_" + UUID.randomUUID().toString().take(8)
        val stream = LiveStreamEntity(
            streamId = streamId,
            hostId = "host_me",
            hostName = hostName,
            hostAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            hostRegion = region,
            title = title,
            category = category,
            viewersCount = 1,
            totalEarningsMxn = 0.0,
            coverImageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4"
        )
        db.liveStreamDao().insertOrUpdateStream(stream)
        return streamId
    }

    suspend fun seedInitialDataIfEmpty() {
        val sampleProfiles = listOf(
            ProfileEntity(
                id = "p1",
                name = "Valentina López",
                age = 23,
                region = "Copándaro de Galeana",
                bio = "¡Orgullosamente de Copándaro de Galeana, Michoacán! 🌾✨ Sígueme en TikTok e Instagram.",
                imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                galleryImages = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                occupation = "Creadora de Contenido",
                interests = "Copándaro, TikTok, Tradiciones, Café",
                isOnline = true,
                isStreaming = true,
                activeStreamId = "s1",
                likesCount = 1240,
                distanceKm = 2,
                verifiedBadge = true,
                tiktokHandle = "@valentina_copandaro",
                instagramHandle = "valentina.michoacan",
                facebookHandle = "Valentina Copándaro"
            ),
            ProfileEntity(
                id = "p2",
                name = "Mariana Arriaga",
                age = 22,
                region = "Santa Rita de Casia",
                bio = "Desde Santa Rita de Casia, Michoacán 🌸 Disfrutando del campo y la buena compañía.",
                imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                galleryImages = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                occupation = "Emprendedora",
                interests = "Santa Rita, Fotografía, Moda, Redes",
                isOnline = true,
                isStreaming = false,
                likesCount = 820,
                distanceKm = 4,
                verifiedBadge = true,
                tiktokHandle = "@mariana_santarita",
                instagramHandle = "mariana.casia",
                facebookHandle = "Mariana Santa Rita"
            ),
            ProfileEntity(
                id = "p3",
                name = "Camila Fernández",
                age = 21,
                region = "Nispo",
                bio = "De Nispo, Michoacán ☀️ Amante de los bailes tradicionales y conectar en redes.",
                imageUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                galleryImages = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                occupation = "Estudiante",
                interests = "Nispo, Música, Baile, TikTok",
                isOnline = true,
                isStreaming = true,
                activeStreamId = "s3",
                likesCount = 890,
                distanceKm = 5,
                verifiedBadge = true,
                tiktokHandle = "@camila_nispo",
                instagramHandle = "camila.nispo.mich",
                facebookHandle = "Camila Nispo"
            ),
            ProfileEntity(
                id = "p4",
                name = "Sofía Morales",
                age = 24,
                region = "La Cañada de la Yerbabuena",
                bio = "De La Cañada de la Yerbabuena, Michoacán 🌿 Amante de la naturaleza y redes sociales.",
                imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                galleryImages = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                occupation = "Instructora de Nutrición",
                interests = "Yerbabuena, Naturaleza, Fitness, FB",
                isOnline = true,
                isStreaming = false,
                likesCount = 670,
                distanceKm = 7,
                verifiedBadge = true,
                tiktokHandle = "@sofia_yerbabuena",
                instagramHandle = "sofia.yerbabuena",
                facebookHandle = "Sofia Yerbabuena Mich"
            ),
            ProfileEntity(
                id = "p5",
                name = "Isabella Benítez",
                age = 23,
                region = "San Agustín Arúmbaro",
                bio = "De San Agustín Arúmbaro, Michoacán 🏰 Compartiendo la cultura y tradición michoacana.",
                imageUrl = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e",
                galleryImages = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e",
                occupation = "Chef Tradicional",
                interests = "San Agustín, Gastronomía, Gastronomía Michoacana",
                isOnline = true,
                isStreaming = false,
                likesCount = 930,
                distanceKm = 6,
                verifiedBadge = true,
                tiktokHandle = "@isabella_arumbaro",
                instagramHandle = "isabella.arumbaro",
                facebookHandle = "Isabella Arumbaro"
            )
        )
        db.profileDao().insertProfiles(sampleProfiles)

        val sampleStreams = listOf(
            LiveStreamEntity(
                streamId = "s1",
                hostId = "p1",
                hostName = "Valentina López",
                hostAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                hostRegion = "CDMX",
                title = "🎙️ Platicando y Tocando Guitarra Acoustic",
                category = "Música",
                viewersCount = 1420,
                totalEarningsMxn = 4200.0,
                coverImageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4"
            ),
            LiveStreamEntity(
                streamId = "s3",
                hostId = "p3",
                hostName = "Camila Fernández",
                hostAvatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                hostRegion = "Guadalajara",
                title = "💃 Ensayo de Baile Latino & Charla Live",
                category = "Baile",
                viewersCount = 2890,
                totalEarningsMxn = 8900.0,
                coverImageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7"
            )
        )
        db.liveStreamDao().insertStreams(sampleStreams)
    }
}
