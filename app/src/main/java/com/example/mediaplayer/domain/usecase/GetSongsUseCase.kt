package com.example.mediaplayer.domain.usecase

import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.domain.repository.PlaylistRepository

//interface GetSongsUseCase {
//  suspend fun getSongs(): List<Song>?
//}

class GetSongsUseCase(private val playlistRepository: PlaylistRepository) {

    suspend fun getSongs(): List<Song>? {
        return playlistRepository.getSongs()
    }
}