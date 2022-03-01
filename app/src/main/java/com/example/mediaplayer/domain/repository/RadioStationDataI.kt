package com.example.mediaplayer.domain.repository

import com.example.mediaplayer.data.model.RadioStation

interface RadioStationDataI {
  suspend fun dataListRadioStation(): MutableList<RadioStation>
}