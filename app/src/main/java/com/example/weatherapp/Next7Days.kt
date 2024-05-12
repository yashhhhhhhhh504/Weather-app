package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
class Next7Days : ComponentActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weeklyApi: WeeklyApi = retrofit.create(WeeklyApi::class.java)
    var text = "text"
    var lat = 0.0
    var long = 0.0
    var isCelsius = true
    var conversionFactor = 1.8
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lat = intent.getDoubleExtra("lat", 0.0)
        long = intent.getDoubleExtra("long", 0.0)
        isCelsius = intent.getBooleanExtra("isCelsius", true)
        conversionFactor = intent.getDoubleExtra("conversionFactor", 1.8)
        setContent{
            Test(weeklyApi, text, lat, long, isCelsius, conversionFactor)
        }
    }
}
@Composable
fun Test(weeklyApi: WeeklyApi, text: String, lat: Double, long: Double, isCelsius: Boolean, conversionFactor: Double){
    var weatherTest by remember { mutableStateOf<retrieveData?>(null)}
    var errorMessage by remember { mutableStateOf<String?>(null)}
    if (weatherTest == null) {
        CircularProgressIndicator()
    } else if (errorMessage != null) {
        Text(text = errorMessage!!)
    } else {
        Surface(
            color = Color(0xFF9290C3),
            modifier = Modifier.fillMaxSize()
        )  {
            List(weatherTest = weatherTest!!, isCelsius = isCelsius, conversionFactor = conversionFactor)
        }
    }
    LaunchedEffect(text) {
        try {
            val response = weeklyApi.getWeeklyWeather(
                lat,
                long,
                "temperature_2m_max,temperature_2m_min"
            )
            weatherTest = response
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}

@Composable
fun List(weatherTest: retrieveData, isCelsius: Boolean, conversionFactor: Double){
    val minTemp = weatherTest.daily.temperature_2m_min
    val maxTemp = weatherTest.daily.temperature_2m_max
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        items(minTemp.size) { index ->
            ElevatedCard(
                modifier = Modifier
                    .size(width = 400.dp, height = 200.dp)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xB22B2155),
                ),
            ) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, index)
                val sdf = SimpleDateFormat("EEEE")
                val dayOfTheWeek: String = sdf.format(calendar.time)
                var min = minTemp[index]
                var max = maxTemp[index]
                if (!isCelsius) {
                    min = (minTemp[index].toDouble() * conversionFactor + 32)
                    max = (maxTemp[index].toDouble() * conversionFactor + 32)
                }
                min = BigDecimal(min).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                max = BigDecimal(max).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                Row (
                    modifier = Modifier.padding(30.dp),
                ) {
                    Text(
                        text = "Min",
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = dayOfTheWeek,
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Max",
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.End
                    )
                }
                Row (
                    modifier = Modifier.padding(30.dp)
                ) {
                    Text(
                        text = "$min",
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$max",
                        color = Color(0xFFD3D3D3),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

