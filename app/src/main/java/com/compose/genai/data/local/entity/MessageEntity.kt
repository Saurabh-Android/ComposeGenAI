package com.compose.genai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.protobuf.Timestamp

@Entity(tableName = "messages")
data class MessageEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long
)