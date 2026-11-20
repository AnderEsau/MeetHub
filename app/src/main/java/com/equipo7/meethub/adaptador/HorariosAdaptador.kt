package com.equipo7.meethub.adaptador


import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.equipo7.meethub.R
import com.equipo7.meethub.modelo.dataHorarios

class HorariosAdaptador(
    private var listaHorarios: List<dataHorarios>,
    private val layoutItem: Int = R.layout.item_horario
) : RecyclerView.Adapter<HorariosAdaptador.HorarioViewHolder>() {

    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHorario: TextView = itemView.findViewById(R.id.txtHorario)
        val txtEstado: TextView? = itemView.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutItem, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorarioViewHolder, position: Int) {
        val horario = listaHorarios[position]
        val hora = horario.horaInicio + " - " + horario.horaFin

        if (holder.txtEstado != null) {
            holder.txtHorario.text = hora
            holder.txtEstado.text = horario.estado
        } else {
            holder.txtHorario.text = hora + " | " + horario.estado
        }

        val contexto = holder.itemView.context
        val colorTexto: Int

        when (horario.estado) {
            "Libre" -> {
                holder.itemView.background = crearFondo(
                    contexto.getColor(R.color.verde), false, contexto
                )
                colorTexto = contexto.getColor(R.color.blanco)
            }

            "Ocupado" -> {
                holder.itemView.background = crearFondo(
                    contexto.getColor(R.color.rojo), false, contexto
                )
                colorTexto = contexto.getColor(R.color.blanco)
            }

            else -> {
                holder.itemView.background = crearFondo(
                    contexto.getColor(R.color.pendiente), true, contexto
                )
                colorTexto = contexto.getColor(R.color.texto)
            }
        }

        holder.txtHorario.setTextColor(colorTexto)
        holder.txtEstado?.setTextColor(colorTexto)
    }

    private fun crearFondo(
        color: Int,
        punteado: Boolean,
        contexto: android.content.Context
    ): GradientDrawable {
        val densidad = contexto.resources.displayMetrics.density
        val fondo = GradientDrawable()
        fondo.setColor(color)
        fondo.cornerRadius = 8 * densidad

        if (punteado) {
            fondo.setStroke(
                (1 * densidad).toInt(),
                contexto.getColor(R.color.cafe),
                4 * densidad,
                3 * densidad
            )
        }

        return fondo
    }

    override fun getItemCount(): Int {
        return listaHorarios.size
    }

    fun actualizarLista(nuevaLista: List<dataHorarios>) {
        listaHorarios = nuevaLista
        notifyDataSetChanged()
    }
}
