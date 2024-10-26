package com.example.kpu.adapter



import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kpu.R
import com.example.kpu.model.Pemilih


class PemilihAdapter(private val pemilihList: List<Pemilih>) : RecyclerView.Adapter<PemilihAdapter.PemilihViewHolder>() {

    class PemilihViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNIK: TextView = itemView.findViewById(R.id.tvNIK)
        val tvNamaLengkap: TextView = itemView.findViewById(R.id.tvNamaLengkap)
        val tvNomorHP: TextView = itemView.findViewById(R.id.tvNomorHP)
        val tvJenisKelamin: TextView = itemView.findViewById(R.id.tvJenisKelamin)
        val tvTanggalPendataan: TextView = itemView.findViewById(R.id.tvTanggalPendataan)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvImage: TextView = itemView.findViewById(R.id.tvImage)
        val imgPemilih:ImageView = itemView.findViewById(R.id.imgPemilih)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PemilihViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pemilih, parent, false)
        return PemilihViewHolder(view)
    }

    override fun onBindViewHolder(holder: PemilihViewHolder, position: Int) {
        val pemilih = pemilihList[position]
        holder.tvNIK.text = pemilih.nik.toString()
        holder.tvNamaLengkap.text = pemilih.namaLengkap
        holder.tvNomorHP.text = pemilih.nomorHP.toString()
        holder.tvJenisKelamin.text = if (pemilih.jenisKelamin == "L") "Laki-Laki" else "Perempuan"
        holder.tvTanggalPendataan.text = pemilih.tanggalPendataan
        holder.tvAlamat.text = pemilih.alamat


        val imageUri = Uri.parse(pemilih.gambar)
        holder.imgPemilih.setImageURI(imageUri)

    }

    override fun getItemCount(): Int {
        return pemilihList.size
    }
}
