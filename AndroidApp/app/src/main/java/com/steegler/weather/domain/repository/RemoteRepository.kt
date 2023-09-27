package com.steegler.weather.domain.repository

import com.steegler.weather.Resource
import com.steegler.weather.data.remote.GeoResponse
import com.steegler.weather.data.remote.WeatherResponse
import com.steegler.weather.network.WeatherAPI

class RemoteRepository constructor(
    private val api: WeatherAPI
) : WeatherRepository {
    override suspend fun getWeatherFor(latitude: Double, longitude: Double): Resource<WeatherResponse> {
        return Resource.Success(api.getWeather(lat = latitude, lon = longitude))
    }

    override suspend fun getGeoFor(cityName: String): Resource<GeoResponse> {
        return Resource.Success(api.getGeoDataFor("$cityName,US"))
    }


}