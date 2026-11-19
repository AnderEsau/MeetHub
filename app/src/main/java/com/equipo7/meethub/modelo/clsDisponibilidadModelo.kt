package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsDisponibilidadModelo {
    // La fecha va en formato 2026-11-23
    suspend fun obtenerDisponibilidad(salaId: Int, fecha: String): dataDisponibilidad {
        return RetrofitCliente.apiService.obtenerDisponibilidad(salaId, fecha)
    }
}