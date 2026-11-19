package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrarSugerenciaRequest(
    @SerialName("tipo_usuario") val tipoUsuario: String = "",
    @SerialName("id_usuario") val idUsuario: Int = 0,
    @SerialName("id_reserva") val idReserva: Int = 0,
    @SerialName("comentario") val comentario: String = ""
)