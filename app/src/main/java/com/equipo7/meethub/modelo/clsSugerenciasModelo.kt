package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsSugerenciasModelo {

    // Reservas que ya se usaron y todavía no tienen sugerencia (sin_sugerencia = 1)
    suspend fun obtenerReservasParaOpinar(tipoUsuario: String, idUsuario: Int): List<dataReservas> {
        return RetrofitCliente.apiService.obtenerMisReservas(tipoUsuario, idUsuario, 1)
    }

    suspend fun registrarSugerencia(
        tipoUsuario: String,
        idUsuario: Int,
        idReserva: Int,
        comentario: String
    ): RegistrarSugerenciaResponse {
        val request = RegistrarSugerenciaRequest(
            tipoUsuario = tipoUsuario,
            idUsuario = idUsuario,
            idReserva = idReserva,
            comentario = comentario
        )
        return RetrofitCliente.apiService.registrarSugerencia(request)
    }
}