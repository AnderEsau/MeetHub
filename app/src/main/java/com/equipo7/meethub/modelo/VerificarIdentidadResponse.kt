package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificarIdentidadResponse(
    @SerialName("exito") val exito: Boolean = false,
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("pregunta") val pregunta: String = ""
)