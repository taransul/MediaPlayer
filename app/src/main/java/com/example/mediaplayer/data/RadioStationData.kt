package com.example.mediaplayer.data

import android.os.Environment
import android.util.Log
import com.example.mediaplayer.data.model.RadioStation
import com.example.mediaplayer.domain.repository.RadioStationDataI
import java.io.File

class RadioStationData : RadioStationDataI {

    override suspend fun dataListRadioStation(): MutableList<RadioStation> {

        val data: MutableList<RadioStation> = mutableListOf()
        val file = File("${Environment.getExternalStorageDirectory()}/Download/list.txt")
        val contents = file.readText()
        val lines: MutableList<String> = contents.lines().toMutableList()

        Log.v("my!!!", "Кол-во строк: ${lines.size}")
        lines.retainAll { it.contains("https") }

        val linesNameHttps = lines.map { it.substringAfter("name\":\"").substringBefore("\",") }
        val linesUrl = lines.map { it.substringAfter("url\":\"").substringBeforeLast("\"") }

        for (i in linesNameHttps.indices) {
            data.add(RadioStation(linesNameHttps[i], linesUrl[i], false))
        }
        return data
    }
}