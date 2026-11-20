package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsLoginModelo
import com.equipo7.meethub.vista.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
class clsLoginPresentador(private val view: LoginActivity) {
    private val modelo = clsLoginModelo()
    fun realizarLogin(identificador: String, psw: String) {
        if (identificador.isEmpty() || psw.isEmpty()) {
            view.mostrarError("Ingresa tu correo o matrícula y tu contraseña")
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val respuesta = modelo.autenticarUsuario(identificador, psw)
                if (respuesta.exito) {
                    view.loginExitoso(
                        respuesta.mensaje,
                        respuesta.tipoUsuario,
                        respuesta.idUsuario,
                        respuesta.nombreCompleto
                    )
                } else {
                    view.mostrarError(respuesta.mensaje)
                }
            } catch (e: HttpException) {
                view.mostrarError("Error en el servidor (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }
}
