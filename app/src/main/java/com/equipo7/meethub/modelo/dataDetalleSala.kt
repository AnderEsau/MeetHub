package com.equipo7.meethub.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataDetalleSala(
    @SerialName("id_sala") val idSala: Int = 0,
    @SerialName("nombre_sala") val nombreSala: String = "",
    @SerialName("id_tipo_sala") val idTipoSala: Int = 0,
    @SerialName("nombre_tipo") val nombreTipo: String = "",
    @SerialName("ubicacion") val ubicacion: String = "",
    @SerialName("descripcion") val descripcion: String = "Sin descripcion",
    @SerialName("capacidad") val capacidad: Int = 0,
    @SerialName("capacidad_minima") val capacidadMinima: Int = 0,
    @SerialName("hora_apertura") val horaApertura: String = "",
    @SerialName("hora_cierre") val horaCierre: String = "",
    @SerialName("imagen_url") val imagenUrl: String = "",
    @SerialName("equipamiento_texto") val equipamientoTexto: String = "",
    @SerialName("equipamiento") val equipamiento: List<dataEquipamiento> = emptyList(),
    @SerialName("galeria") val galeria: List<String> = emptyList()

)
