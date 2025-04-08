package com.bindiya.strinx.domain.usecase

import com.bindiya.strinx.data.repository.StringProviderRepository
import javax.inject.Inject

class DeleteEntryUseCase @Inject constructor(private val stringProviderRepository: StringProviderRepository) {
    suspend operator fun invoke(string: String) = stringProviderRepository.deleteEntry(string)
}