package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("identificador") val identificador: String = "",
    @SerialName("psw") val psw: String = ""
)

