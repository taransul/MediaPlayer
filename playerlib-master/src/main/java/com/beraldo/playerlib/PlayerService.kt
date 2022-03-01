package com.beraldo.playerlib

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.beraldo.playerlib.player.PlayerHolder
import com.beraldo.playerlib.player.PlayerModule
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class PlayerService : IntentService("playerlib"), PlayerNotificationManager.NotificationListener {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL = "playerlib_channel"
        const val STREAM_URL = "stream_url"
    }

    private lateinit var playerHolder: PlayerHolder

    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onBind(intent: Intent?): IBinder {
        intent?.getStringExtra(STREAM_URL)?.let {
            initialize(it)
            playerHolder.start()
        }

        return PlayerServiceBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerNotificationManager.setPlayer(null)
        playerHolder.release()
    }

    private fun initialize(streamUrl: String) {
    
        playerHolder = PlayerModule.getPlayerHolder(this, streamUrl)

        playerNotificationManager = PlayerModule.getPlayerNotificationManager(this).also {
            it.setNotificationListener(this)
            it.setPlayer(playerHolder.audioFocusPlayer)
        }
    }

    override fun onNotificationCancelled(notificationId: Int) {}

    override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
        startForeground(notificationId, notification)
    }

    inner class PlayerServiceBinder : Binder() {
        fun getPlayerHolderInstance() = playerHolder
    }

    override fun onHandleIntent(intent: Intent?) {}
}