package com.steegler.weather.data.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GeoResponse : ArrayList<GeoResponseItem>()