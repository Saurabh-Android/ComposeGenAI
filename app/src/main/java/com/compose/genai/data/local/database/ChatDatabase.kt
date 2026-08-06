package com.compose.genai.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.compose.genai.data.local.dao.MessageDao
import com.compose.genai.data.local.entity.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun messageDao(): MessageDao

}