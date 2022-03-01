package com.example.mediaplayer.utils

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mediaplayer.utils.util.DialogPermissionsFragment

fun Fragment.checkReadStoragePermissions(): Boolean {

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            val myDialogFragment = DialogPermissionsFragment()
            val manager = activity?.supportFragmentManager
            if (manager != null) {
                myDialogFragment.show(manager, "myDialog")
            }
            return false

        } else {
            return true
        }
    }

fun Fragment.le(message: String) {
    Log.e("my!!!", message)
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}