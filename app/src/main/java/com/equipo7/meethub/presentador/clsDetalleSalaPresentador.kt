package com.equipo7.meethub.presentador

import com.equipo7.meethub.modelo.clsDetalleSalaModelo
import com.equipo7.meethub.modelo.dataDetalleSala
import com.equipo7.meethub.vista.DetalleSalaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
class clsDetalleSalaPresentador(private val view: DetalleSalaActivity) {
    private val modelo = clsDetalleSalaModelo()
    fun cargarDetalle(salaId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val sala: dataDetalleSala = modelo.obtenerDetalleSala(salaId)
                view.mostrarDetalle(sala)
            } catch (e: HttpException) {
                when (e.code()) {
                    404 -> view.mostrarError("La sala no existe o no está disponible.")
                    400 -> view.mostrarError("Sala no válida.")
                    else -> view.mostrarError("Error en el servidor (${e.code()})")
                }
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }
}