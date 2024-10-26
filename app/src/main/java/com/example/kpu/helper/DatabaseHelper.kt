package com.example.kpu.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PEMILIH")
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
        db.close()
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
}
