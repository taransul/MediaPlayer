package com.example.mediaplayer.di.module

import com.example.mediaplayer.data.RadioStationData
import com.example.mediaplayer.domain.repository.PlaylistRepository
import com.example.mediaplayer.domain.repository.RadioStationDataI
import com.example.mediaplayer.domain.usecase.DeleteSongUseCase
import com.example.mediaplayer.domain.usecase.GetSongsUseCase
import com.example.mediaplayer.domain.usecase.SaveSongDataUseCase
import org.koin.dsl.module

val appModule = module {


    single { createSaveSongDataUseCase(get()) }

    single { createGetSongsUseCase(get()) }

    single { createDeleteSongUseCase(get()) }

    single { createPlaylistRepository(get()) }

    single { createRadioStationInteractorRoom(get()) }

    single<RadioStationDataI> {
        RadioStationData()
    }

}

fun createSaveSongDataUseCase(
    playlistRepository: PlaylistRepository,
): SaveSongDataUseCase {
    return SaveSongDataUseCase(playlistRepository)
}

fun createDeleteSongUseCase(
    playlistRepository: PlaylistRepository,
): DeleteSongUseCase {
    return DeleteSongUseCase(playlistRepository)
}


fun createGetSongsUseCase(
    playlistRepository: PlaylistRepository,
): GetSongsUseCase {
    return GetSongsUseCase(playlistRepository)
}
