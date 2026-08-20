package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY isStreaming DESC, isOnline DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE region = :region ORDER BY isStreaming DESC, isOnline DESC")
    fun getProfilesByRegion(region: String): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Query("UPDATE profiles SET isLikedByMe = :isLiked, isMatched = :isMatched WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, isMatched: Boolean)
}

@Dao
interface LiveStreamDao {
    @Query("SELECT * FROM live_streams WHERE isLive = 1 ORDER BY viewersCount DESC")
    fun getActiveLiveStreams(): Flow<List<LiveStreamEntity>>

    @Query("SELECT * FROM live_streams WHERE hostRegion = :region AND isLive = 1 ORDER BY viewersCount DESC")
    fun getLiveStreamsByRegion(region: String): Flow<List<LiveStreamEntity>>

    @Query("SELECT * FROM live_streams WHERE streamId = :streamId")
    suspend fun getStreamById(streamId: String): LiveStreamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStream(stream: LiveStreamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<LiveStreamEntity>)

    @Query("UPDATE live_streams SET viewersCount = viewersCount + :delta WHERE streamId = :streamId")
    suspend fun updateViewerCount(streamId: String, delta: Int)

    @Query("UPDATE live_streams SET totalEarningsMxn = totalEarningsMxn + :amount WHERE streamId = :streamId")
    suspend fun addEarnings(streamId: String, amount: Double)
}

@Dao
interface StreamCommentDao {
    @Query("SELECT * FROM stream_comments WHERE streamId = :streamId ORDER BY timestamp ASC")
    fun getCommentsForStream(streamId: String): Flow<List<StreamCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: StreamCommentEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationPartnerId = :partnerId ORDER BY timestamp ASC")
    fun getMessagesForPartner(partnerId: String): Flow<List<MessageEntity>>

    @Query("SELECT DISTINCT conversationPartnerId FROM messages")
    fun getActiveConversations(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface MyProfileDao {
    @Query("SELECT * FROM my_profile WHERE id = 'me'")
    fun getMyProfile(): Flow<MyProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMyProfile(profile: MyProfileEntity)
}

@Dao
interface SavedCardDao {
    @Query("SELECT * FROM saved_cards ORDER BY isDefault DESC")
    fun getSavedCards(): Flow<List<SavedCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: SavedCardEntity)

    @Query("DELETE FROM saved_cards WHERE id = :id")
    suspend fun deleteCard(id: Long)
}
