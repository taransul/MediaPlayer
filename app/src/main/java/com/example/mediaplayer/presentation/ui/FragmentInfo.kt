package com.example.mediaplayer.presentation.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.databinding.FragmentInfoBinding
import com.example.mediaplayer.playback.MusicNotificationManager
import com.example.mediaplayer.playback.MusicService
import com.example.mediaplayer.playback.PlaybackInfoListener
import com.example.mediaplayer.playback.PlayerAdapter
import com.example.mediaplayer.utils.util.UniversalUtils
import com.example.mediaplayer.utils.util.Utils
import com.example.mediaplayer.utils.util.SongProvider
import org.koin.android.viewmodel.ext.android.viewModel


class FragmentInfo : Fragment(R.layout.fragment_info) {

    private val binding: FragmentInfoBinding by viewBinding()

    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean? = null
    private var mPlaybackListener: PlaybackListener? = null
    private var deviceSongs: MutableList<Song> = mutableListOf()
    private var mMusicNotificationManager: MusicNotificationManager? = null
    private val viewModel: PlaylistViewModel by viewModel()
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mUserIsSeeking: Boolean = false


    override fun onStart() {
        super.onStart()

        if (mPlayerAdapter != null && mPlayerAdapter?.isPlaying() == true) {
            restorePlayerStatus()
        }
        binding.buttonInfoPlay.setOnClickListener { resumeOrPause() }
        binding.buttonInfoNext.setOnClickListener { skipNext() }
        binding.buttonInfoPrev.setOnClickListener { skipPrev() }

        deviceSongs = SongProvider.getAllDeviceSongs(requireContext())

        doBindService()
        initializeSeekBar()

        binding.buttonInfoPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentInfo_to_nav_music)
        }

        binding.buttonInfoFave.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentInfo_to_nav_video)
        }

    }

    private fun resumeOrPause() {
        var songs: List<Song> = mutableListOf()

        if (checkIsPlayer()) {
            mPlayerAdapter?.resumeOrPause()
        } else {
            viewModel.playlistData.observe(viewLifecycleOwner) {
                songs = it
            }
            onSongSelected(songs[0], songs)
        }
    }

    private fun onSongSelected(song: Song, songs: List<Song>) {
        if (!binding.seekBarInfo.isEnabled) {
            binding.seekBarInfo.isEnabled = true
        }
        try {
            mPlayerAdapter?.setCurrentSong(song, songs)
            mPlayerAdapter?.initMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter?.skip(true)
        }
    }

    private fun skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter?.instantReset()
        }
    }

    private fun checkIsPlayer(): Boolean {
        return mPlayerAdapter!!.isMediaPlayer()
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            mMusicService = (iBinder as MusicService.LocalBinder).instance
            mPlayerAdapter = mMusicService?.mediaPlayerHolder
            mMusicNotificationManager = mMusicService?.musicNotificationManager

            if (mPlaybackListener == null) {
                mPlaybackListener = PlaybackListener()
                mPlayerAdapter?.setPlaybackInfoListener(mPlaybackListener!!)
            }
            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {

                restorePlayerStatus()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mMusicService = null
        }
    }


    private fun doBindService() {
        activity?.bindService(Intent(requireContext(),
            MusicService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsBound = true

        val startNotStickyIntent = Intent(requireContext(), MusicService::class.java)
        activity?.startService(startNotStickyIntent)
    }

    private fun doUnbindService() {
        if (mIsBound == true) {
            activity?.unbindService(mConnection)
            mIsBound = false
        }
    }

    private fun restorePlayerStatus() {
        binding.seekBarInfo.isEnabled = mPlayerAdapter!!.isMediaPlayer()

        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {

            mPlayerAdapter?.onResumeActivity()
            updatePlayingInfo(restore = true, startPlay = false)
        }
    }

    private fun initializeSeekBar() {
        binding.seekBarInfo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userSelectedPosition = 0

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    userSelectedPosition = progress

                }
                binding.textViewProgress.text = UniversalUtils.formatTime(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mUserIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mUserIsSeeking) {
                }
                mUserIsSeeking = false
                mPlayerAdapter?.seekTo(userSelectedPosition)
            }
        })
    }

    private fun updatePlayingStatus() {

            val drawable = if (mPlayerAdapter?.getState() != PlaybackInfoListener.State.PAUSED)
                R.drawable.ic_pause_vector
            else
                R.drawable.ic_play_vector
             binding.buttonInfoPlay.post{
                binding.buttonInfoPlay.setImageResource(drawable)
            }
    }

    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean) {

        if (startPlay) {
            mPlayerAdapter?.getMediaPlayer()?.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mMusicService?.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                    mMusicNotificationManager?.createNotification())
            }, 200)
        }

        val selectedSong = mPlayerAdapter?.getCurrentSong()

        binding.textViewInfoTitle.text = selectedSong?.title
        val duration = selectedSong?.duration
        binding.seekBarInfo.max = duration ?: 0
        binding.imageViewInfo.setImageBitmap(Utils.songArt(selectedSong?.path,
            requireContext()))

        if (restore) {
            binding.seekBarInfo.progress = mPlayerAdapter?.getPlayerPosition() ?: 0
            updatePlayingStatus()


            Handler(Looper.getMainLooper()).postDelayed({
                if (mMusicService?.isRestoredFromPause == true) {
                    mMusicService?.stopForeground(false)
                    mMusicService?.musicNotificationManager?.notificationManager?.notify(
                        MusicNotificationManager.NOTIFICATION_ID,
                        mMusicService?.musicNotificationManager?.notificationBuilder?.build())
                    mMusicService?.isRestoredFromPause = false
                }
            }, 200)
        }
    }


    internal inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                binding.seekBarInfo.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

            updatePlayingStatus()
            if (mPlayerAdapter?.getState() != State.PAUSED
                && mPlayerAdapter?.getState() != State.PAUSED
            ) {
                updatePlayingInfo(restore = false, startPlay = true)
            }
        }

        override fun onPlaybackCompleted() {
        }
    }
}