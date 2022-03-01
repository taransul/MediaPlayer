package com.example.mediaplayer.presentation.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.domain.SongInteractor
import com.example.mediaplayer.domain.usecase.DeleteSongUseCase
import com.example.mediaplayer.domain.usecase.GetSongsUseCase
import com.example.mediaplayer.domain.usecase.SaveSongDataUseCase
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val saveSongDataUseCase: SaveSongDataUseCase,
    private val getSongsUseCase: GetSongsUseCase,
    private val deleteSongUseCase: DeleteSongUseCase,
    private val songInteractor: SongInteractor,
    application: Application,
) : AndroidViewModel(application) {

    companion object {
        const val TEXT_WHEN_DELETED_FROM_SAVED_FRAGMENT = "Песня удалена из избранных"
        const val TEXT_WHEN_INSERTED_INTO_SAVED_FRAGMENT = "Песня добавлена в избранные"
    }

    private val _songsPhone = MutableLiveData<List<Song>>()
    val songsPhone: LiveData<List<Song>> get() = _songsPhone

    private val _playlistData = MutableLiveData<List<Song>>()
    val playlistData: LiveData<List<Song>> get() = _playlistData


    init {
        getSongIcon()
        Log.v("my!!!","ИНИЦИАЛИЗАЦИЯ")

    }

     fun getSongIcon() {
        viewModelScope.launch {
            _songsPhone.value = songInteractor.getSongIcon()
            _playlistData.value = getSongsUseCase.getSongs()

        }
    }


    fun saveSongData(song: Song) {
        viewModelScope.launch {
            saveSongDataUseCase.saveSongItem(song)
        }
    }

    fun getSongsFromDb() {
        viewModelScope.launch {
            _playlistData.value = getSongsUseCase.getSongs()
        }
    }

//    fun removeItemFromList(song: Song) {
//        viewModelScope.launch {
//            deleteSongUseCase.deleteSongItem(song)
//            val list = _playlistData.value as ArrayList<Song>
//            list.remove(song)
//            _playlistData.value = list
//        }
//    }

    private fun deleteOneSongTitle(songTitle: String) {
        viewModelScope.launch {
            deleteSongUseCase.deleteOneSongTitle(songTitle)
        }
    }

    fun onIconItemClicked(position: Int, isFragment: Boolean) {
        val listSaved = _playlistData.value?.toMutableList() ?: return
        val item = _songsPhone.value?.get(position) ?: return
        val list = _songsPhone.value?.toMutableList() ?: return

        if (!isFragment) {
            list[position] = item.copy(isChecked = !item.isChecked)
            _songsPhone.value = list
Log.v("my!!!","list*obs***** $list")
            if (item.isChecked) {
                deleteOneSongTitle(list[position].title)   //.title

                Toast.makeText(getApplication(),
                    TEXT_WHEN_DELETED_FROM_SAVED_FRAGMENT,
                    Toast.LENGTH_SHORT).show()
            } else {
                saveSongData(list[position])

                Toast.makeText(getApplication(),
                    TEXT_WHEN_INSERTED_INTO_SAVED_FRAGMENT,
                    Toast.LENGTH_SHORT).show()
            }
        } else if (isFragment) {
            deleteOneSongTitle(listSaved[position].title)   //.title

            Toast.makeText(getApplication(),
                TEXT_WHEN_DELETED_FROM_SAVED_FRAGMENT,
                Toast.LENGTH_SHORT).show()

            getSongIcon()
        }
    }

}
