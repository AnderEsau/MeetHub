package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestablecerPasswordRequest(
    @SerialName("identificador") val identificador: String = "",
    @SerialName("respuesta") val respuesta: String = "",
    @SerialName("nueva_password") val nuevaPassword: String = "",
    @SerialName("confirmar_password") val confirmarPassword: String = ""
)