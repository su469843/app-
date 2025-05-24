package io.qzz.studyhard.mail

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeoutException

sealed class EmailUiState {
    data object Idle : EmailUiState()
    data object Sending : EmailUiState()
    data class Success(val message: String) : EmailUiState()
    data class Error(val message: String) : EmailUiState()
}

class EmailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EmailUiState>(EmailUiState.Idle)
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()
    
    // 发送天气信息邮件
    fun sendWeatherEmail(to: String, weather: WeatherResponse) {
        viewModelScope.launch {
            _uiState.value = EmailUiState.Sending
            
            try {
                // 构建邮件HTML内容
                val emailHtml = buildWeatherEmailHtml(weather)
                
                // 创建邮件请求
                val emailRequest = EmailRequest(
                    to = to,
                    subject = "天气预报: ${weather.name}, ${weather.sys.country}",
                    html = emailHtml
                )
                
                // 发送邮件
                val response = EmailApi.service.sendEmail(emailRequest)
                
                if (response.isSuccessful) {
                    _uiState.value = EmailUiState.Success("邮件已成功发送到 $to")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uiState.value = EmailUiState.Error(
                        "发送失败: ${response.code()}${errorBody?.let { " - $it" } ?: ""}"
                    )
                }
            } catch (e: IOException) {
                _uiState.value = EmailUiState.Error("网络连接错误: ${e.localizedMessage}")
            } catch (e: SocketTimeoutException) {
                _uiState.value = EmailUiState.Error("请求超时: ${e.localizedMessage}")
            } catch (e: TimeoutException) {
                _uiState.value = EmailUiState.Error("请求超时: ${e.localizedMessage}")
            } catch (e: HttpException) {
                _uiState.value = EmailUiState.Error("HTTP错误: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                _uiState.value = EmailUiState.Error("发送失败: ${e.localizedMessage ?: e.javaClass.simpleName}")
            }
        }
    }
    
    // 构建天气邮件HTML内容
    private fun buildWeatherEmailHtml(weather: WeatherResponse): String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val temp = weather.main.temp.toInt()
        val weatherDescription = weather.weather.firstOrNull()?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } ?: ""
        val humidity = weather.main.humidity
        val windSpeed = weather.wind.speed
        val visibility = weather.visibility / 1000
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>天气预报</title>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #f9f9f9; border-radius: 10px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 20px; color: #1A237E; }
                    .weather-main { text-align: center; padding: 20px 0; background: linear-gradient(to right, #1A237E, #3949AB); color: white; border-radius: 8px; margin-bottom: 20px; }
                    .temperature { font-size: 48px; font-weight: bold; margin: 10px 0; }
                    .city { font-size: 24px; margin-bottom: 5px; }
                    .description { font-size: 18px; }
                    .details { display: flex; justify-content: space-around; margin: 20px 0; text-align: center; }
                    .detail-item { padding: 10px; }
                    .detail-label { color: #666; font-size: 14px; }
                    .detail-value { font-size: 18px; font-weight: bold; color: #1A237E; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>天气预报</h1>
                        <p>生成时间: $date</p>
                    </div>
                    <div class="weather-main">
                        <div class="city">${weather.name}, ${weather.sys.country}</div>
                        <div class="temperature">${temp}°C</div>
                        <div class="description">$weatherDescription</div>
                    </div>
                    <div class="details">
                        <div class="detail-item">
                            <div class="detail-label">湿度</div>
                            <div class="detail-value">${humidity}%</div>
                        </div>
                        <div class="detail-item">
                            <div class="detail-label">风速</div>
                            <div class="detail-value">${windSpeed} m/s</div>
                        </div>
                        <div class="detail-item">
                            <div class="detail-label">能见度</div>
                            <div class="detail-value">${visibility} km</div>
                        </div>
                    </div>
                    <div class="footer">
                        <p>此邮件由天气预报应用自动发送</p>
                        <p>© ${SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())} 天气预报 - studyhard.qzz.io</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
    
    // 重置状态
    fun resetState() {
        _uiState.value = EmailUiState.Idle
    }
} 