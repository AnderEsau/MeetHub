package com.equipo7.meethub.red

import com.equipo7.meethub.modelo.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Ander
    @POST("login.php")
    suspend fun iniciarSesion(@Body request: LoginRequest): LoginResponse

    @GET("detalle_sala.php")
    suspend fun detalleSala(@Query("sala_id") salaId: Int): dataDetalleSala

    // Samantha
    @GET("obtener_salas.php")
    suspend fun obtenerSalas(): List<dataSalas>

    @POST("crear_cuenta.php")
    suspend fun crearCuenta(@Body request: CrearCuentaRequest): CrearCuentaResponse

    // Lary
    @POST("verificar_identidad.php")
    suspend fun verificarIdentidad(@Body request: VerificarIdentidadRequest): VerificarIdentidadResponse

    @POST("restablecer_password.php")
    suspend fun restablecerPassword(@Body request: RestablecerPasswordRequest): RestablecerPasswordResponse

    @GET("obtener_mis_reservas.php")
    suspend fun obtenerMisReservas(
        @Query("tipo_usuario") tipoUsuario: String,
        @Query("id_usuario") idUsuario: Int,
        @Query("sin_sugerencia") sinSugerencia: Int
    ): List<dataReservas>

    @POST("registrar_sugerencia.php")
    suspend fun registrarSugerencia(@Body request: RegistrarSugerenciaRequest): RegistrarSugerenciaResponse

    // Leonel
    @GET("obtener_disponibilidad.php")
    suspend fun obtenerDisponibilidad(
        @Query("sala_id") salaId: Int,
        @Query("fecha") fecha: String
    ): dataDisponibilidad

    @POST("crear_solicitud.php")
    suspend fun crearSolicitud(@Body request: CrearSolicitudRequest): CrearSolicitudResponse
}