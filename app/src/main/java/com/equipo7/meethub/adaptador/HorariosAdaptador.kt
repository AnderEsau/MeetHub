package com.equipo7.meethub.adaptador


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.equipo7.meethub.R
import com.equipo7.meethub.modelo.dataHorarios

class HorariosAdaptador(
    private var listaHorarios: List<dataHorarios>
) : RecyclerView.Adapter<HorariosAdaptador.HorarioViewHolder>() {

    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHorario: TextView = itemView.findViewById(R.id.txtHorario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = listaHorarios[position]
        holder.txtHorario.text = horario.horaInicio + " - " + horario.horaFin + " | " + horario.estado

        // El color depende del estado: Libre verde, Ocupado rojo, Pendiente beige
        when (horario.estado) {
            "Libre" -> {
                holder.txtHorario.setBackgroundColor(Color.parseColor("#2E7D32"))
                holder.txtHorario.setTextColor(Color.WHITE)
            }
            "Ocupado" -> {
                holder.txtHorario.setBackgroundColor(Color.parseColor("#C62828"))
                holder.txtHorario.setTextColor(Color.WHITE)
            }
            else -> {
                holder.txtHorario.setBackgroundColor(Color.parseColor("#F5EBDD"))
                holder.txtHorario.setTextColor(Color.parseColor("#222222"))
            }
        }
    }

    override fun getItemCount(): Int {
        return listaHorarios.size
    }

    fun actualizarLista(nuevaLista: List<dataHorarios>) {
        listaHorarios = nuevaLista
        notifyDataSetChanged()
    }
}