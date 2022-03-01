package com.example.mediaplayer.presentation.ui.favourite

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.databinding.FragmentFavouriteBinding
import com.example.mediaplayer.playback.MusicNotificationManager
import com.example.mediaplayer.playback.MusicService
import com.example.mediaplayer.playback.PlaybackInfoListener
import com.example.mediaplayer.playback.PlayerAdapter
import com.example.mediaplayer.presentation.recycler.music.*
import com.example.mediaplayer.presentation.ui.PlaylistViewModel
import com.example.mediaplayer.utils.util.Constants
import com.example.mediaplayer.utils.util.Utils
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.NullPointerException

class FavouriteFragment : Fragment(R.layout.fragment_favourite) {

    private val viewModel: PlaylistViewModel by viewModel()
    private val binding: FragmentFavouriteBinding by viewBinding()

    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean? = null
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mUserIsSeeking = false
    private var mPlaybackListener: PlaybackListener? = null
    private var deviceSongs: MutableList<Song> = mutableListOf()
    private var mMusicNotificationManager: MusicNotificationManager? = null

    private val adapter by lazy {
        MusicAdapter(
            longClick,
            songClicked,
            songsSelected,
            iconClickListener,
            requireContext()
        )
    }

    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE_CODE)
        } else {
            viewModel.getSongsFromDb()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
            restorePlayerStatus()
        }
        binding.controls.buttonPlayPause.setOnClickListener { resumeOrPause() }
        binding.controls.buttonNext.setOnClickListener { skipNext() }
        binding.controls.buttonPrevious.setOnClickListener { skipPrev() }

        initRecycler()

        binding.controls.cardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_video_to_fragmentInfo)
        }

        viewModel.playlistData.observe(viewLifecycleOwner) { song ->
            adapter.addSongs(song)
            deviceSongs = song.toMutableList()

            doBindService()

            initializeSeekBar()
        }

    }

    private val iconClickListener: IconClickListener = object : IconClickListener {

        override fun onIconClickListener(position: Int) {
            viewModel.onIconItemClicked(position, true)
        }
    }

    private val longClick: LongClick = object : LongClick {
        override fun onSongLongClicked(position: Int) {
        }
    }

    private val songClicked: SongClicked = object : SongClicked {
        override fun onSongClicked(song: Song) {

            onSongSelected(song, deviceSongs)
        }
    }

    private fun initializeSeekBar() {

        binding.controls.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                var userSelectedPosition = 0

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    mUserIsSeeking = true
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean,
                ) {

                    if (fromUser) {
                        userSelectedPosition = progress
                    }
                    binding.controls.tvPassControls.text = Utils.formatDuration(seekBar.progress)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                    if (mUserIsSeeking) {

                    }
                    mUserIsSeeking = false
                    mPlayerAdapter!!.seekTo(userSelectedPosition)
                }
            })
    }

    private fun skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.instantReset()
        }
    }

    private fun resumeOrPause() {
        var songs: List<Song> = listOf()
        if (checkIsPlayer()) {
            mPlayerAdapter!!.resumeOrPause()
        } else {
            viewModel.playlistData.observe(viewLifecycleOwner) {
                songs = it
            }
            if (songs.isNotEmpty()) {
                onSongSelected(songs[0], songs)
            }
        }
    }

    private fun onSongSelected(song: Song, songs: List<Song>) {
        if (!binding.controls.seekBar.isEnabled) {
            binding.controls.seekBar.isEnabled = true
        }
        try {
            mPlayerAdapter!!.setCurrentSong(song, songs)
            mPlayerAdapter!!.initMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.skip(true)
        }
    }

    private fun checkIsPlayer(): Boolean {
        return mPlayerAdapter!!.isMediaPlayer()
    }


    private val songsSelected: SongsSelected = object : SongsSelected {
        override fun onSelectSongs(selectedSongs: MutableList<Song>) {

        }
    }

    private fun initRecycler() {
        binding.recyclerContainerMusic.adapter = adapter
        binding.recyclerContainerMusic.layoutManager = LinearLayoutManager(requireContext())
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            mMusicService = (iBinder as MusicService.LocalBinder).instance
            mPlayerAdapter = mMusicService?.mediaPlayerHolder
            mMusicNotificationManager = mMusicService?.musicNotificationManager

            if (mPlaybackListener == null) {
                mPlaybackListener = PlaybackListener()
                try {
                    mPlayerAdapter?.setPlaybackInfoListener(mPlaybackListener!!)
                }catch (e: NullPointerException){
                    Log.e("my!!!","onServiceConnected")
                }

            }
            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {

                restorePlayerStatus()
            }
            checkReadStoragePermissions()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mMusicService = null
        }
    }

    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean) {

        if (startPlay) {
            mPlayerAdapter!!.getMediaPlayer()?.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mMusicService!!.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                    mMusicNotificationManager!!.createNotification())
            }, 200)
        }

        val selectedSong = mPlayerAdapter!!.getCurrentSong()

        binding.controls.songTitle.text = selectedSong?.title
        val duration = selectedSong?.duration
        binding.controls.seekBar.max = duration!!
        binding.controls.imageViewControl.setImageBitmap(Utils.songArt(selectedSong.path,
            requireContext()))

        if (restore) {
            binding.controls.seekBar.progress = mPlayerAdapter!!.getPlayerPosition()
            updatePlayingStatus()


            Handler(Looper.getMainLooper()).postDelayed({
                //stop foreground if coming from pause state
                if (mMusicService!!.isRestoredFromPause) {
                    mMusicService!!.stopForeground(false)
                    mMusicService!!.musicNotificationManager!!.notificationManager
                        .notify(MusicNotificationManager.NOTIFICATION_ID,
                            mMusicService!!.musicNotificationManager!!.notificationBuilder!!.build())
                    mMusicService!!.isRestoredFromPause = false
                }
            }, 200)
        }
    }

    private fun updatePlayingStatus() {
        val drawable = if (mPlayerAdapter!!.getState() != PlaybackInfoListener.State.PAUSED)
            R.drawable.ic_pause_vector
        else
            R.drawable.ic_play_vector
        binding.controls.buttonPlayPause.post {
            binding.controls.buttonPlayPause.setImageResource(drawable)
        }
    }

    private fun restorePlayerStatus() {
        binding.controls.seekBar.isEnabled = mPlayerAdapter!!.isMediaPlayer()

        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {

            mPlayerAdapter!!.onResumeActivity()
            updatePlayingInfo(restore = true, startPlay = false)
        }
    }


    internal inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                binding.controls.seekBar.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

            updatePlayingStatus()
            if (mPlayerAdapter!!.getState() != State.PAUSED
                && mPlayerAdapter!!.getState() != State.PAUSED
            ) {
                updatePlayingInfo(false, true)
            }
        }

        override fun onPlaybackCompleted() {
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
        if (mIsBound!!) {
            activity?.unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {
            mPlayerAdapter!!.onPauseActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMusicService = null
    }
}