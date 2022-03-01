package com.example.mediaplayer.utils.util

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.mediaplayer.R

class DialogPermissionsFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Важно!")
                .setMessage("Это разрешение обязательно для работы приложения")
                .setIcon(R.drawable.headphones)
                .setPositiveButton("ОК") {
                        dialog, _ ->
                            ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
            Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE)
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}