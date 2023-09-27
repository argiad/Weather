package com.steegler.weather.domain.usecase

import android.util.Log
import com.steegler.weather.Resource
import com.steegler.weather.data.remote.ErrorResponse
import com.steegler.weather.data.remote.GeoResponse
import com.steegler.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetGeo @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(cityName: String): Flow<Resource<GeoResponse>> = flow {
        Log.e("GetGeo", "Flow for $cityName")
        try {
            emit(Resource.Loading())
            val resource = repository.getGeoFor(cityName)
            emit(resource)
        } catch (e: HttpException) {
            emit(Resource.Error(ErrorResponse("${e.code()}", e.message())))
        } catch (e: IOException) {
            emit(Resource.Error(ErrorResponse("-1", "Couldn't reach server. Check your internet connection.")))
        }
    }
}