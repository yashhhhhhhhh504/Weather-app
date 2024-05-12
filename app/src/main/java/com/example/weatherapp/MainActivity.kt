package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
class MainActivity : ComponentActivity(), LocationListener {
    private var isCelsius by mutableStateOf(true)
    private var conversionFactor by mutableDoubleStateOf(1.0)
    private var isMM by mutableStateOf(true)
    private var conversionFactor2 by mutableDoubleStateOf(1.0)
    private var isKMH by mutableStateOf(true)
    private var conversionFactor3 by mutableDoubleStateOf(1.0)
    private var lat by mutableDoubleStateOf(0.0)
    private var long by mutableDoubleStateOf(0.0)
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)
    private val pastMonthlyApi: PastMonthlyApi = retrofit.create(PastMonthlyApi::class.java)
    fun navigateToNext7Days() {
        val intent = Intent(this, Next7Days::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("long", long)
        intent.putExtra("isCelsius", isCelsius)
        intent.putExtra("conversionFactor", conversionFactor)
        startActivity(intent)
    }
    private val viewModel: PastViewModel by viewModels {
        PastViewModelFactory((application as PastApplication).repository)
    }
    private val unique = "lmao"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
        val retrofit2 = Retrofit.Builder()
            .baseUrl("https://geocode.maps.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit2.create(ApiService::class.java)
        setContent {
            WeatherAppTheme {
                Surface(
                    color = Color(0xFF070F2B),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box{
                        TextButton(
                            onClick = {
                                openSecondActivity()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(text = "⚙\uFE0F", color = Color(0xFF9290C3), fontSize = 20.sp)
                        }
                    }
                    WeatherScreen(
                        weatherApi,
                        this@MainActivity,
                        viewModel,
                        pastMonthlyApi,
                        unique,
                        lat,
                        long,
                        isCelsius,
                        conversionFactor,
                        isMM,
                        conversionFactor2,
                        isKMH,
                        conversionFactor3,
                        service
                    )
                }
            }
        }
    }
    private fun getCurrentLocation() {
        val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        fun checkPermission(): Boolean {
            return (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)
        }

        fun requestPermission() {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_ACCESS_LOCATION
            )
        }

        fun isLocationEnabled(): Boolean {
            val locationManager: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
        if (checkPermission()) {
            if (isLocationEnabled()) {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastKnownLocation != null) {
                    onLocationChanged(lastKnownLocation)
                } else {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        this
                    )
                }
            } else {
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }
    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        long = location.longitude
    }
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            conversionFactor = data?.getDoubleExtra("conversionFactor", 1.0) ?: 1.0
            conversionFactor2 = data?.getDoubleExtra("conversionFactor2", 1.0) ?: 1.0
            conversionFactor3 = data?.getDoubleExtra("conversionFactor3", 1.0) ?: 1.0
            isCelsius = !isCelsius
            isMM = !isMM
            isKMH = !isKMH
        }
    }

    private fun openSecondActivity() {
        val intent = Intent(this@MainActivity, Settings::class.java)
        intent.putExtra("isCelsius", isCelsius)
        intent.putExtra("isMM", isMM)
        intent.putExtra("isKMH", isKMH)
        resultLauncher.launch(intent)
    }
}
@Composable
fun WeatherScreen(
    weatherApi: WeatherApi,
    mainActivity: MainActivity,
    viewModel: PastViewModel,
    pastMonthlyApi: PastMonthlyApi,
    unique: String,
    lat: Double,
    long: Double,
    isCelsius: Boolean,
    conversionFactor: Double,
    isMM: Boolean,
    conversionFactor2: Double,
    isKMH: Boolean,
    conversionFactor3: Double,
    service: ApiService
) {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var data by remember { mutableStateOf<GeocodeResponse?>(null) }
    val location by remember { mutableStateOf("Loading...") }
    var eM by remember { mutableStateOf<String?>(null) }
    var temp: String
    var unit: String
    LazyColumn(
        modifier = Modifier.padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            val cd = data?.address?.city
            Text(
                text = cd.toString(),
                modifier = Modifier.padding(start = 8.dp),
                color = Color(0xFFD3D3D3),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
        }
        if (weatherData == null && errorMessage == null){
            item{
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            item{
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFD3D3D3),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        } else {
            val currentData = weatherData!!.current
            if (currentData.time.isNotEmpty()) {
                temp = currentData.temperature_2m.toString()
                if (!isCelsius) {
                    temp = (temp.toDouble() * conversionFactor + 32).toString()
                }
                temp = BigDecimal(temp.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
                unit = if (isCelsius) "°C" else "°F"
                item {
                    Text(
                        modifier = Modifier.padding(30.dp),
                        text = " $temp$unit",
                        color = Color(0xFFD3D3D3),
                        fontSize = 60.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Today",
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(5.dp))
                    HourlyData(weatherData = weatherData!!, isCelsius, conversionFactor)
                    TextButton(
                        onClick = {
                            mainActivity.navigateToNext7Days()
                        },
                        modifier = Modifier.padding(start = 200.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFD3D3D3)
                        )
                    ) {
                        Text(text = "Next 7 Days >")
                    }
                    DisplayGraph(viewModel, pastMonthlyApi, unique, lat, long, isCelsius, conversionFactor)
                }
                item {
                    Spacer(modifier = Modifier.padding(10.dp))
                    FeatureGrids(lat, long, isMM, conversionFactor2, isKMH, conversionFactor3)
                }
            } else {
                item{
                    Text(text = "No hourly temperature data available")
                }
            }
        }
    }
    LaunchedEffect(location) {
        try {
            val response = weatherApi.getWeather(
                latitude = lat,
                longitude = long,
                current = "temperature_2m",
                hourly = "temperature_2m",
                daily = "temperature_2m_max,temperature_2m_min",
                timezone = "GMT",
                forecastDays = 1
            )
            weatherData = response
        } catch (e: IOException) {
            errorMessage = "Error: No internet connection"
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        }
    }
    LaunchedEffect("2") {
        try {
            val response = service.reverseGeocode(
                latitude = lat,
                longitude = long,
                api_key = "664078c99762d208919191ansd70603"
            )
            data = response
        } catch (e: IOException) {
            eM = "Error: No internet connection"
        } catch (e: Exception) {
            eM = "Error: ${e.message}"
        }
    }
}
@Composable
fun HourlyData(weatherData: WeatherResponse, isCelsius: Boolean, conversionFactor: Double) {
    val hour = weatherData.hourly.temperature_2m
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hour.size) { index ->
            var temp = hour[index].toString()
            if (!isCelsius) {
                temp = (temp.toDouble() * conversionFactor + 32).toString()
            }
            temp = BigDecimal(temp.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
            Card(
                modifier = Modifier
                    .shadow(8.dp)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9290C3),
                ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    val time = weatherData.hourly.time[index].substringAfter('T').replace("T", "")
                    Text(
                        text = time,
                        color = Color(0xFF070F2B),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = "$temp°",
                        color = Color(0xFF070F2B),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}