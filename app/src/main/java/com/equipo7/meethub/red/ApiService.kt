package com.equipo7.meethub.red

import com.equipo7.meethub.modelo.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // ---------- Leonel ----------
    @GET("obtener_disponibilidad.php")
    suspend fun obtenerDisponibilidad(
        @Query("sala_id") salaId: Int,
        @Query("fecha") fecha: String
    ): dataDisponibilidad

    @POST("crear_solicitud.php")
    suspend fun crearSolicitud(@Body request: CrearSolicitudRequest): CrearSolicitudResponse

}