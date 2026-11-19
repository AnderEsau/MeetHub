package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataEquipamiento(
    @SerialName("id_equipamiento") val idEquipamiento: Int = 0,
    @SerialName("nombre_equipamiento") val nombreEquipamiento: String = "",
    @SerialName("descripcion") val descripcion: String = "",
    @SerialName("tipo_equipamiento") val tipoEquipamiento: String = "",
    @SerialName("cantidad") val cantidad: Int = 1
)
