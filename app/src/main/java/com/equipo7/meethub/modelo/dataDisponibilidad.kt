package com.equipo7.meethub.modelo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Lo que responde obtener_disponibilidad.php
@Serializable
data class dataDisponibilidad(
    @SerialName("id_sala") val idSala: Int = 0,
    @SerialName("nombre_sala") val nombreSala: String = "",
    @SerialName("fecha") val fecha: String = "",
    @SerialName("horarios") val horarios: List<dataHorarios> = emptyList()
)