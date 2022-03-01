package com.example.mediaplayer.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediaplayer.data.model.RadioStationEntity
import com.example.mediaplayer.data.source.local.dao.RadioDao

@Database(entities = [RadioStationEntity::class], version = 2, exportSchema = false)
abstract class AppDatabaseRadio : RoomDatabase() {

    abstract val radioDao: RadioDao

    companion object {
        const val DB_NAME_RADIO = "RadioApp.db"
    }
}