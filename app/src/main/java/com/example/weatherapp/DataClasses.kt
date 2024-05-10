package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("hourly") hourly: String,
        @Query("daily") daily: String,
        @Query("timezone") timezone: String,
        @Query("forecast_days") forecastDays: Int
    ): WeatherResponse
}
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_units: CurrentUnits,
    val current: CurrentData,
    val hourly_units: Units,
    val hourly: HourlyData,
    val daily_units: Units,
    val daily: DailyData
)

data class CurrentUnits(
    val time: String,
    val interval: String,
    val temperature_2m: String
)

data class CurrentData(
    val time: String,
    val interval: String,
    val temperature_2m: Double
)

data class Units(
    val time: String,
    val temperature_2m: String
)

data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>
)

data class DailyData(
    val time: List<String>,
    val maxTemperature: List<Double>,
    val minTemperature: List<Double>
)