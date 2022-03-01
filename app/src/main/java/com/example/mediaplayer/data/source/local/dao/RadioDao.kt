package com.example.mediaplayer.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mediaplayer.data.model.RadioStationEntity

@Dao
interface RadioDao {

    @Query("SELECT * FROM radio")
    fun getAll(): MutableList<RadioStationEntity>

    @Insert
    fun insert(radioStationEntity: RadioStationEntity)
}