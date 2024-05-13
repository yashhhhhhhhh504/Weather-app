package com.example.weatherapp

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonParser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SavedCities : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavedCitiesScreen()
        }
    }
    private fun sendBack(location: String, latitude: Double, longitude: Double) {
        val intent = Intent()
        intent.putExtra("location", location)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        setResult(RESULT_OK, intent)
        finish()
    }
    @Composable
    fun SavedCitiesScreen() {
        val context = LocalContext.current
        val databaseHelper = DatabaseHelper(context)
        val showDialog = remember { mutableStateOf(false) }
        val locations = remember { mutableStateOf(databaseHelper.getAllLocations().toMutableList()) }

        if (showDialog.value) {
            AddCityDialog(
                onDismissRequest = { showDialog.value = false },
                onCityAdded = { newCity, latitude, longitude ->
                    databaseHelper.addLocation(newCity, latitude, longitude)
                    locations.value = databaseHelper.getAllLocations().toMutableList()
                    showDialog.value = false
                }
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        onClick = { showDialog.value = true }
                    ) {
                        Text(text = "Add City")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Saved Cities",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                SavedCitiesList(locations = locations)
            }
        }
    }
    @Composable
    fun AddCityDialog(
        onDismissRequest: () -> Unit,
        onCityAdded: (String, Double, Double) -> Unit
    ) {
        val cityName = remember { mutableStateOf("") }
        val geocodingApiKey = "6640c0cad1150480423385ycd27079a"

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Add City") },
            text = {
                Column {
                    TextField(
                        value = cityName.value,
                        onValueChange = { newValue ->
                            if (newValue.isNotBlank()) {
                                cityName.value = newValue
                            }
                        },
                        placeholder = { Text(text = "Enter city name") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (cityName.value.isNotBlank()) {
                            getLatLongFromApi(cityName.value, geocodingApiKey) { latitude, longitude ->
                                onCityAdded(cityName.value, latitude, longitude)
                                cityName.value = ""
                            }
                        }
                    }
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun getLatLongFromApi(
        cityName: String,
        apiKey: String,
        callback: (Double, Double) -> Unit
    ) {
        val apiUrl = "https://geocode.maps.co/search?q=${cityName.replace(" ", "%20")}&api_key=$apiKey"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val jsonString = inputStream.bufferedReader().use { it.readText() }

                    val jsonArray = JsonParser.parseString(jsonString).asJsonArray
                    if (jsonArray.size() > 0) {
                        val firstResult = jsonArray.get(0).asJsonObject
                        val boundingbox = firstResult.getAsJsonArray("boundingbox")
                        val latitude = (boundingbox.get(0).asDouble + boundingbox.get(2).asDouble) / 2
                        val longitude = (boundingbox.get(1).asDouble + boundingbox.get(3).asDouble) / 2

                        withContext(Dispatchers.Main) {
                            callback(latitude, longitude)
                        }
                    } else {
                        Log.e("Geocoding", "No results found for: $cityName")
                    }
                } else {
                    Log.e("Geocoding", "API request failed with code ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("Geocoding", "Exception occurred: ${e.message}")
            }
        }
    }
    @Composable
    fun SavedCitiesList(locations: MutableState<MutableList<LocationData>>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = locations.value, key = { it.id.hashCode() }) { location ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 24.dp)
                        .clickable {
                            sendBack(location.location, location.latitude, location.longitude)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "${location.location}",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

