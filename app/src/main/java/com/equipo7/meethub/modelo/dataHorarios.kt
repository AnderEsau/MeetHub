package com.equipo7.meethub.modelo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Una franja de horario. estado puede ser Libre, Pendiente u Ocupado
@Serializable
data class dataHorarios(
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String = "",
    @SerialName("estado") val estado: String = ""
)