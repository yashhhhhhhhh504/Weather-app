package com.example.weatherapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun FeatureGrids(lat: Double,
                 long: Double,
                 isMM: Boolean,
                 converterFactor2: Double,
                 isKMH: Boolean,
                 converterFactor3: Double
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val otherStuffApi = retrofit.create(OtherStuffApi::class.java)
    var featureData by remember { mutableStateOf<OtherStuffResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val lol = ""
    if (featureData == null) {
        CircularProgressIndicator()
    } else {
        val humidity = featureData!!.current.relative_humidity_2m
        var precipitation = featureData!!.current.precipitation
        if (!isMM) {
            precipitation *= converterFactor2
            precipitation = BigDecimal(precipitation).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
        val unit = if (isMM) "mm" else "in"
        val surfacePressure = featureData!!.current.surface_pressure
        var windSpeed = featureData!!.current.wind_speed_10m
        if (!isKMH) {
            windSpeed *= converterFactor3
            windSpeed = BigDecimal(windSpeed).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        }
        val wunit = if (isKMH) "km/h" else "mph"
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .shadow(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF9290C3),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Humidity",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$humidity %",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .shadow(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF9290C3),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Precipitation",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$precipitation $unit",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .shadow(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF9290C3),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Surface Pressure",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$surfacePressure hpa",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .shadow(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF9290C3),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Wind Speed",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$windSpeed $wunit",
                            fontSize = 17.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF070F2B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(lol) {
        try {
            val response = otherStuffApi.getOtherStuff(
                latitude = lat,
                longitude = long,
                current = "relative_humidity_2m,precipitation,surface_pressure,wind_speed_10m"
            )
            featureData = response
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        }
    }
}