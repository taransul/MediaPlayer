package com.example.mediaplayer.presentation.recycler.radio.horizontal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.RadioStationEntity
import com.example.mediaplayer.databinding.RadioItemSaveBinding

class HorizontalViewHolder(
    itemView: View,
    private val radioClickListener: RadioClickListenerHorizontal,
) :
    RecyclerView.ViewHolder(itemView) {

    private val binding: RadioItemSaveBinding by viewBinding()

    companion object {
        fun fromParent(parent: ViewGroup, radioClickListener: RadioClickListenerHorizontal) =
            HorizontalViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.radio_item_save, parent, false), radioClickListener
            )
    }

    private val nameRadio: TextView by lazy { binding.nameSave }

    private val itemContainer by lazy { binding.itemContainerSave }

    fun bindView(radioStation: RadioStationEntity) {
        nameRadio.text = radioStation.nameRadio

        itemContainer.setOnClickListener {
            radioClickListener.onItemClickListener(radioStation)
        }
    }
}