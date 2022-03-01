package com.example.mediaplayer.presentation.ui.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.databinding.FragmentMusicBinding
import com.example.mediaplayer.playback.MusicNotificationManager
import com.example.mediaplayer.playback.MusicService
import com.example.mediaplayer.playback.PlaybackInfoListener
import com.example.mediaplayer.playback.PlayerAdapter
import com.example.mediaplayer.presentation.MainActivity
import com.example.mediaplayer.presentation.recycler.*
import com.example.mediaplayer.presentation.recycler.music.*
import com.example.mediaplayer.presentation.ui.PlaylistViewModel
import com.example.mediaplayer.utils.checkReadStoragePermissions
import com.example.mediaplayer.utils.le
import com.example.mediaplayer.utils.util.Utils
import com.example.mediaplayer.utils.util.SongProvider
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class MusicFragment : Fragment(R.layout.fragment_music), ActionMode.Callback {

    private val adapter by lazy {
        MusicAdapter(
            longClick,
            songClicked,
            songsSelected,
            iconClickListener,
            requireContext()
        )
    }

    private val binding: FragmentMusicBinding by viewBinding()
    private val viewModel: PlaylistViewModel by viewModel()
    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean? = null
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mUserIsSeeking = false
    private var mPlaybackListener: PlaybackListener? = null
    private var deviceSongs: MutableList<Song> = mutableListOf()
    private var mMusicNotificationManager: MusicNotificationManager? = null

    private var actionMode: ActionMode? = null


    private val iconClickListener: IconClickListener = object : IconClickListener {

        override fun onIconClickListener(position: Int) {
            viewModel.onIconItemClicked(position, false)
        }

    }

    private val longClick: LongClick = object : LongClick {
        override fun onSongLongClicked(position: Int) {
            if (actionMode == null) {
                actionMode = activity?.startActionMode(this@MusicFragment)
            }
        }
    }

    private val songClicked: SongClicked = object : SongClicked {
        override fun onSongClicked(song: Song) {
            onSongSelected(song, deviceSongs)
        }
    }


    private val songsSelected: SongsSelected = object : SongsSelected {
        override fun onSelectSongs(selectedSongs: MutableList<Song>) {

            if (selectedSongs.isEmpty()) {
                actionMode?.finish()
                adapter.removeSelection()
            } else {
                val title = "Delete ${selectedSongs.size} songs?"
                actionMode?.title = title
            }
        }
    }

    private fun initRecycler() {
        binding.recyclerContainerMusic.adapter = adapter
        binding.recyclerContainerMusic.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onStart() {
        super.onStart()

        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
            restorePlayerStatus()
        }
        binding.controls.buttonPlayPause.setOnClickListener { resumeOrPause() }
        binding.controls.buttonNext.setOnClickListener { skipNext() }
        binding.controls.buttonPrevious.setOnClickListener { skipPrev() }

        deviceSongs = SongProvider.getAllDeviceSongs(requireContext())
        initRecycler()

        binding.controls.cardView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_music_to_fragmentInfo)
        }

        doBindService()
        initializeSeekBar()
    }

    private fun getMusic() {
        viewModel.getSongIcon()
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            mMusicService = (iBinder as MusicService.LocalBinder).instance
            mPlayerAdapter = mMusicService!!.mediaPlayerHolder
            mMusicNotificationManager = mMusicService!!.musicNotificationManager

            if (mPlaybackListener == null) {
                mPlaybackListener = PlaybackListener()
                mPlayerAdapter!!.setPlaybackInfoListener(mPlaybackListener!!)
            }
            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {

                restorePlayerStatus()
            }

            if (checkReadStoragePermissions()) {
                getMusic()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mMusicService = null
        }
    }


    override fun onPause() {
        super.onPause()
        doUnbindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {
            mPlayerAdapter!!.onPauseActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        doBindService()
        viewModel.songsPhone.observe(viewLifecycleOwner) { song ->
            adapter.addSongs(song)

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

        val drawable = if (mPlayerAdapter?.getState() != PlaybackInfoListener.State.PAUSED)
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

    private fun skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.instantReset()
        }
    }

    private fun resumeOrPause() {
        var songs: List<Song> = mutableListOf()

        if (checkIsPlayer()) {
            mPlayerAdapter!!.resumeOrPause()
        } else {
            viewModel.playlistData.observe(viewLifecycleOwner) {
                songs = it
            }
            onSongSelected(songs[0], songs)
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

    internal inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                binding.controls.seekBar.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

            updatePlayingStatus()
            if (mPlayerAdapter?.getState() != State.PAUSED
            ) {
                updatePlayingInfo(restore = false, startPlay = true)
            }
        }

        override fun onPlaybackCompleted() {
        }
    }


    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode?.menuInflater
        inflater?.inflate(R.menu.action_mode_menu, menu!!)
        (requireActivity() as MainActivity)
            .findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .visibility = View.GONE
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                val songs = adapter.getSelectedSongs()
                le("songs  $songs")
                songs.forEach {
                    val file = File(it.path)
                    le("file  $file")
                    Utils.delete(requireActivity(), file)
                    adapter.updateRemoved(it)
                }
                Toast.makeText(requireContext(),
                    "Deleted ${songs.size} songs",
                    Toast.LENGTH_SHORT).show()
                mode?.finish()
                adapter.removeSelection()
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        adapter.removeSelection()
        (requireActivity() as MainActivity)
            .findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .visibility = View.VISIBLE
        actionMode = null
    }
}