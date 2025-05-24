package io.qzz.studyhard.mail

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface EmailApiService {
    @POST("emails/send")
    suspend fun sendEmail(@Body request: EmailRequest): Response<EmailResponse>
    
    @POST("emails/send-with-attachments")
    suspend fun sendEmailWithAttachments(@Body request: AttachmentEmailRequest): Response<EmailResponse>
    
    @POST("emails/schedule")
    suspend fun scheduleEmail(@Body request: ScheduledEmailRequest): Response<ScheduledEmailResponse>
    
    @POST("emails/send-bulk")
    suspend fun sendBulkEmail(@Body request: BulkEmailRequest): Response<EmailResponse>
    
    @GET("emails/scheduled")
    suspend fun getScheduledEmails(): Response<List<ScheduledEmailData>>
    
    @DELETE("emails/scheduled/{id}")
    suspend fun deleteScheduledEmail(@Path("id") id: String): Response<Map<String, Any>>
    
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>
}

object EmailApi {
    private const val BASE_URL = "https://email.studyhard.qzz.io/api/"
    
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
    
    val service: EmailApiService = retrofit.create(EmailApiService::class.java)
} 