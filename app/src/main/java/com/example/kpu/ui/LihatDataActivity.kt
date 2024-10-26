package com.example.kpu.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kpu.R
import com.example.kpu.adapter.PemilihAdapter
import com.example.kpu.databinding.ActivityLihatDataBinding
import com.example.kpu.helper.DatabaseHelper
import com.example.kpu.model.Pemilih

class LihatDataActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var pemilihAdapter: PemilihAdapter
    private lateinit var binding: ActivityLihatDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLihatDataBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dbHelper = DatabaseHelper(this)


        val recyclerView: RecyclerView = binding.recyclerViewInformasi
        recyclerView.layoutManager = LinearLayoutManager(this)


        val pemilihList = getPemilihData()
        pemilihAdapter = PemilihAdapter(pemilihList)
        recyclerView.adapter = pemilihAdapter

    }


    private fun getPemilihData(): List<Pemilih> {
        val pemilihList = mutableListOf<Pemilih>()
        val cursor = dbHelper.getAllPemilih()
        val nikIndex = cursor.getColumnIndex("nik")
        val namaLengkapIndex = cursor.getColumnIndex("nama_lengkap")
        val nomorHPIndex = cursor.getColumnIndex("nomor_hp")
        val jenisKelaminIndex = cursor.getColumnIndex("jenis_kelamin")
        val tanggalPendataanIndex = cursor.getColumnIndex("tanggal_pendataan")
        val alamatIndex = cursor.getColumnIndex("alamat")
        val gambarIndex = cursor.getColumnIndex("gambar")

        while (cursor.moveToNext()) {
            val nik = if (nikIndex >= 0) cursor.getInt(nikIndex) else -1
            val namaLengkap = if (namaLengkapIndex >= 0) cursor.getString(namaLengkapIndex) else ""
            val nomorHP = if (nomorHPIndex >= 0) cursor.getInt(nomorHPIndex) else -1
            val jenisKelamin = if (jenisKelaminIndex >= 0) cursor.getString(jenisKelaminIndex) else ""
            val tanggalPendataan = if (tanggalPendataanIndex >= 0) cursor.getString(tanggalPendataanIndex) else ""
            val alamat = if (alamatIndex >= 0) cursor.getString(alamatIndex) else ""
            val gambar = if (gambarIndex >= 0) cursor.getString(gambarIndex) else ""

            if (nik >= 0) {
                pemilihList.add(Pemilih(nik, namaLengkap, nomorHP, jenisKelamin, tanggalPendataan, alamat, gambar))
            }
        }

        cursor.close()
        return pemilihList
    }
}