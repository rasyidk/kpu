package com.example.kpu.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kpu.R
import com.example.kpu.databinding.ActivityLoginBinding
import com.example.kpu.helper.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        databaseHelper = DatabaseHelper(this)
        binding.btnSubmit.setOnClickListener {
            handleLogin()
        }
        binding.tvSignupPage.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun handleLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (databaseHelper.isPasswordValid(username, password)) {
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", true)
                putString("username", username)
                apply()
            }
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}
