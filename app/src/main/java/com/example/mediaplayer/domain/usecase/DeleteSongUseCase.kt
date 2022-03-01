package com.example.mediaplayer.domain.usecase

import com.example.mediaplayer.domain.repository.PlaylistRepository

//interface DeleteSongUseCase {
//  suspend  fun deleteSongItem(song: Song)
//}

class DeleteSongUseCase(private val playlistRepository: PlaylistRepository) {

    suspend fun deleteOneSongTitle(song: String) {
        playlistRepository.deleteOneSongTitle(song)
    }

}