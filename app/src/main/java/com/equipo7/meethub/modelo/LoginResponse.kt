package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("exito") val exito: Boolean = false,
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("tipo_usuario") val tipoUsuario: String = "",
    @SerialName("id_usuario") val idUsuario: Int = 0,
    @SerialName("identificador") val identificador: String = "",
    @SerialName("nombre_completo") val nombreCompleto: String = "",
    @SerialName("correo_electronico") val correoElectronico: String = "",
    @SerialName("telefono") val telefono: String = ""
)
