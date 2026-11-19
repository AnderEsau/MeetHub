package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsSolicitudModelo {
    suspend fun crearSolicitud(
        tipoUsuario: String,
        idUsuario: Int,
        idSala: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        asistentes: Int,
        actividad: String
    ): CrearSolicitudResponse {
        val request = CrearSolicitudRequest(
            tipoUsuario = tipoUsuario,
            idUsuario = idUsuario,
            idSala = idSala,
            fechaReserva = fecha,
            horaInicio = horaInicio,
            horaFin = horaFin,
            totalAsistentes = asistentes,
            descripcionEvento = actividad
        )
        return RetrofitCliente.apiService.crearSolicitud(request)
    }
}