package com.example.kpu.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kpu.R
import com.example.kpu.helper.DatabaseHelper

class DuplicateEntryActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var textView: TextView
    private lateinit var imageView:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duplicate_entry)

        textView = findViewById(R.id.textViewDuplicateInfo)
        imageView = findViewById<ImageView>(R.id.imageViewUser)

        // Initialize the DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Get the NIK from intent
        val nik = intent.getIntExtra("nik", -1)

        // Fetch existing record and display
        if (nik != -1) {
            displayDuplicateInfo(nik)
        }
    }

    @SuppressLint("Range")
    private fun displayDuplicateInfo(nik: Int) {
        val cursor = dbHelper.getAllPemilih()
        while (cursor.moveToNext()) {
            val existingNik = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NIK))
            if (existingNik == nik) {
                val namaLengkap = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAMA_LENGKAP))
                val nomorHP = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMOR_HP))
                val jenisKelamin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JENIS_KELAMIN))
                val tanggalPendataan = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TANGGAL_PENDATAAN))
                val alamat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ALAMAT))

                val imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GAMBAR)))
                imageView.setImageURI(imageUri)

                textView.text = "NIK: $existingNik\nNama: $namaLengkap\nNomor HP: $nomorHP\nJenis Kelamin: $jenisKelamin\nTanggal Pendataan: $tanggalPendataan\nAlamat: $alamat"
                break
            }
        }
        cursor.close()
    }
}
