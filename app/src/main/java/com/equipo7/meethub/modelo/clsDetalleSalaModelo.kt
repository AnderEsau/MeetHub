package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsDetalleSalaModelo {
    suspend fun obtenerDetalleSala(salaId: Int): dataDetalleSala {
        return RetrofitCliente.apiService.detalleSala(salaId)
    }
}