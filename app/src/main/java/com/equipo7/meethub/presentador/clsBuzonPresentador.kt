package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsSugerenciasModelo
import com.equipo7.meethub.modelo.dataReservas
import com.equipo7.meethub.vista.BuzonActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsBuzonPresentador(private val view: BuzonActivity) {

    private val modelo = clsSugerenciasModelo()
    private var reservas: List<dataReservas> = emptyList()

    fun cargarReservas(tipoUsuario: String, idUsuario: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                reservas = modelo.obtenerReservasParaOpinar(tipoUsuario, idUsuario)
                if (reservas.isEmpty()) {
                    view.mostrarSinReservas()
                } else {
                    view.mostrarReservas(reservas)
                }
            } catch (e: HttpException) {
                view.mostrarError("Error al cargar tus salas (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    fun enviarSugerencia(
        tipoUsuario: String,
        idUsuario: Int,
        posicionReserva: Int,
        comentario: String
    ) {
        if (posicionReserva < 0 || posicionReserva >= reservas.size) {
            view.mostrarError("Elige la sala que utilizaste")
            return
        }
        if (comentario.isEmpty()) {
            view.mostrarError("Escribe tu sugerencia o comentario")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val idReserva = reservas[posicionReserva].idReserva
                val r = modelo.registrarSugerencia(tipoUsuario, idUsuario, idReserva, comentario)
                if (r.exito) {
                    view.sugerenciaEnviada(r.mensaje)
                    cargarReservas(tipoUsuario, idUsuario)
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