package io.qzz.studyhard.mail

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "cb22d76cf9da450ab00ffa42b3d64c9a" // 更新的OpenWeatherMap API密钥
    ): Response<WeatherResponse>
}

object WeatherApi {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: WeatherApiService = retrofit.create(WeatherApiService::class.java)
} 