package com.bindiya.strinx.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.bindiya.strinx.utils.ContentProviderConstants.READ_PERMISSION
import com.bindiya.strinx.utils.ContentProviderConstants.WRITE_PERMISSION

fun Context.isProviderPermissionsGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        READ_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        WRITE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
}