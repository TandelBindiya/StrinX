package com.bindiya.strinx.data.repository

import com.bindiya.strinx.data.datasource.Result
import com.bindiya.strinx.data.datasource.StringContentProvider
import com.bindiya.strinx.data.model.RandomStringData
import com.bindiya.strinx.domain.repocontracts.IStringProviderRepo
import javax.inject.Inject

class StringProviderRepository @Inject constructor(private val contentProvider: StringContentProvider) :
    IStringProviderRepo {

    override suspend fun generateRandomString(length: Int): Result<RandomStringData> {
        return contentProvider.generateRandomText(length)
    }

    override suspend fun deleteEntry(data: String): Result<Boolean> {
        return contentProvider.deleteEntry(data)
    }

    override suspend fun deleteAllEntries(): Result<Boolean> {
        return contentProvider.deleteAllEntries()
    }
}
