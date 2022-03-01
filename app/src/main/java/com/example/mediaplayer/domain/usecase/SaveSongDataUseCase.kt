package com.example.mediaplayer.domain.usecase

import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.domain.repository.PlaylistRepository

//interface SaveSongDataUseCase {
//  suspend  fun saveSongItem(song: Song)
//}

class SaveSongDataUseCase(private val playlistRepository: PlaylistRepository) {

    suspend fun saveSongItem(song: Song) {
        playlistRepository.saveSongData(song)
    }
}