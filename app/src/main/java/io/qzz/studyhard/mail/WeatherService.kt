package io.qzz.studyhard.mail

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

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
    
    // 设置超时时间
    private const val TIMEOUT_CONNECT = 15L // 连接超时15秒
    private const val TIMEOUT_READ = 30L    // 读取超时30秒
    private const val TIMEOUT_WRITE = 30L   // 写入超时30秒
    
    // 创建日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // 创建OkHttpClient并添加拦截器
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: WeatherApiService = retrofit.create(WeatherApiService::class.java)
} 