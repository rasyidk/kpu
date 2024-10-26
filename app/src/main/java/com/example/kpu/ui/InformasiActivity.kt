package com.example.kpu.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.kpu.R
import com.example.kpu.databinding.ActivityFormEntryBinding
import com.example.kpu.databinding.ActivityInformasiBinding


class InformasiActivity : AppCompatActivity() {

    private lateinit var binding:ActivityInformasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi)

        binding = ActivityInformasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data informasi pemilu sebagai list string
        val informasiPemilu = arrayListOf(
            "Pemilu Presiden 2024 - 14 Februari 2024 - Indonesia",
            "Pemilu Legislatif 2024 - 17 April 2024 - Indonesia",
            "Pemilu Kepala Daerah 2024 - 27 November 2024 - Jakarta"
        )

        // Menggunakan ArrayAdapter default untuk menampilkan data
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, informasiPemilu)
        binding.listViewInformasi.adapter = adapter
    }
}
