package com.equipo7.meethub.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.equipo7.meethub.R
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.vista.SalasActivity

class SalasAdaptador(
    private val activity: SalasActivity,
    private var listaSalas: List<dataSalas>
) : RecyclerView.Adapter<SalasAdaptador.SalaViewHolder>() {

    class SalaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSala: ImageView = itemView.findViewById(R.id.imgSala)
        val txtNombreSala: TextView = itemView.findViewById(R.id.txtNombreSala)
        val txtCapacidad: TextView = itemView.findViewById(R.id.txtCapacidad)
        val txtEquipamiento: TextView = itemView.findViewById(R.id.txtEquipamiento)
        val btnVerDetalles: TextView = itemView.findViewById(R.id.btnVerDetalles)
        val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sala, parent, false)
        return SalaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalaViewHolder, position: Int) {
        val sala = listaSalas[position]
        holder.txtNombreSala.text = sala.nombreSala
        holder.txtCapacidad.text = holder.itemView.context.getString(
            R.string.sala_capacidad, sala.capacidad, sala.capacidadMinima
        )
        holder.txtEquipamiento.text = sala.equipamiento

        Glide.with(holder.itemView.context)
            .load(sala.imagenUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.imgSala)

        // Los botones avisan a SalasActivity, que se los pasa al presentador
        holder.btnVerDetalles.setOnClickListener { activity.verDetalle(sala) }
        holder.btnReservar.setOnClickListener { activity.reservar(sala) }
    }

    override fun getItemCount(): Int {
        return listaSalas.size
    }

    fun actualizarLista(nuevaLista: List<dataSalas>) {
        listaSalas = nuevaLista
        notifyDataSetChanged()
    }
}