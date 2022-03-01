package com.example.mediaplayer.presentation.recycler.radio

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.data.model.RadioStation

class RadioAdapter(private val radioClickListener: RadioClickListener) : RecyclerView.Adapter<RadioViewHolder>() {

    private var radioList = mutableListOf<RadioStation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder =
        RadioViewHolder.fromParent(parent,radioClickListener)

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bindView(radioList[position])
    }

    override fun getItemCount() = radioList.size

    fun addRadio(radio: List<RadioStation>) {
        radioList = radio.toMutableList()
        notifyDataSetChanged()
    }
}