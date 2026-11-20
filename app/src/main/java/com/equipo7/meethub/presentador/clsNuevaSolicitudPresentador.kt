package com.equipo7.meethub.presentador


import com.equipo7.meethub.modelo.clsSalasModelo
import com.equipo7.meethub.modelo.clsDisponibilidadModelo
import com.equipo7.meethub.modelo.clsSolicitudModelo
import com.equipo7.meethub.modelo.dataSalas
import com.equipo7.meethub.vista.NuevaSolicitudActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class clsNuevaSolicitudPresentador(private val view: NuevaSolicitudActivity) {
    private val modeloSalas = clsSalasModelo()
    private val modeloDisponibilidad = clsDisponibilidadModelo()
    private val modeloSolicitud = clsSolicitudModelo()
    private var salas: List<dataSalas> = emptyList()

    // salaIdInicial es el SALA_ID que llegó por Intent (0 si no se eligió una)
    fun iniciar(salaIdInicial: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                salas = modeloSalas.obtenerSalas()
                val posicion = salas.indexOfFirst { it.idSala == salaIdInicial }
                view.mostrarSalas(salas, if (posicion >= 0) posicion else 0)
            } catch (e: Exception) {
                view.mostrarError("Error al cargar las salas.")
            }
        }
    }

    fun consultarHorarios(posicionSala: Int, fecha: String) {
        if (posicionSala < 0 || posicionSala >= salas.size || fecha.isEmpty()) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val idSala = salas[posicionSala].idSala
                val respuesta = modeloDisponibilidad.obtenerDisponibilidad(idSala, fecha)
                view.mostrarHorarios(respuesta.horarios)
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    view.mostrarError("Solo se puede reservar de lunes a viernes.")
                } else {
                    view.mostrarError("Error en el servidor (${e.code()})")
                }
            } catch (e: Exception) {
                view.mostrarError("Error de conexión con el servidor.")
            }
        }
    }

    fun enviarSolicitud(
        tipoUsuario: String,
        idUsuario: Int,
        posicionSala: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        asistentes: String,
        actividad: String
    ) {
        if (posicionSala < 0 || posicionSala >= salas.size || fecha.isEmpty() ||
            horaInicio.isEmpty() || horaFin.isEmpty() || actividad.isEmpty()) {
            view.mostrarError("Todos los campos son obligatorios.")
            return
        }

        val total = asistentes.toIntOrNull()
        if (total == null || total <= 0) {
            view.mostrarError("Escribe un número de asistentes válido.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val r = modeloSolicitud.crearSolicitud(
                    tipoUsuario, idUsuario, salas[posicionSala].idSala,
                    fecha, horaInicio, horaFin, total, actividad
                )
                if (r.exito) {
                    view.solicitudEnviada(r.mensaje + " Folio: " + r.folio)
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