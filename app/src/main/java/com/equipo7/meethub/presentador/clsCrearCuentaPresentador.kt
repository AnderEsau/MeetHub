package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsCrearCuentaModelo
import com.equipo7.meethub.vista.CrearCuentaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsCrearCuentaPresentador(private val view: CrearCuentaActivity) {
    private val modelo = clsCrearCuentaModelo()

    fun crearCuenta(
        tipoUsuario: String,
        identificador: String,
        nombreCompleto: String,
        correo: String,
        telefono: String,
        psw: String,
        pregunta: String,
        respuesta: String
    ) {
        if (identificador.isEmpty() || nombreCompleto.isEmpty() || correo.isEmpty() ||
            telefono.isEmpty() || psw.isEmpty() || respuesta.isEmpty()
        ) {
            view.mostrarError("Todos los campos son obligatorios")
            return
        }

        if (psw.length < 8) {
            view.mostrarError("La contraseña debe tener mínimo 8 caracteres")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = modelo.crearCuenta(
                    tipoUsuario, identificador, nombreCompleto,
                    correo, telefono, psw, pregunta, respuesta
                )
                if (r.exito) {
                    view.cuentaCreada(r.mensaje)
                } else {
                    view.mostrarError(r.mensaje)
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> view.mostrarError("Datos incompletos o incorrectos.")
                    else -> view.mostrarError("Error en el servidor (${e.code()})")
                }
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }
}