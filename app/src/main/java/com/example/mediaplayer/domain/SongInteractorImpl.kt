package com.example.mediaplayer.domain

import android.content.Context
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.domain.usecase.GetSongsUseCase
import com.example.mediaplayer.utils.util.SongProvider

class SongInteractorImpl(
    private val getSongsUseCase: GetSongsUseCase,
    private val context: Context,
) : SongInteractor {

    override suspend fun getSongIcon(): List<Song> {
        val savedNews = getSongsUseCase.getSongs()
        val articles = SongProvider.getAllDeviceSongs(context) as List<Song>

        val savedNewsList: List<Song> = savedNews!!.map { dataBaseNews ->
            dataBaseNews
        }

        val articlesList: List<Song> = articles.map { networkNews ->
            networkNews
        }

        val comparisonResultList: MutableList<Song> = mutableListOf()

        articlesList.forEach { item ->
            val newItem = savedNewsList.find { news ->
                item.title == news.title
            }
            comparisonResultList.add(
                newItem ?: item
            )
        }
        return comparisonResultList
    }
}