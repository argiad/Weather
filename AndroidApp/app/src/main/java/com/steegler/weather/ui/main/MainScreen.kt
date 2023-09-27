@file:OptIn(ExperimentalMaterial3Api::class)

package com.steegler.weather.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.steegler.weather.Resource
import com.steegler.weather.data.remote.GeoResponseItem
import com.steegler.weather.data.remote.Main
import com.steegler.weather.data.remote.WeatherResponse
import com.steegler.weather.domain.usecase.GetGeo
import com.steegler.weather.domain.usecase.GetWeather
import com.steegler.weather.domain.usecase.GetWeatherFor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val isSearchingCity by viewModel.isSearchingText.collectAsState()
    val cityList by viewModel.cities.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        ) {
            Column {
                TextField(value = searchText, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    viewModel.searchCity(it)
                }, trailingIcon = { (if (isSearchingCity) Icons.Default.Refresh else if (selectedCity != null) Icons.Default.Check) })
                LazyColumn {
                    items(cityList) {
                        Text(text = "${it.name}, ${it.state}",
                            Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .align(Alignment.CenterHorizontally)
                                .clickable {
                                    viewModel.requestWeather(it)
                                }
                        )

                    }
                }
            }
        }
        if (weather != null)
            Row {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    item {
                        WeatherMain(main = weather!!.main)
                    }
                }
            }
    }

}


@Composable
fun WeatherMain(main: Main) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .shadow(elevation = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                .height(34.dp)
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

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getGeo: GetGeo,
    private val getWeather: GetWeather,
    private val getWeatherFor: GetWeatherFor
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
    fun requestWeather(city: GeoResponseItem) {

        viewModelScope.launch(Dispatchers.Default) {
            _selectedCity.emit(city)
            _cities.emit(emptyList())
            _searchText.emit("${city.name},${city.state}")
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

        if (fixedText.isEmpty()){
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
