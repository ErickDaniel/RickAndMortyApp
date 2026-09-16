package com.erickjuarez.rickandmorty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ChatConversationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ChatHistoryConverters::class)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao
}
