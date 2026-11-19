package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsLoginModelo {
    suspend fun autenticarUsuario(identificador: String, psw: String): LoginResponse {
        val request = LoginRequest(identificador = identificador, psw = psw)
        return RetrofitCliente.apiService.iniciarSesion(request)
    }

}