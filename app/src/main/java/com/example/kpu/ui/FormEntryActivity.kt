package com.example.kpu.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.kpu.R
import com.example.kpu.databinding.ActivityFormEntryBinding
import com.example.kpu.helper.DatabaseHelper
import com.example.kpu.helper.SaveImageToExternalStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormEntryBinding
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DatabaseHelper(this)

        setupDatePicker()

        binding.btnOpenMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            mapActivityResultLauncher.launch(intent)
        }

        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                binding.imgPreview.setImageURI(selectedImageUri)
            }
        }

        binding.btnTakePhoto.setOnClickListener{
            checkCameraPermission()
        }

        binding.imgPreview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            saveDataToDatabase()
        }
    }

    private val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {

            binding.imgPreview.setImageURI(selectedImageUri)
        }
    }

    private val CAMERA_PERMISSION_CODE = 100

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            takePhoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        selectedImageUri = photoUri
        takePictureResultLauncher.launch(photoUri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }


    private val mapActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val location = result.data?.getStringExtra("selected_location")
            binding.etAlamat.setText(location)
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            binding.etTanggalPendataan.setText(dateFormat.format(calendar.time))
        }
        binding.etTanggalPendataan.setOnClickListener {
            DatePickerDialog(
                this@FormEntryActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }


    private fun saveDataToDatabase() {

        val nik = binding.etNIK.text.toString().toIntOrNull()
        val namaLengkap = binding.etNamaLengkap.text.toString()
        val nomorHP = binding.etNomorHP.text.toString().toIntOrNull()
        val jenisKelamin = when (binding.rgJenisKelamin.checkedRadioButtonId) {
            R.id.rbLakiLaki -> "L"
            R.id.rbPerempuan -> "P"
            else -> ""
        }
        val tanggalPendataan = binding.etTanggalPendataan.text.toString()
        val alamat = binding.etAlamat.text.toString()
        val gambar = selectedImageUri?.let { uri ->
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                SaveImageToExternalStorage(this, bitmap, "nama_gambar_${System.currentTimeMillis()}")
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        if (nik != null && dbHelper.isNikExists(nik)) {
            // If NIK exists, show duplicate entry activity
            val intent = Intent(this, DuplicateEntryActivity::class.java)
            intent.putExtra("nik", nik)
            startActivity(intent)
            return
        }

        // Validasi input
        if (nik == null || nomorHP == null || namaLengkap.isEmpty() || jenisKelamin.isEmpty() || tanggalPendataan.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            return
        }



        // Simpan data ke database
        val success = dbHelper.insertPemilih(nik, namaLengkap, nomorHP, jenisKelamin, tanggalPendataan, alamat, gambar)

        if (success) {
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            clearForm()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        binding.etNIK.text.clear()
        binding.etNamaLengkap.text.clear()
        binding.etNomorHP.text.clear()
        binding.rgJenisKelamin.clearCheck()
        binding.etTanggalPendataan.text.clear()
        binding.etAlamat.text.clear()
    }
}
