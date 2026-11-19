package com.equipo7.meethub.modelo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// fecha_reserva en formato 2026-12-02 y las horas como 10:00
@Serializable
data class CrearSolicitudRequest(
    @SerialName("tipo_usuario") val tipoUsuario: String = "",
    @SerialName("id_usuario") val idUsuario: Int = 0,
    @SerialName("id_sala") val idSala: Int = 0,
    @SerialName("fecha_reserva") val fechaReserva: String = "",
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String = "",
    @SerialName("total_asistentes") val totalAsistentes: Int = 0,
    @SerialName("descripcion_evento") val descripcionEvento: String = ""
)