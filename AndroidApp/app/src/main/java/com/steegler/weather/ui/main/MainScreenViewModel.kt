package com.steegler.weather.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.steegler.weather.LocationHelper
import com.steegler.weather.Resource
import com.steegler.weather.data.remote.GeoResponseItem
import com.steegler.weather.data.remote.WeatherResponse
import com.steegler.weather.domain.usecase.GetGeo
import com.steegler.weather.domain.usecase.GetGeoReverse
import com.steegler.weather.domain.usecase.GetWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getGeo: GetGeo,
    private val getWeather: GetWeather,
    private val getGeoReverse: GetGeoReverse,
    private val preferences: SharedPreferences,
    private val myLocationManager: LocationHelper
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearchingText = MutableStateFlow(false)
    val isSearchingText = _isSearchingText.asStateFlow()

    private val _cities = MutableStateFlow((listOf<GeoResponseItem>()))
    val cities = _cities.asStateFlow()

    private val _selectedCity: MutableStateFlow<GeoResponseItem?> = MutableStateFlow(null)
    val selectedCity = _selectedCity.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading = _isWeatherLoading.asStateFlow()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private val _lastSelectedCity: MutableStateFlow<GeoResponseItem?> = MutableStateFlow(null)
    val lastSelectedCity = _lastSelectedCity.asStateFlow()

    private val _locationCity: MutableStateFlow<GeoResponseItem?> = MutableStateFlow(null)
    val locationCity = _locationCity.asStateFlow()

    val location = myLocationManager.location
    fun restoreFromPref() {
        preferences.getString("city", null)?.let {
            Gson().fromJson(it, GeoResponseItem::class.java)?.let { item ->
                viewModelScope.launch(Dispatchers.Default) {
                    _lastSelectedCity.emit(item)
                }
            }
        }
    }

    fun defineLocation() {
        myLocationManager.runLocation()
        myLocationManager.location.onEach {
            it?.let {
                getGeoReverse(it).onEach { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _locationCity.emit(resource.data?.firstOrNull())
                        }

                        is Resource.Error -> {
                            _locationCity.emit(null)
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)

    }

    fun requestWeather(city: GeoResponseItem) {

        viewModelScope.launch(Dispatchers.Default) {
            _selectedCity.emit(city)
            _lastSelectedCity.emit(city)
            _cities.emit(emptyList())
            _searchText.emit("${city.name},${city.state}")
            val c = Gson().toJson(city)
            preferences.edit().putString("city", c).apply()
        }

        getWeather(city.lat, city.lon).let {
            it.onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _isWeatherLoading.emit(false)
                        _weather.emit(resource.data)
                    }

                    is Resource.Error -> {
                        _isWeatherLoading.emit(false)
                        _weather.emit(null)
                    }

                    is Resource.Loading -> {
                        _isWeatherLoading.emit(true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun searchCity(text: String) {

        val fixedText = text.split(",").first()

        viewModelScope.launch(Dispatchers.IO) {
            _searchText.emit(fixedText)
        }

        if (fixedText.isEmpty()) {
            viewModelScope.launch {
                _cities.emit(emptyList())
            }
            return
        }

        getGeo(fixedText)
            .let {


                it.onEach { resource ->

                    when (resource) {
                        is Resource.Success -> {
                            val list = resource.data!!.toList()
                            _cities.emit(list)
                            _isSearchingText.emit(false)
                        }

                        is Resource.Error -> {
                            _isSearchingText.emit(false)

                        }

                        is Resource.Loading -> {
                            _isSearchingText.emit(true)
                        }
                    }

                }
            }.launchIn(viewModelScope)
    }


}