package com.example.mediaplayer.presentation.recycler.music

import com.example.mediaplayer.data.model.Song

interface SongsSelected {
    fun onSelectSongs(selectedSongs: MutableList<Song>)
}