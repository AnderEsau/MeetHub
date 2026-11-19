package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Si respuesta va vacía, la API solo regresa la pregunta secreta
@Serializable
data class VerificarIdentidadRequest(
    @SerialName("identificador") val identificador: String = "",
    @SerialName("respuesta") val respuesta: String = ""
)