package com.equipo7.meethub.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.equipo7.meethub.R

// Carrusel de fotos de la sala (Detalle de sala)
class GaleriaAdaptador(
    private var listaImagenes: List<String>
) : RecyclerView.Adapter<GaleriaAdaptador.ImagenViewHolder>() {

    class ImagenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_galeria, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val baseUrl = "https://anderehj.somos4a.org/meethub_api/"
        val urlCompleta = baseUrl + listaImagenes[position]

        Glide.with(holder.itemView.context)
            .load(urlCompleta)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.imgFoto)
    }

    override fun getItemCount(): Int {
        return listaImagenes.size
    }

    fun actualizarLista(nuevaLista: List<String>) {
        listaImagenes = nuevaLista
        notifyDataSetChanged()
    }
}
