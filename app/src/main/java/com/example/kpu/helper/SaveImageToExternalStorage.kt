package com.example.kpu.helper

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream

fun SaveImageToExternalStorage(context: Context, bitmap: Bitmap, imageName: String): String? {
    val resolver = context.contentResolver
    var imagePath: String? = null

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let {
        val outputStream: OutputStream? = resolver.openOutputStream(it)
        outputStream?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }
        imagePath = it.toString()
//        Toast.makeText(context, "Gambar disimpan di $imagePath", Toast.LENGTH_SHORT).show()
    }

    return imagePath
}