package com.example.mediaplayer.presentation.recycler.radio.horizontal

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.data.model.RadioStationEntity

class HorizontalAdapter(private val radioClickListener: RadioClickListenerHorizontal) :
    RecyclerView.Adapter<HorizontalViewHolder>() {

    private var radioListSave = mutableListOf<RadioStationEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder =
        HorizontalViewHolder.fromParent(parent, radioClickListener)

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        holder.bindView(radioListSave[position])
    }

    override fun getItemCount() = radioListSave.size

    fun addRadio(radio: List<RadioStationEntity>) {
        radioListSave = radio.reversed() as MutableList<RadioStationEntity>
        notifyDataSetChanged()
    }
}
