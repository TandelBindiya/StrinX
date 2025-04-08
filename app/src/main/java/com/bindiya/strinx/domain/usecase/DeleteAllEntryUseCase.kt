package com.bindiya.strinx.domain.usecase

import com.bindiya.strinx.data.repository.StringProviderRepository

class DeleteAllEntryUseCase(private val stringProviderRepository: StringProviderRepository) {
    suspend operator fun invoke() = stringProviderRepository.deleteAllEntries()
}