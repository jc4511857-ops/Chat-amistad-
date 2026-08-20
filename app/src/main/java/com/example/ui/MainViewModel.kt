package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.ui.components.VirtualGiftItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab {
    DISCOVER, LIVE, CAMERA, CHAT, PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AppRepository(db)

    // Current Navigation Tab
    var currentTab = MutableStateFlow(AppNavTab.DISCOVER)

    // Selected Mexico Region Filter ("Todas", "CDMX", "Guadalajara", "Monterrey", "Cancún", "Puebla", "Tijuana", "Mérida", "Querétaro", "Oaxaca")
    val selectedRegion = MutableStateFlow("Todas")

    // Active Live Stream being watched
    val activeWatchingStreamId = MutableStateFlow<String?>(null)

    // Active Chat Partner ID
    val activeChatPartnerId = MutableStateFlow<String?>(null)

    // Tipping Card Modal state
    val showCardPaymentModal = MutableStateFlow(false)
    val pendingGiftForPayment = MutableStateFlow<VirtualGiftItem?>(null)
    val pendingRecipientName = MutableStateFlow("")
    val pendingRecipientId = MutableStateFlow("")
    val pendingStreamId = MutableStateFlow<String?>(null)

    // Detailed Profile Bottom Sheet
    val selectedProfileDetail = MutableStateFlow<ProfileEntity?>(null)

    // Camera Studio Captured Media
    val lastCapturedMediaUri = MutableStateFlow<String?>(null)

    // User's own Profile Flow
    val myProfile = repository.myProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // User's Saved Cards Flow
    val savedCards = repository.savedCards.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Reactive streams from Repository
    val profiles = selectedRegion.flatMapLatest { region ->
        repository.getProfilesByRegion(region)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveStreams = selectedRegion.flatMapLatest { region ->
        repository.getStreamsByRegion(region)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val currentStreamComments = activeWatchingStreamId.flatMapLatest { streamId ->
        if (streamId != null) repository.getStreamComments(streamId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentChatMessages = activeChatPartnerId.flatMapLatest { partnerId ->
        if (partnerId != null) repository.getMessagesForPartner(partnerId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectRegion(region: String) {
        selectedRegion.value = region
    }

    fun toggleLike(profileId: String, currentLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLikeProfile(profileId, currentLiked)
        }
    }

    fun openCardPaymentForGift(
        gift: VirtualGiftItem,
        recipientId: String,
        recipientName: String,
        streamId: String? = null
    ) {
        pendingGiftForPayment.value = gift
        pendingRecipientId.value = recipientId
        pendingRecipientName.value = recipientName
        pendingStreamId.value = streamId
        showCardPaymentModal.value = true
    }

    fun closeCardPaymentModal() {
        showCardPaymentModal.value = false
        pendingGiftForPayment.value = null
    }

    fun completeCardTipping(amount: Double, last4: String, brand: String) {
        val gift = pendingGiftForPayment.value ?: return
        val recipientId = pendingRecipientId.value
        val recipientName = pendingRecipientName.value
        val streamId = pendingStreamId.value

        viewModelScope.launch {
            repository.processCardTippingPayment(
                recipientId = recipientId,
                recipientName = recipientName,
                giftName = gift.name,
                giftIconEmoji = gift.emoji,
                amountMxn = amount,
                cardNumberLast4 = last4,
                cardBrand = brand,
                streamId = streamId
            )

            // If tipping in direct chat, post a gift message in chat
            if (activeChatPartnerId.value == recipientId) {
                repository.sendDirectMessage(
                    partnerId = recipientId,
                    text = "¡Te envié un regalo ${gift.emoji} ${gift.name} de $${amount.toInt()} MXN!",
                    giftType = gift.name,
                    giftAmountMxn = amount
                )
            }

            closeCardPaymentModal()
        }
    }

    fun sendStreamCommentText(messageText: String) {
        val streamId = activeWatchingStreamId.value ?: return
        if (messageText.isBlank()) return
        viewModelScope.launch {
            repository.sendStreamComment(
                streamId = streamId,
                senderName = "Tú",
                message = messageText
            )
        }
    }

    fun sendChatMessageText(partnerId: String, text: String, imageUri: String? = null) {
        if (text.isBlank() && imageUri == null) return
        viewModelScope.launch {
            repository.sendDirectMessage(
                partnerId = partnerId,
                text = text,
                imageUri = imageUri
            )
        }
    }

    fun createAndStartHostStream(hostName: String, region: String, title: String, category: String) {
        viewModelScope.launch {
            val newStreamId = repository.createLiveStream(hostName, region, title, category)
            activeWatchingStreamId.value = newStreamId
        }
    }

    suspend fun saveRegistrationProfile(
        name: String,
        age: Int,
        gender: String,
        region: String,
        bio: String
    ): Boolean {
        return repository.checkAndSaveRegistrationProfile(name, age, gender, region, bio)
    }

    fun completeRegistrationPayment(
        cardHolder: String,
        cardNumberLast4: String,
        cardBrand: String,
        expiry: String,
        currentProfile: MyProfileEntity,
        saveCard: Boolean = true,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.processRegistrationPayment(
                cardHolder = cardHolder,
                cardNumberLast4 = cardNumberLast4,
                cardBrand = cardBrand,
                expiry = expiry,
                currentProfile = currentProfile,
                saveCardForFuture = saveCard
            )
            onSuccess()
        }
    }

    fun addNewCreditCard(cardHolder: String, last4: String, expiry: String, brand: String) {
        viewModelScope.launch {
            repository.addSavedCard(cardHolder, last4, expiry, brand)
        }
    }

    fun depositToWallet(amountMxn: Double, cardLast4: String, brand: String) {
        val prof = myProfile.value ?: return
        viewModelScope.launch {
            repository.depositFundsToWallet(amountMxn, cardLast4, brand, prof)
        }
    }
}
