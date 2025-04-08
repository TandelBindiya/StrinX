package com.bindiya.strinx.data.datasource

import android.annotation.SuppressLint
import android.content.ContentResolver
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.bindiya.strinx.data.model.RandomStringData
import com.bindiya.strinx.utils.ContentProviderConstants
import com.bindiya.strinx.utils.ContentProviderConstants.CONTENT_PROVIDER_URI
import com.bindiya.strinx.utils.ErrorConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class StringContentProvider(private val contentResolver: ContentResolver) {
    private val contentUri by lazy { CONTENT_PROVIDER_URI.toUri() }

    @SuppressLint("SuspiciousIndentation")
    suspend fun generateRandomText(length: Int): Result<RandomStringData> {
        return withContext(Dispatchers.IO) {
            try {
                val queryArgs = bundleOf(
                    ContentResolver.QUERY_ARG_LIMIT to
                            length
                )
                val cursor = contentResolver.query(
                    contentUri,
                    null,
                    queryArgs,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        it.getColumnIndex(ContentProviderConstants.DATA_COLUMN).let { index ->
                            if (index >= 0) {
                                JSONObject(it.getString(index)).let { jsonObj ->
                                    jsonObj.getJSONObject(ContentProviderConstants.RANDOM_TEXT_FIELD)
                                        .let { randomTextObj ->
                                            val value = randomTextObj.getString(
                                                ContentProviderConstants.VALUE_FIELD
                                            )
                                            val len =
                                                randomTextObj.getInt(ContentProviderConstants.LENGTH_FIELD)
                                            val created =
                                                randomTextObj.getString(ContentProviderConstants.CREATED_AT_FIELD)
                                            Result.Success(RandomStringData(value, len, created))
                                        }
                                }
                            } else {
                                Result.Error(ErrorConstants.NO_DATA_FOUND)
                            }
                        }
                    } else {
                        Result.Error(ErrorConstants.NO_DATA_FOUND)
                    }
                } ?: Result.Error(ErrorConstants.FAILED_TO_QUERY_CONTENT_PROVIDER)
            } catch (e: Exception) {
                Result.Error(e.message ?: ErrorConstants.FAILED_TO_QUERY_CONTENT_PROVIDER)
            }
        }

    }

    suspend fun deleteEntry(data: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val selection = "${ContentProviderConstants.DATA_COLUMN}=?"
                val selectionArgs = arrayOf(data)
                val rowsDeleted = contentResolver.delete(contentUri, selection, selectionArgs)
                if (rowsDeleted > 0) {
                    Result.Success(true)
                } else {
                    Result.Error(ErrorConstants.NO_ENTRY_FOUND_TO_DELETE)
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: ErrorConstants.FAILED_TO_DELETE_RECORD)
            }
        }
    }

    suspend fun deleteAllEntries(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val rowsDeleted = contentResolver.delete(contentUri, null, null)
                if (rowsDeleted > 0) {
                    Result.Success(true)
                } else {
                    Result.Error(ErrorConstants.NO_ENTRIES_FOUND_TO_DELETE)
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: ErrorConstants.FAILED_TO_DELETE_RECORD)
            }
        }
    }
}