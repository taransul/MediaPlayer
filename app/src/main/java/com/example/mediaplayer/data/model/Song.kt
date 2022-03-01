package com.example.mediaplayer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "Song")
@Parcelize
data class Song(
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Int,
    val path: String?,
    val albumName: String,
    @PrimaryKey val artistId: Int,
    val artistName: String,
    val isChecked: Boolean,
) : Parcelable