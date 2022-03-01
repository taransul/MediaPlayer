package com.example.mediaplayer.utils.util

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import com.example.mediaplayer.R
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    fun songArt(path: String?, context: Context): Bitmap {
        val retriever = MediaMetadataRetriever()
        val inputStream: InputStream
        retriever.setDataSource(path)
        if (retriever.embeddedPicture != null) {
            inputStream = ByteArrayInputStream(retriever.embeddedPicture)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            retriever.release()
            return bitmap
        } else {
            return getLargeIcon(context)
        }
    }

    private fun getLargeIcon(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources,
            R.drawable.headphones)
    }

    fun formatDuration(duration: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong())))
    }

    fun formatTrack(trackNumber: Int): Int {
        var formatted = trackNumber
        if (trackNumber >= 1000) {
            formatted = trackNumber % 1000
        }
        return formatted
    }

    fun delete(activity: Activity, imageFile: File) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

            val projection = arrayOf(MediaStore.Audio.Media._ID)

            val selection = MediaStore.Audio.Media.DATA + " = ?"
            val selectionArgs = arrayOf<String>(imageFile.absolutePath)

            val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val contentResolver = activity.contentResolver
            val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)

            if (c!!.moveToFirst()) {
                val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val deleteUri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                contentResolver.delete(deleteUri, null, null)
            } else {
                Log.w("Media ", "Media not found!!")
            }
            c.close()
        }, 70)
    }
}