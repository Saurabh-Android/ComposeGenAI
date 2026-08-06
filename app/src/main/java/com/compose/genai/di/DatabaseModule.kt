package com.compose.genai.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.compose.genai.data.local.dao.MessageDao
import com.compose.genai.data.local.database.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : ChatDatabase{
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chat.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao = database.messageDao()
}