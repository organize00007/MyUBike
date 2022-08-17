package com.example.myubike.network

import com.example.myubike.model.GoogleMapPath
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private const val BASE_URL = "https://maps.googleapis.com/maps/api/directions/"

private val client = OkHttpClient.Builder()
    .connectTimeout(30000, TimeUnit.SECONDS)
    .readTimeout(30000, TimeUnit.SECONDS)

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client.build())
    .build()

interface PathApiService {
    @GET("json")
    suspend fun getData(@Query("origin") origin: String, @Query("destination") destination: String, @Query("mode") mode: String = "walking", @Query("key") key: String): GoogleMapPath
}

object PathApi {
    val retrofitService: PathApiService by lazy {
        retrofit.create(PathApiService::class.java)
    }
}