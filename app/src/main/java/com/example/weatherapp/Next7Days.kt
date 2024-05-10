package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Next7Days : ComponentActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weeklyApi: WeeklyApi = retrofit.create(WeeklyApi::class.java)
    var text = "text"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Test(weeklyApi, text)
        }

    }
}

@Composable
fun Test(weeklyApi: WeeklyApi, text: String){
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
            List(weatherTest = weatherTest!!)
        }
    }
    LaunchedEffect(text) {
        try {
            val response = weeklyApi.getWeeklyWeather(
                28.7041,
                77.1025,
                "temperature_2m_max,temperature_2m_min"
            )
            weatherTest = response
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}

@Composable
fun List(weatherTest: retrieveData){
    val minTemp = weatherTest.daily.temperature_2m_min
    val maxTemp = weatherTest.daily.temperature_2m_max
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        items(minTemp.size) { index ->
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(width = 400.dp, height = 200.dp)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF070F2B),
                ),
            ) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, index)
                val sdf = SimpleDateFormat("EEEE")
                val dayOfTheWeek: String = sdf.format(calendar.time)
                Row (
                    modifier = Modifier.padding(30.dp)
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
                        text = "${minTemp[index]}",
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
                        text = "${maxTemp[index]}",
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

