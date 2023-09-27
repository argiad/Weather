package com.steegler.weather.network

import com.steegler.weather.Constants
import com.steegler.weather.data.remote.GeoResponse
import com.steegler.weather.data.remote.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

//API calls
//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
//https://api.openweathermap.org/data/2.5/weather?q={city name},{country code}&appid={API key}
//https://api.openweathermap.org/data/2.5/weather?q={city name},{state code},{country code}&appid={API key}
//https://api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid={API key}
//https://api.openweathermap.org/data/2.5/weather?lat=33.44&lon=-9ee4.04&appid={API key}

interface WeatherAPI {

    @GET(Constants.PATH_WEATHER)
    suspend fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") appid: String = Constants.API_KEY): WeatherResponse

    @GET(Constants.PATH_GEO)
    suspend fun getGeoDataFor(@Query("q") q: String, @Query("limit") limit: Int = 5, @Query("appid") appid: String = Constants.API_KEY): GeoResponse
}
