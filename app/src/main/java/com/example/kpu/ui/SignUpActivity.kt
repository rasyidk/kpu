package com.example.kpu.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kpu.R
import com.example.kpu.databinding.ActivitySignUpBinding
import com.example.kpu.helper.DatabaseHelper

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        databaseHelper = DatabaseHelper(this)
        binding.btnSubmit.setOnClickListener {
            handleSignUp()
        }
        binding.tvLoginPage.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun handleSignUp() {
        val name = binding.etName.text.toString()
        val nik = binding.etNIK.text.toString()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        if (name.isEmpty() || nik.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!nik.all { it.isDigit() }) {
            Toast.makeText(this, "NIK must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        val nikInt = nik

        if (databaseHelper.isUsernameExists(username)) {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
            return
        }
        if (databaseHelper.isNikExistsInUsers(nikInt)) {
            Toast.makeText(this, "NIK is already used", Toast.LENGTH_SHORT).show()
            return
        }
        val insertSuccess = databaseHelper.insertUser(username, databaseHelper.hashPassword(password), name, nikInt)
        if (insertSuccess) {
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", true)
                putString("username", username)  // Optionally save the username
                apply()
            }
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Sign up failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
