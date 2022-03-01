package com.example.mediaplayer.di.module

import android.app.Application
import androidx.room.Room
import com.example.mediaplayer.data.repository.PlaylistRepositoryImp
import com.example.mediaplayer.data.source.local.AppDatabase
import com.example.mediaplayer.data.source.local.AppDatabaseRadio
import com.example.mediaplayer.data.source.local.dao.RadioDao
import com.example.mediaplayer.data.source.local.dao.SongDao
import com.example.mediaplayer.domain.RadioStationInteractorRoom
import com.example.mediaplayer.domain.RadioStationInteractorRoomImpl
import com.example.mediaplayer.domain.repository.PlaylistRepository
import org.koin.dsl.module

val databaseModule = module {

    single { createAppDatabase(get()) }
    single { createAppDatabaseRadio(get()) }

    single { createSongDao(get()) }
    single { createRadioDao(get()) }

}


internal fun createAppDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        AppDatabase.DB_NAME
    )
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
}

internal fun createAppDatabaseRadio(application: Application): AppDatabaseRadio {
    return Room.databaseBuilder(
        application,
        AppDatabaseRadio::class.java,
        AppDatabaseRadio.DB_NAME_RADIO
    )
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
}


fun createSongDao(appDatabase: AppDatabase): SongDao {
    return appDatabase.songDao
}

fun createRadioDao(appDatabase: AppDatabaseRadio): RadioDao {
    return appDatabase.radioDao
}


fun createPlaylistRepository(appDatabase: AppDatabase): PlaylistRepository {
    return PlaylistRepositoryImp(appDatabase)
}

fun createRadioStationInteractorRoom(appDatabaseRadio: AppDatabaseRadio): RadioStationInteractorRoom {
    return RadioStationInteractorRoomImpl(appDatabaseRadio)
}