package com.steegler.weather.domain.repository

import com.steegler.weather.Resource
import com.steegler.weather.data.remote.GeoResponse
import com.steegler.weather.data.remote.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherFor(latitude: Double, longitude: Double): Resource<WeatherResponse>
    suspend fun getGeoFor(latitude: Double, longitude: Double): Resource<GeoResponse>
    suspend fun getGeoFor(cityName: String): Resource<GeoResponse>

}