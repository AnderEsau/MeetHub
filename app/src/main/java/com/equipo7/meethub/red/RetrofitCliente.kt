package com.equipo7.meethub.red

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitCliente {
    const val BASE_URL = "https://anderehj.somos4a.org/meethub_api/" // Url del api

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    val apiService: ApiService by lazy {
        val contenType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConfig.asConverterFactory(contenType))
            .build()
            .create(ApiService::class.java)
    }
}