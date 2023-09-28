@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.steegler.weather.ui.main

import android.Manifest
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
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


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
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsState() // ToDo: !!!!!
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



