package com.example.mediaplayer.utils.util

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.mediaplayer.data.model.Song
import java.util.*

object SongProvider {
    private const val TITLE = 0
    private const val TRACK = 1
    private const val YEAR = 2
    private const val DURATION = 3
    private const val PATH = 4
    private const val ALBUM = 5
    private const val ARTIST_ID = 6
    private const val ARTIST = 7

    private val BASE_PROJECTION = arrayOf(MediaStore.Audio.AudioColumns.TITLE, // 0
        MediaStore.Audio.AudioColumns.TRACK, // 1
        MediaStore.Audio.AudioColumns.YEAR, // 2
        MediaStore.Audio.AudioColumns.DURATION, // 3
        MediaStore.Audio.AudioColumns.DATA, // 4
        MediaStore.Audio.AudioColumns.ALBUM, // 5
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 6
        MediaStore.Audio.AudioColumns.ARTIST)// 7

    private val mAllDeviceSongs = ArrayList<Song>()

    fun getAllDeviceSongs(context: Context): MutableList<Song> {
        val cursor = makeSongCursor(context)
        return getSongs(cursor)
    }

    private fun getSongs(cursor: Cursor?): MutableList<Song> {
        val songs = ArrayList<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = getSongFromCursorImpl(cursor)
                if (song.duration >= 30000) {
                    songs.add(song)
                    mAllDeviceSongs.add(song)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()

        return songs
    }

    private fun getSongFromCursorImpl(cursor: Cursor): Song {
        val title = cursor.getString(TITLE)
        val trackNumber = cursor.getInt(TRACK)
        val year = cursor.getInt(YEAR)
        val duration = cursor.getInt(DURATION)
        val uri = cursor.getString(PATH)
        val albumName = cursor.getString(ALBUM)
        val artistId = cursor.getInt(ARTIST_ID)
        val artistName = cursor.getString(ARTIST)

        return Song(title, trackNumber, year, duration, uri, albumName, artistId, artistName,false)
    }

    private fun makeSongCursor(context: Context): Cursor? {
        try {
            return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                BASE_PROJECTION, null, null, null)
        } catch (e: SecurityException) {
            return null
        }
    }
}