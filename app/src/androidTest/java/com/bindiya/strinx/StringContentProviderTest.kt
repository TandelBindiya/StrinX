package com.bindiya.strinx

import android.content.ContentResolver
import android.database.Cursor
import androidx.core.net.toUri
import com.bindiya.strinx.data.datasource.Result
import com.bindiya.strinx.data.datasource.StringContentProvider
import com.bindiya.strinx.utils.ContentProviderConstants
import com.bindiya.strinx.utils.ErrorConstants
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StringContentProviderTest {

    @Mock
    lateinit var contentResolver: ContentResolver

    @Mock
    lateinit var cursor: Cursor

    private lateinit var stringContentProvider: StringContentProvider

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        stringContentProvider = StringContentProvider(contentResolver)
    }

    @Test
    fun generateRandomTextReturnsSuccessWhenDataFound() = runBlocking {
        val jsonResponse = """
            {
                "randomText": {
                    "value": "abc123",
                    "length": 6,
                    "createdAt": "2025-04-08"
                }
            }
        """
        `when`(
            contentResolver.query(
                ContentProviderConstants.CONTENT_PROVIDER_URI.toUri(),
                any(),
                any(),
                any()
            )
        )
            .thenReturn(cursor)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getColumnIndex(ContentProviderConstants.DATA_COLUMN)).thenReturn(0)
        `when`(cursor.getString(0)).thenReturn(jsonResponse)

        val result = stringContentProvider.generateRandomText(6)

        assert(result is Result.Success)
        assertEquals((result as Result.Success).data?.value, "abc123")
        assertEquals(result.data?.length, 6)
        assertEquals(result.data?.created, "2025-04-08")
    }

    @Test
    fun generateRandomTextReturnsErrorWhenNoDataIsFound() = runBlocking {
        `when`(contentResolver.query(any(), any(), any(), any()))
            .thenReturn(cursor)
        `when`(cursor.moveToFirst()).thenReturn(false)

        val result = stringContentProvider.generateRandomText(6)

        assert(result is Result.Error)
        assertEquals((result as Result.Error).errorMessage, ErrorConstants.NO_DATA_FOUND)
    }

    @Test
    fun generateRandomTextReturnsErrorOnException() = runBlocking {
        `when`(contentResolver.query(any(), any(), any(), any()))
            .thenThrow(Exception("Database Error"))

        val result = stringContentProvider.generateRandomText(6)

        assert(result is Result.Error)
        assertEquals(
            (result as Result.Error).errorMessage,
            ErrorConstants.FAILED_TO_QUERY_CONTENT_PROVIDER
        )
    }

    @Test
    fun deleteEntryReturnsSuccessWhenEntryIsDeleted() = runBlocking {
        val jsonResponse = """
            {
                "randomText": {
                    "value": "abc123",
                    "length": 6,
                    "createdAt": "2025-04-08"
                }
            }
        """
        `when`(contentResolver.delete(any(), any(), any()))
            .thenReturn(1)  // Simulate that one row was deleted

        val result = stringContentProvider.deleteEntry(jsonResponse)

        assert(result is Result.Success)
        assertEquals((result as Result.Success).data, true)
    }

    @Test
    fun deleteEntryReturnsErrorWhenNoEntryIsDeleted() = runBlocking {
        val jsonResponse = """
            {
                "randomText": {
                    "value": "abc123",
                    "length": 6,
                    "createdAt": "2025-04-08"
                }
            }
        """
        `when`(contentResolver.delete(any(), any(), any()))
            .thenReturn(0)  // Simulate that no row was deleted

        val result = stringContentProvider.deleteEntry(jsonResponse)

        assert(result is Result.Error)
        assertEquals((result as Result.Error).errorMessage, ErrorConstants.NO_ENTRY_FOUND_TO_DELETE)
    }

    @Test
    fun deleteEntryReturnsErrorOnException() = runBlocking {
        val jsonResponse = """
            {
                "randomText": {
                    "value": "abc123",
                    "length": 6,
                    "createdAt": "2025-04-08"
                }
            }
        """
        `when`(contentResolver.delete(any(), any(), any()))
            .thenThrow(Exception("Database Error"))

        val result = stringContentProvider.deleteEntry(jsonResponse)

        assert(result is Result.Error)
        assertEquals((result as Result.Error).errorMessage, ErrorConstants.FAILED_TO_DELETE_RECORD)
    }

    @Test
    fun `deleteAllEntriesReturnsSuccessWhenEntriesAreDeleted`() = runBlocking {
        `when`(contentResolver.delete(any(), any(), any()))
            .thenReturn(1)

        val result = stringContentProvider.deleteAllEntries()

        assert(result is Result.Success)
        assertEquals((result as Result.Success).data, true)
    }

    @Test
    fun deleteAllEntriesReturnsErrorWhenNoEntriesAreDeleted() = runBlocking {
        `when`(contentResolver.delete(any(), any(), any()))
            .thenReturn(0)

        val result = stringContentProvider.deleteAllEntries()

        assert(result is Result.Error)
        assertEquals(
            (result as Result.Error).errorMessage,
            ErrorConstants.NO_ENTRIES_FOUND_TO_DELETE
        )
    }

    @Test
    fun deleteAllEntriesReturnsErrorOnException() = runBlocking {
        `when`(contentResolver.delete(any(), any(), any()))
            .thenThrow(Exception("Database Error"))

        val result = stringContentProvider.deleteAllEntries()

        assert(result is Result.Error)
        assertEquals((result as Result.Error).errorMessage, ErrorConstants.FAILED_TO_DELETE_RECORD)
    }
}
