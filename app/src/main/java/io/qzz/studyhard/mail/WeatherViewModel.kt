package io.qzz.studyhard.mail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    fun fetchWeather(city: String = "Shanghai") {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            
            try {
                val response = WeatherApi.service.getWeather(city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = WeatherUiState.Success(it)
                    } ?: run {
                        _uiState.value = WeatherUiState.Error("返回数据为空")
                    }
                } else {
                    _uiState.value = WeatherUiState.Error("API错误: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("网络错误: ${e.message}")
            }
        }
    }
} 