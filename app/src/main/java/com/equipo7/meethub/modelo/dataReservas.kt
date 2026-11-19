package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Una reserva del usuario (obtener_mis_reservas.php)
@Serializable
data class dataReservas(
    @SerialName("id_reserva") val idReserva: Int = 0,
    @SerialName("folio") val folio: String = "",
    @SerialName("id_sala") val idSala: Int = 0,
    @SerialName("nombre_sala") val nombreSala: String = "",
    @SerialName("fecha_reserva") val fechaReserva: String = "",
    @SerialName("hora_inicio") val horaInicio: String = "",
    @SerialName("hora_fin") val horaFin: String = "",
    @SerialName("total_asistentes") val totalAsistentes: Int = 0,
    @SerialName("descripcion_evento") val descripcionEvento: String = "",
    @SerialName("estado_reserva") val estadoReserva: String = "",
    @SerialName("motivo_rechazo_cancelacion") val motivoRechazoCancelacion: String = ""
)