package com.example.mediaplayer.di.module

import com.example.mediaplayer.domain.SongInteractor
import com.example.mediaplayer.domain.SongInteractorImpl
import com.example.mediaplayer.presentation.ui.PlaylistViewModel
import com.example.mediaplayer.presentation.ui.radio.RadioViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    viewModel {
        RadioViewModel(
            radioStationData = get(),
            radioStationInteractorRoom = get()
        )
    }

    viewModel {
        PlaylistViewModel(
            saveSongDataUseCase = get(),
            getSongsUseCase = get(),
            deleteSongUseCase = get(),
            get(),
            application = get(),
        )
    }

    single<SongInteractor> {
        SongInteractorImpl(get(), get())
    }
}