package com.steegler.weather.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("cod")
    val cod: String,
    @SerialName("message")
    val message: String
)