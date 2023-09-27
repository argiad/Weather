package com.steegler.weather.domain.usecase

import com.steegler.weather.Resource
import com.steegler.weather.data.remote.ErrorResponse
import com.steegler.weather.data.remote.WeatherResponse
import com.steegler.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetWeather @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<Resource<WeatherResponse>> = flow {
        try {
            emit(Resource.Loading())
            val resource = repository.getWeatherFor(latitude, longitude)
            emit(resource)
        } catch (e: HttpException) {
            emit(Resource.Error(ErrorResponse("${e.code()}", e.message())))
        } catch (e: IOException) {
            emit(Resource.Error(ErrorResponse("-1", "Couldn't reach server. Check your internet connection.")))
        }
    }
}