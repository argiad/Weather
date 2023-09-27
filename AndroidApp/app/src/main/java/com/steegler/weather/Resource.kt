package com.steegler.weather

import com.steegler.weather.data.remote.ErrorResponse

sealed class Resource<T>(val data: T? = null, val message: ErrorResponse? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: ErrorResponse, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)

}