package com.example.kpu.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kpu.R
import com.example.kpu.adapter.MenuAdapter
import com.example.kpu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuItems = listOf("Informasi", "Form Entry", "Lihat Data", "Keluar")
        val menuIcons = listOf(
            R.drawable.baseline_info_24, // Replace with your actual icon resources
            R.drawable.baseline_form_24,
            R.drawable.baseline_lihat_data_24,
            R.drawable.baseline_exit_to_app_24
        )

        val adapter = MenuAdapter(this, menuItems, menuIcons)
        binding.listview.adapter = adapter
        binding.listview.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    startActivity(Intent(this, InformasiActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(this, FormEntryActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(this, LihatDataActivity::class.java))
                }
                3 -> {

                    finish()
                }
            }
        }
    }
}