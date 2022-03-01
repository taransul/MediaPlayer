package com.beraldo.playerlib.media

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.beraldo.playerlib.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class DescriptionAdapter(private val context: Context) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): String =
        context.getString(R.string.notification_title)

    override fun getCurrentContentText(player: Player): String? = null

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = null

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val intent = Intent().apply {
            action = "com.beraldo.playerlib.LAUNCH_PLAYER_ACTIVITY"
        }
        val matches = context.packageManager.queryBroadcastReceivers(intent, 0)

        val explicit = Intent(intent)
        for (resolveInfo in matches) {
            val componentName = ComponentName(
                resolveInfo.activityInfo.applicationInfo.packageName,
                resolveInfo.activityInfo.name
            )

            explicit.component = componentName
        }

        return PendingIntent.getBroadcast(context, 0, explicit, 0)
    }
}