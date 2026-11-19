package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsCrearCuentaModelo {
    suspend fun crearCuenta(
        tipoUsuario: String,
        identificador: String,
        nombreCompleto: String,
        correo: String,
        telefono: String,
        psw: String,
        pregunta: String,
        respuesta: String
    ): CrearCuentaResponse {
        val request = CrearCuentaRequest(
            tipoUsuario = tipoUsuario,
            identificador = identificador,
            nombreCompleto = nombreCompleto,
            correoElectronico = correo,
            telefono = telefono,
            psw = psw,
            preguntaSecreta = pregunta,
            respuestaSecreta = respuesta
        )
        return RetrofitCliente.apiService.crearCuenta(request)
    }
}