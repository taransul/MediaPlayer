package com.example.mediaplayer.domain

import com.example.mediaplayer.data.model.Song

interface SongInteractor {
    suspend fun getSongIcon(): List<Song>
}