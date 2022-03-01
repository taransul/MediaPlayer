package com.example.mediaplayer.data.source.local.dao

import androidx.room.*
import com.example.mediaplayer.data.model.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM Song")
    fun getAll(): MutableList<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song): Long

    @Delete
    fun delete(song: Song)

    @Query("DELETE FROM Song")
    fun deleteAll()

    @Query("DELETE FROM Song WHERE title = :title")
    fun deleteOneSongTitle(title: String)

    @Update
    fun update(song: Song)

}