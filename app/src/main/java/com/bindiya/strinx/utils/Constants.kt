package com.bindiya.strinx.utils

const val EMPTY_STRING = ""
object ContentProviderConstants {
    const val CONTENT_PROVIDER_AUTHORITY = "com.iav.contestdataprovider"
    const val CONTENT_PROVIDER_PATH = "text"
    const val CONTENT_PROVIDER_URI = "content://$CONTENT_PROVIDER_AUTHORITY/$CONTENT_PROVIDER_PATH"
    const val READ_PERMISSION = "$CONTENT_PROVIDER_AUTHORITY.READ"
    const val WRITE_PERMISSION = "$CONTENT_PROVIDER_AUTHORITY.WRITE"
    val PERMISSION_ARRAY = arrayOf(READ_PERMISSION, WRITE_PERMISSION)
    const val DATA_COLUMN = "data"
    const val RANDOM_TEXT_FIELD = "randomText"
    const val VALUE_FIELD = "value"
    const val LENGTH_FIELD = "length"
    const val CREATED_AT_FIELD = "created"
}

object ErrorConstants {
    const val NO_DATA_FOUND = "No data found"
    const val FAILED_TO_QUERY_CONTENT_PROVIDER = "Failed to generate random text"
    const val FAILED_TO_DELETE_RECORD = "Failed to delete record"
    const val NO_ENTRY_FOUND_TO_DELETE = "No entry found to delete"
    const val NO_ENTRIES_FOUND_TO_DELETE = "No entries found to delete"
}