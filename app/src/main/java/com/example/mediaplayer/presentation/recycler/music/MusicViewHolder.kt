package com.example.mediaplayer.presentation.recycler.music

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.Song
import com.example.mediaplayer.databinding.TrackItemBinding
import com.example.mediaplayer.utils.util.UniversalUtils
import com.example.mediaplayer.utils.util.Utils

class MusicViewHolder(
    itemView: View,
    private val longClick: LongClick,
    private val onSongClicked: SongClicked,
    private val songsSelected: SongsSelected,
    private val iconClickListener: IconClickListener,
    private val context: Context,
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding: TrackItemBinding by viewBinding()

    companion object {
        fun fromParent(
            parent: ViewGroup,
            longClick: LongClick,
            songClicked: SongClicked,
            songsSelected: SongsSelected,
            iconClickListener: IconClickListener,
            context: Context,
        ) =
            MusicViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.track_item, parent, false),
                longClick, songClicked, songsSelected, iconClickListener, context
            )
    }

    private val imageViewMusic: ImageView by lazy { binding.imageView }
    private val textViewTitle: TextView by lazy { binding.textViewSongTitle }
    private val textViewArtist: TextView by lazy { binding.textViewArtistName }
    private val textViewDuration: TextView by lazy { binding.tvDuration }
    private val iconCheckBox: CheckBox by lazy { binding.iconCheckBox }


    val mainItem by lazy { binding.mainConstraint }

    fun bindView(song: Song) {
        textViewTitle.text = song.title
        textViewArtist.text = song.artistName
        textViewDuration.text = UniversalUtils.formatTime(song.duration.toLong())


        iconCheckBox.isChecked = song.isChecked
        imageViewMusic.setImageBitmap(Utils.songArt((song.path), context))
        iconCheckBox.setOnClickListener {
            iconClickListener.onIconClickListener(absoluteAdapterPosition)
        }
    }
}


