package com.beraldo.playerlib.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import androidx.media.AudioAttributesCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class PlayerHolder(
    context: Context,
    private val streamUrl: String,
    private val playerState: PlayerState,
) {
    val audioFocusPlayer: ExoPlayer

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributesCompat.Builder()
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .build()
        audioFocusPlayer = AudioFocusWrapper(
            audioAttributes,
            audioManager,
            ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        ).apply { prepare(buildMediaSource(Uri.parse(streamUrl))) }
        Log.i("my!!!", "SimpleExoPlayer created")
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exo-radiouci"))
            .createMediaSource(uri)
    }

    fun start() {
        with(audioFocusPlayer) {

            prepare(buildMediaSource(Uri.parse(streamUrl)))
            with(playerState) {

                playWhenReady = whenReady
                seekTo(window, position)

                attachLogging(audioFocusPlayer)
            }
        }
    }

    fun stop() {
        with(audioFocusPlayer) {
            // Save state
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }

            stop(true)
        }
    }

    fun release() {
        audioFocusPlayer.release()

    }

    private fun attachLogging(exoPlayer: ExoPlayer) {
        // Write to log on state changes.
        exoPlayer.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e("my!!!", "playerError: $error")
            }

            fun getStateString(state: Int): String {
                return when (state) {
                    Player.STATE_BUFFERING -> "STATE_BUFFERING"
                    Player.STATE_ENDED -> "STATE_ENDED"
                    Player.STATE_IDLE -> "STATE_IDLE"
                    Player.STATE_READY -> "STATE_READY"
                    else -> "?"
                }
            }
        })
    }
}