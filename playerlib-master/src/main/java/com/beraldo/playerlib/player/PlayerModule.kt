package com.beraldo.playerlib.player

import android.content.Context
import com.beraldo.playerlib.PlayerService
import com.beraldo.playerlib.R
import com.beraldo.playerlib.media.DescriptionAdapter
import com.google.android.exoplayer2.ui.PlayerNotificationManager

object PlayerModule {
    fun getPlayerHolder(context: Context, streamUrl: String) = PlayerHolder(context, streamUrl, PlayerState())

    fun getPlayerNotificationManager(context: Context): PlayerNotificationManager =
        PlayerNotificationManager.createWithNotificationChannel(
            context,
            PlayerService.NOTIFICATION_CHANNEL,
            R.string.app_name,
            PlayerService.NOTIFICATION_ID,
            getDescriptionAdapter(context)
        ).apply {
            setFastForwardIncrementMs(0)
            setOngoing(true)
            setUseNavigationActions(false)
            setRewindIncrementMs(0)
            setStopAction(null)
        }

    private fun getDescriptionAdapter(context: Context) = DescriptionAdapter(context)
}