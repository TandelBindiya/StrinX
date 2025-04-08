package com.bindiya.strinx.di

import android.content.ContentResolver
import android.content.Context
import com.bindiya.strinx.data.datasource.StringContentProvider
import com.bindiya.strinx.data.repository.StringProviderRepository
import com.bindiya.strinx.domain.usecase.DeleteAllEntryUseCase
import com.bindiya.strinx.domain.usecase.DeleteEntryUseCase
import com.bindiya.strinx.domain.usecase.GenerateRandomStringUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideStringContentProvider(contentResolver: ContentResolver): StringContentProvider {
        return StringContentProvider(contentResolver)
    }

    @Provides
    fun provideStringProviderRepository(stringContentProvider: StringContentProvider): StringProviderRepository {
        return StringProviderRepository(stringContentProvider)
    }

    @Provides
    @Singleton
    fun provideGenerateRandomStringUseCase(stringProviderRepository: StringProviderRepository): GenerateRandomStringUseCase {
        return GenerateRandomStringUseCase(stringProviderRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteEntryUseCase(stringProviderRepository: StringProviderRepository): DeleteEntryUseCase {
        return DeleteEntryUseCase(stringProviderRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteAllEntryUseCase(stringProviderRepository: StringProviderRepository): DeleteAllEntryUseCase {
        return DeleteAllEntryUseCase(stringProviderRepository)
    }

}