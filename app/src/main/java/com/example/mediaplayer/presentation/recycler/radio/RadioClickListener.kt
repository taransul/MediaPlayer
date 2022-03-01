package com.example.mediaplayer.presentation.recycler.radio

import com.example.mediaplayer.data.model.RadioStation

interface RadioClickListener {
    fun onItemClickListener(radioStation: RadioStation)

    fun onIconClickListener(position: Int)
}