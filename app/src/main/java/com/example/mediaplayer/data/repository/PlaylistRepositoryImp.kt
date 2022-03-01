package com.example.mediaplayer.data.repository

import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.data.source.local.AppDatabase
import com.example.mediaplayer.domain.repository.PlaylistRepository

class PlaylistRepositoryImp(private val appDatabase: AppDatabase) : PlaylistRepository {

    override suspend fun getSongs(): List<Song>? {
        return appDatabase.songDao.getAll()
    }

    override suspend fun saveSongData(song: Song): Long {

        return appDatabase.songDao.insert(song)
    }

    override suspend fun deleteOneSongTitle(song: String) {

            appDatabase.songDao.deleteOneSongTitle(song)
    }
}