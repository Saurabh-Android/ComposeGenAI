package com.compose.genai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.compose.genai.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insertMessage(messageEntity: MessageEntity)

    @Query("Select * from messages order by timestamp ASC")
    fun getAllMessage(): Flow<List<MessageEntity>>
}