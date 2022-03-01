package com.example.mediaplayer.data

import com.example.mediaplayer.data.model.RadioStation
import com.example.mediaplayer.data.model.RadioStationEntity

fun RadioStation.toRadioStationEntity() =
    RadioStationEntity(
        nameRadio = nameRadio,
        urlPath = urlPath,
        isImage = isImage,
        numberOfOpenings = 0
    )

fun RadioStationEntity.toRadioStation() =
    RadioStation(
        nameRadio = nameRadio,
        urlPath = urlPath,
        isImage = isImage
    )
