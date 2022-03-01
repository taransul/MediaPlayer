package com.example.mediaplayer.presentation.recycler.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.RadioStation
import com.example.mediaplayer.databinding.RadioItemBinding

class RadioViewHolder(itemView: View, private val radioClickListener: RadioClickListener) :
    RecyclerView.ViewHolder(itemView) {

    private val binding: RadioItemBinding by viewBinding()

    companion object {
        fun fromParent(parent: ViewGroup, radioClickListener: RadioClickListener) = RadioViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.radio_item, parent, false), radioClickListener
        )
    }

    private val nameRadio: TextView by lazy { binding.nameRadio }
    private val imageRadio: ImageView by lazy { binding.imageRadio }
    private val itemContainer by lazy { binding.itemContainer }

    fun bindView(radioStation: RadioStation) {
        nameRadio.text = radioStation.nameRadio

        itemContainer.setOnClickListener {
            radioClickListener.onItemClickListener(radioStation)

            radioClickListener.onIconClickListener(absoluteAdapterPosition)
        }
        if (radioStation.isImage) {
            imageRadio.setImageResource(R.drawable.radio_image_ok)
        }
        if (!radioStation.isImage) {
            imageRadio.setImageResource(R.drawable.radio_image)
        }
    }
}