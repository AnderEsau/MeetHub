package com.equipo7.meethub.modelo

import com.equipo7.meethub.red.RetrofitCliente

class clsSalasModelo {
    suspend fun obtenerSalas(): List<dataSalas> {
        return RetrofitCliente.apiService.obtenerSalas()
    }
}