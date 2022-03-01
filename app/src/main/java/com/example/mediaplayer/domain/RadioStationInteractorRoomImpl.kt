package com.example.mediaplayer.domain

import com.example.mediaplayer.data.source.local.AppDatabaseRadio
import com.example.mediaplayer.data.model.RadioStationEntity

class RadioStationInteractorRoomImpl(private val appDatabaseRadio: AppDatabaseRadio) :
    RadioStationInteractorRoom {
    override suspend fun getRadio(): List<RadioStationEntity> {
        return appDatabaseRadio.radioDao.getAll()
    }

    override suspend fun insertRadio(radioStationEntity: RadioStationEntity) {
        return appDatabaseRadio.radioDao.insert(radioStationEntity)
    }
}