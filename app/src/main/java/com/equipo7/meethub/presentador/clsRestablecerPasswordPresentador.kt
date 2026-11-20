package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsRestablecerPasswordModelo
import com.equipo7.meethub.vista.RestablecerPasswordActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsRestablecerPasswordPresentador(private val view: RestablecerPasswordActivity) {

    private val modelo = clsRestablecerPasswordModelo()

    // Paso 1: botón "Consultar pregunta"
    fun consultarPregunta(identificador: String) {
        if (identificador.isEmpty()) {
            view.mostrarError("Ingresa tu correo o matrícula")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = modelo.obtenerPregunta(identificador)
                if (r.exito) {
                    view.mostrarPregunta(r.pregunta)
                } else {
                    view.mostrarError(r.mensaje)
                }
            } catch (e: HttpException) {
                view.mostrarError("Error en el servidor (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    // Paso 2: botón "Validar respuesta"
    fun validarRespuesta(identificador: String, respuesta: String) {
        if (respuesta.isEmpty()) {
            view.mostrarError("Escribe tu respuesta secreta")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = modelo.validarRespuesta(identificador, respuesta)
                if (r.exito) {
                    view.respuestaValida(r.mensaje)
                } else {
                    view.mostrarError(r.mensaje)
                }
            } catch (e: HttpException) {
                view.mostrarError("Error en el servidor (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    // Paso 3: botón "Guardar contraseña nueva"
    fun guardarPassword(
        identificador: String,
        respuesta: String,
        nueva: String,
        confirmar: String
    ) {
        if (nueva.isEmpty() || confirmar.isEmpty()) {
            view.mostrarError("Escribe y confirma tu nueva contraseña")
            return
        }
        if (nueva != confirmar) {
            view.mostrarError("Las contraseñas no coinciden")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = modelo.restablecerPassword(identificador, respuesta, nueva, confirmar)
                if (r.exito) {
                    view.passwordRestablecida(r.mensaje)
                } else {
                    view.mostrarError(r.mensaje)
                }
            } catch (e: HttpException) {
                view.mostrarError("Error en el servidor (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }
}