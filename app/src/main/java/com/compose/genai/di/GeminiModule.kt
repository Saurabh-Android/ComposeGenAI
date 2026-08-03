package com.compose.genai.di

import com.compose.genai.BuildConfig
import com.google.genai.Client
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    fun provideClient(): Client {
        return Client.builder()
            .apiKey(BuildConfig.GEMINI_API_KEY)
            .build()
    }
}