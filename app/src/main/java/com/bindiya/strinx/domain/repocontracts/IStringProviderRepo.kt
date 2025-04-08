package com.bindiya.strinx.domain.repocontracts

import com.bindiya.strinx.data.model.RandomStringData
import com.bindiya.strinx.data.datasource.Result

interface IStringProviderRepo {
    suspend fun generateRandomString(length: Int): Result<RandomStringData>
    suspend fun deleteEntry(data: String): Result<Boolean>
    suspend fun deleteAllEntries(): Result<Boolean>
}