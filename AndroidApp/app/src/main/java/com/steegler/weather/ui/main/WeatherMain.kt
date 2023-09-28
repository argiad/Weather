package com.steegler.weather.ui.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.steegler.weather.data.remote.Main

@Composable
fun WeatherMain(main: Main) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))

    ) {
        Row(
            Modifier
                .height(34.dp)
                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Temperature", modifier = Modifier.weight(1f))
            Text(text = "${main.temp}", modifier = Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                .height(34.dp)
        ) {
            Text(text = "Feels like", modifier = Modifier.weight(1f))
            Text(text = "${main.feelsLike}", modifier = Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                .height(34.dp)
        ) {
            Text(text = "Humidity", modifier = Modifier.weight(1f))
            Text(text = "${main.humidity}", modifier = Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                .height(34.dp)
        ) {
            Text(text = "Pressure", modifier = Modifier.weight(1f))
            Text(text = "${main.pressure}", modifier = Modifier.weight(1f))
        }
    }
}