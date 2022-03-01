package com.example.mediaplayer.domain

import com.example.mediaplayer.data.model.RadioStationEntity

interface RadioStationInteractorRoom {
    suspend fun getRadio(): List<RadioStationEntity>

    suspend fun insertRadio(radioStationEntity: RadioStationEntity)
}