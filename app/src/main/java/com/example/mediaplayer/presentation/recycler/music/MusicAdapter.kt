package com.example.mediaplayer.presentation.recycler.music

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.data.model.Song

class MusicAdapter(
    private val longClick: LongClick,
    private val songClicked: SongClicked,
    private val songsSelected: SongsSelected,
    private val iconClickListener: IconClickListener,
    private val context: Context,
) : RecyclerView.Adapter<MusicViewHolder>() {

    private var songsList = mutableListOf<Song>()
    private var selectedSongs = mutableListOf<Song>()
    private var selectionModeActive = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder =
        MusicViewHolder.fromParent(parent,
            longClick,
            songClicked,
            songsSelected,
            iconClickListener,
            context)

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val song = songsList[position]
        holder.bindView(song)

        holder.mainItem.isSelected = selectedSongs.contains(song)
        holder.mainItem.setOnLongClickListener {
            longClick.onSongLongClicked(position)
            if (!selectionModeActive) {
                selectionModeActive = true
            }
            false
        }
        holder.mainItem.setOnClickListener {
            if (!selectionModeActive) {
                songClicked.onSongClicked(song)
            } else {
                if (selectedSongs.contains(song)) {
                    selectedSongs.remove(song)
                    songsSelected.onSelectSongs(getSelectedSongs())

                } else {
                    selectedSongs.add(song)

                    songsSelected.onSelectSongs(getSelectedSongs())
                    Log.v("My!!!", "$selectedSongs")
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = songsList.size

    fun addSongs(songs: List<Song>) {
        songsList = songs.toMutableList()
        notifyDataSetChanged()
    }

    fun removeSelection() {
        selectionModeActive = false
        selectedSongs.clear()
        notifyDataSetChanged()
    }

    fun getSelectedSongs(): MutableList<Song> {
        return selectedSongs
    }

    fun updateRemoved(song: Song) {
        songsList.remove(song)
        notifyDataSetChanged()
    }
}