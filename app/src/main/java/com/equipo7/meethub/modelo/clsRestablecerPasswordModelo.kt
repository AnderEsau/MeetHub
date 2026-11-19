package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsRestablecerPasswordModelo {

    // Paso 1: pide la pregunta secreta del usuario
    suspend fun obtenerPregunta(identificador: String): VerificarIdentidadResponse {
        val request = VerificarIdentidadRequest(identificador = identificador)
        return RetrofitCliente.apiService.verificarIdentidad(request)
    }

    // Paso 2: comprueba la respuesta secreta
    suspend fun validarRespuesta(identificador: String, respuesta: String): VerificarIdentidadResponse {
        val request = VerificarIdentidadRequest(
            identificador = identificador,
            respuesta = respuesta
        )
        return RetrofitCliente.apiService.verificarIdentidad(request)
    }

    // Paso 3: guarda la nueva contraseña
    suspend fun restablecerPassword(
        identificador: String,
        respuesta: String,
        nuevaPassword: String,
        confirmarPassword: String
    ): RestablecerPasswordResponse {
        val request = RestablecerPasswordRequest(
            identificador = identificador,
            respuesta = respuesta,
            nuevaPassword = nuevaPassword,
            confirmarPassword = confirmarPassword
        )
        return RetrofitCliente.apiService.restablecerPassword(request)
    }
}