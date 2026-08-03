package com.compose.genai.di

import com.compose.genai.data.repository.ChatRepositoryImpl
import com.compose.genai.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindChatRepositoryModule(impl: ChatRepositoryImpl): ChatRepository
}