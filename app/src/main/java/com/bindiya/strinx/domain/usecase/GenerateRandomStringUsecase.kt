package com.bindiya.strinx.domain.usecase

import com.bindiya.strinx.data.repository.StringProviderRepository
import javax.inject.Inject

class GenerateRandomStringUseCase @Inject constructor(private val repository: StringProviderRepository) {
    suspend operator fun invoke(length: Int) = repository.generateRandomString(length)
}