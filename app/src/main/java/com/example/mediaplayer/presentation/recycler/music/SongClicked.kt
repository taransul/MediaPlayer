package com.example.mediaplayer.presentation.recycler.music

import com.example.mediaplayer.data.model.Song

interface SongClicked {
    fun onSongClicked(song: Song)
}