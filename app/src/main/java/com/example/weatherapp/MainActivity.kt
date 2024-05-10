package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.core.content.ContextCompat.startActivity
import com.example.weatherapp.ui.theme.WeatherAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private var location by mutableStateOf("New Delhi")
    private var condition by mutableStateOf("Can't Say Yet")
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)
    fun navigateToNext7Days() {
        val intent = Intent(this, Next7Days::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                Surface(
                    color = Color(0xFF070F2B),
                    modifier = Modifier.fillMaxSize()
                ) {
                    WeatherScreen(location, condition, weatherApi, this@MainActivity)
                }
            }
        }
    }

}
@Composable
fun WeatherScreen(location: String, condition: String, weatherApi: WeatherApi, mainActivity: MainActivity) {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var temp: String
    var hour: List<Double> by remember { mutableStateOf(listOf()) }
    Column(
        modifier = Modifier.padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = location,
            modifier = Modifier.padding(start = 8.dp),
            color = Color(0xFFD3D3D3),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        Text(
            text = condition,
            modifier = Modifier.padding(15.dp),
            color = Color(0xFF9290C3),
        )
        if (weatherData == null) {
            CircularProgressIndicator()
        } else {
            val currentData = weatherData!!.current
            if (currentData.time.isNotEmpty()) {
                temp = currentData.temperature_2m.toString()
                Text(
                    modifier = Modifier.padding(30.dp),
                    text = "  $temp°",
                    color = Color(0xFFD3D3D3),
                    fontSize = 80.sp,
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
                Spacer(modifier = Modifier.padding(5.dp))
                HourlyData(weatherData = weatherData!!)
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
            } else {
                Text(text = "No hourly temperature data available")
            }
        }
    }
    LaunchedEffect(location) {
        try {
            val response = weatherApi.getWeather(
                latitude = 28.6519,
                longitude = 77.2315,
                current = "temperature_2m",
                hourly = "temperature_2m",
                daily = "temperature_2m_max,temperature_2m_min",
                timezone = "GMT",
                forecastDays = 1
            )
            weatherData = response
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        }
    }
}

@Composable
fun HourlyData(weatherData: WeatherResponse) {
    val hour = weatherData.hourly.temperature_2m
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hour.size) { index ->
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
                        text = "${hour[index]}°",
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