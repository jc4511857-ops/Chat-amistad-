package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProfileEntity::class,
        LiveStreamEntity::class,
        StreamCommentEntity::class,
        MessageEntity::class,
        TransactionEntity::class,
        MyProfileEntity::class,
        SavedCardEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun liveStreamDao(): LiveStreamDao
    abstract fun streamCommentDao(): StreamCommentDao
    abstract fun messageDao(): MessageDao
    abstract fun transactionDao(): TransactionDao
    abstract fun myProfileDao(): MyProfileDao
    abstract fun savedCardDao(): SavedCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amora_mexico_db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
