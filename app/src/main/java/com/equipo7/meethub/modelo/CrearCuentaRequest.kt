package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// tipo_usuario puede ser Alumno, Docente o Usuario Externo. identificador es la matrícula o el RFC
@Serializable
data class CrearCuentaRequest(
    @SerialName("tipo_usuario") val tipoUsuario: String = "",
    @SerialName("identificador") val identificador: String = "",
    @SerialName("nombre_completo") val nombreCompleto: String = "",
    @SerialName("correo_electronico") val correoElectronico: String = "",
    @SerialName("telefono") val telefono: String = "",
    @SerialName("psw") val psw: String = "",
    @SerialName("pregunta_secreta") val preguntaSecreta: String = "",
    @SerialName("respuesta_secreta") val respuestaSecreta: String = ""
)