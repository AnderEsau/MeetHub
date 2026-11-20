package com.equipo7.meethub.presentador


import com.equipo7.meethub.modelo.clsSalasModelo
import com.equipo7.meethub.modelo.clsDisponibilidadModelo
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.vista.InicioActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsInicioPresentador(private val view: InicioActivity) {
    private val modeloSalas = clsSalasModelo()
    private val modeloDisponibilidad = clsDisponibilidadModelo()
    private var salas: List<dataSalas> = emptyList()

    fun iniciar() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                salas = modeloSalas.obtenerSalas()
                view.mostrarSalas(salas)
            } catch (e: HttpException) {
                view.mostrarError("Error al cargar las salas (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    fun consultarDisponibilidad(posicionSala: Int, fecha: String) {
        if (posicionSala < 0 || posicionSala >= salas.size) return
        if (fecha.isEmpty()) {
            view.mostrarError("Elige una fecha")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val idSala = salas[posicionSala].idSala
                val respuesta = modeloDisponibilidad.obtenerDisponibilidad(idSala, fecha)
                view.mostrarHorarios(respuesta.horarios)
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> view.mostrarError("Solo se puede consultar de lunes a viernes.")
                    404 -> view.mostrarError("La sala no existe o no está disponible.")
                    else -> view.mostrarError("Error en el servidor (${e.code()})")
                }
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    // Para el botón "Reservar sala": regresa el id de la sala elegida
    fun idSalaSeleccionada(posicionSala: Int): Int {
        return if (posicionSala in salas.indices) salas[posicionSala].idSala else 0
    }
}