package com.example.myubike.network

import com.example.myubike.model.UBike
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private const val BASE_URL = "https://tcgbusfs.blob.core.windows.net/dotapp/youbike/v2/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface UBikeApiService {
    @GET("youbike_immediate.json")
    suspend fun getUBikeData(): List<UBike>
}

object UBikeApi {
    val retrofitService: UBikeApiService by lazy {
        retrofit.create(UBikeApiService::class.java)
    }
}