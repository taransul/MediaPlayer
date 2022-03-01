package com.example.mediaplayer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Context.readExternalStoragePermission(activity: Activity) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 21)
    } else {
    }
}

fun Context.isReadPhoneStatePermissionGranted(): Boolean {
    val firstPermissionResult = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    return firstPermissionResult == PackageManager.PERMISSION_GRANTED
}