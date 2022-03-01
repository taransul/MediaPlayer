package com.example.mediaplayer.presentation.ui.radio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mediaplayer.presentation.MainActivity

class LaunchPlayerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifyIntent = Intent(context, MainActivity::class.java)
        context.startActivity(notifyIntent)
    }
}