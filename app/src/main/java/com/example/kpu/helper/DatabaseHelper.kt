package com.example.kpu.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "KPU.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_PEMILIH = "pemilih"
        const val COLUMN_NIK = "nik"
        const val COLUMN_NAMA_LENGKAP = "nama_lengkap"
        const val COLUMN_NOMOR_HP = "nomor_hp"
        const val COLUMN_JENIS_KELAMIN = "jenis_kelamin"
        const val COLUMN_TANGGAL_PENDATAAN = "tanggal_pendataan"
        const val COLUMN_ALAMAT = "alamat"
        const val COLUMN_GAMBAR = "gambar"

        const val TABLE_USERS = "users"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FULLNAME = "fullname"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTablePemilih = """
            CREATE TABLE $TABLE_PEMILIH (
                $COLUMN_NIK INTEGER PRIMARY KEY UNIQUE, 
                $COLUMN_NAMA_LENGKAP TEXT,
                $COLUMN_NOMOR_HP INTEGER,
                $COLUMN_JENIS_KELAMIN TEXT CHECK($COLUMN_JENIS_KELAMIN IN ('P', 'L')),
                $COLUMN_TANGGAL_PENDATAAN DATE,
                $COLUMN_ALAMAT TEXT,
                $COLUMN_GAMBAR TEXT  
            )
        """.trimIndent()

        db.execSQL(createTablePemilih)

        val createTableUsers = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USERNAME TEXT PRIMARY KEY UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_FULLNAME TEXT,
                $COLUMN_NIK TEXT UNIQUE
            )
        """.trimIndent()

        db.execSQL(createTableUsers)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PEMILIH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertPemilih(nik: Int, namaLengkap: String, nomorHP: Int, jenisKelamin: String, tanggalPendataan: String, alamat: String, gambar: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIK, nik)
            put(COLUMN_NAMA_LENGKAP, namaLengkap)
            put(COLUMN_NOMOR_HP, nomorHP)
            put(COLUMN_JENIS_KELAMIN, jenisKelamin)
            put(COLUMN_TANGGAL_PENDATAAN, tanggalPendataan)
            put(COLUMN_ALAMAT, alamat)
            put(COLUMN_GAMBAR, gambar)
        }

        val result = db.insert(TABLE_PEMILIH, null, values)
//        db.close()
        return result != -1L
    }

    fun getAllPemilih(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PEMILIH", null)
    }

    fun isNikExists(nik: Int): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_PEMILIH WHERE $COLUMN_NIK = ?", arrayOf(nik.toString()))
        val exists = cursor != null && cursor.count > 0
        cursor?.close()
        return exists
    }

    fun isUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        val exists = cursor != null && cursor.count > 0
        cursor?.close()
        return exists
    }

    fun isNikExistsInUsers(nik: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_NIK = ?", arrayOf(nik.toString()))
        val exists = cursor != null && cursor.count > 0
        cursor?.close()
        return exists
    }

    fun insertUser(username: String, password: String, fullname: String, nik: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FULLNAME, fullname)
            put(COLUMN_NIK, nik)
        }

        val result = db.insert(TABLE_USERS, null, values)
//        db.close() // Close when done
        return result != -1L // Return true if insert was successful
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    @SuppressLint("Range")
    fun isPasswordValid(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))

        return if (cursor != null && cursor.moveToFirst()) {
            val hashedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            cursor.close()
            BCrypt.checkpw(password, hashedPassword) // Validate password
        } else {
            cursor?.close()
            false
        }
    }


}
