package com.example.mediaplayer.domain.repository

import com.example.mediaplayer.data.model.Song

interface PlaylistRepository {

  suspend  fun saveSongData(song: Song): Long

  suspend  fun getSongs(): List<Song>?

  suspend fun deleteOneSongTitle(song: String)
}