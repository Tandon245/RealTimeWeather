package com.example.realtimeweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimeweather.api.Constant
import com.example.realtimeweather.api.RetrofitInstance
import com.example.realtimeweather.api.WeatherModel
import com.example.realtimeweather.api.NetworkResponse
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value=NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                        Log.i("WeatherViewModel", "Weather data loaded successfully: $it")
                    } ?: run {
                        _weatherResult.value = NetworkResponse.Error("Response body is null")
                        Log.e("WeatherViewModel", "Response body is null")
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data: ${response.message()}")
                    Log.e("WeatherViewModel", "Failed to load data: ${response.message()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Exception: ${e.message}")
                Log.e("WeatherViewModel", "Exception occurred: ${e.message}", e)
            }
        }
    }
}
