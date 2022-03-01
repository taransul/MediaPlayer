package com.example.mediaplayer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "radio")
@Parcelize
data class RadioStationEntity(
    val nameRadio: String,
    val urlPath: String,
    var isImage: Boolean = false,
    var numberOfOpenings: Int,
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
