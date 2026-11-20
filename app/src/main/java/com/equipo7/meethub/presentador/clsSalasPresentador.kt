package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsSalasModelo
import com.equipo7.meethub.modelo.dataSalas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsSalasPresentador(private val view: SalasActivity) {
    private val modelo = clsSalasModelo()

    fun cargarSalas() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val salas = modelo.obtenerSalas()
                view.mostrarSalas(salas)
            } catch (e: HttpException) {
                view.mostrarError("Error al cargar las salas (${e.code()})")
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    // Botón "Ver detalles" de una tarjeta
    fun verDetalle(sala: dataSalas) {
        view.abrirDetalleSala(sala.idSala)
    }

    // Botón "Reservar sala" de una tarjeta
    fun reservar(sala: dataSalas) {
        view.abrirNuevaSolicitud(sala.idSala)
    }
}