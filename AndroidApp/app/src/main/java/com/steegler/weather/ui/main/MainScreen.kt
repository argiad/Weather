@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.steegler.weather.ui.main

import android.Manifest
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.gson.Gson
import com.steegler.weather.LocationHelper
import com.steegler.weather.Resource
import com.steegler.weather.data.remote.GeoResponseItem
import com.steegler.weather.data.remote.Main
import com.steegler.weather.data.remote.WeatherResponse
import com.steegler.weather.domain.usecase.GetGeo
import com.steegler.weather.domain.usecase.GetGeoReverse
import com.steegler.weather.domain.usecase.GetWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalComposeUiApi::class)
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
    val lastSelectedCity by viewModel.lastSelectedCity.collectAsState()
    val locationCity by viewModel.locationCity.collectAsState()

    val permissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
    val isFocused = remember { mutableStateOf(true) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(Unit) {
        viewModel.restoreFromPref()
    }

    LaunchedEffect(!permissionState.allPermissionsGranted) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        viewModel.defineLocation()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(
            interactionSource = interactionSource,
            indication = null    // this gets rid of the ripple effect
        ) {
            keyboardController?.hide()
            focusManager.clearFocus(true)
        }) {// London

        Column {

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = TextFieldDefaults.MinHeight + 24.dp)
            ) {
                if (!permissionState.allPermissionsGranted)
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                                .height(34.dp)
                        ) {

                            Button(onClick = {
                                println("**************************")
                                permissionState.launchMultiplePermissionRequest()
                            }) {
                                Text(text = "Request permissions")
                            }
                        }
                    }
                if (weather != null)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                                    .height(34.dp)
                            ) {
                                Text(text = "${selectedCity!!.name} , ${selectedCity!!.state}", modifier = Modifier.weight(1f))
                            }
                        }
                        Divider(startIndent = 8.dp, thickness = 8.dp, color = Color.Transparent)
                    }
                if (weather != null)
                    item {
                        WeatherMain(main = weather!!.main)
                    }
            }
        }
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 8.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                TextField(singleLine = true,
                    value = searchText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused.value = it.isFocused },
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Go),
                    onValueChange = {
                        viewModel.searchCity(it)
                    }, trailingIcon = { (if (isSearchingCity) Icons.Default.Refresh else if (selectedCity != null) Icons.Default.Check) })
                LazyColumn {
                    if (isFocused.value) {
                        lastSelectedCity?.let {
                            item {
                                Text(text = "Last selected: ${it.name}, ${it.state}",
                                    Modifier
                                        .fillMaxWidth()
                                        .height(35.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .clickable {
                                            viewModel.requestWeather(it)
                                            focusManager.clearFocus()
                                        }
                                )

                            }
                        }
                        locationCity?.let {
                            item {
                                Text(text = "Current position: ${it.name}, ${it.state}",
                                    Modifier
                                        .fillMaxWidth()
                                        .height(35.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .clickable {
                                            viewModel.requestWeather(it)
                                            focusManager.clearFocus()
                                        }
                                )

                            }
                        }
                        items(cityList) {
                            Text(text = "${it.name}, ${it.state}",
                                Modifier
                                    .fillMaxWidth()
                                    .height(35.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clickable {
                                        viewModel.requestWeather(it)
                                        focusManager.clearFocus()
                                    }
                            )

                        }
                    }
                }
            }
        }
    }

}


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

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getGeo: GetGeo,
    private val getWeather: GetWeather,
    private val getGeoReverse: GetGeoReverse,
    private val preferences: SharedPreferences,
    private val myLocationManager: LocationHelper
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

    private val _lastSelectedCity: MutableStateFlow<GeoResponseItem?> = MutableStateFlow(null)
    val lastSelectedCity = _lastSelectedCity.asStateFlow()

    private val _locationCity: MutableStateFlow<GeoResponseItem?> = MutableStateFlow(null)
    val locationCity = _locationCity.asStateFlow()

    val location = myLocationManager.location
    fun restoreFromPref() {
        preferences.getString("city", null)?.let {
            Gson().fromJson(it, GeoResponseItem::class.java)?.let { item ->
                viewModelScope.launch(Dispatchers.Default) {
                    _lastSelectedCity.emit(item)
                }
            }
        }
    }

    fun defineLocation() {
        myLocationManager.runLocation()
        myLocationManager.location.onEach {
            it?.let {
                getGeoReverse(it).onEach { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _locationCity.emit(resource.data?.firstOrNull())
                        }

                        is Resource.Error -> {
                            _locationCity.emit(null)
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)

    }

    fun requestWeather(city: GeoResponseItem) {

        viewModelScope.launch(Dispatchers.Default) {
            _selectedCity.emit(city)
            _lastSelectedCity.emit(city)
            _cities.emit(emptyList())
            _searchText.emit("${city.name},${city.state}")
            val c = Gson().toJson(city)
            preferences.edit().putString("city", c).apply()
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

        if (fixedText.isEmpty()) {
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
