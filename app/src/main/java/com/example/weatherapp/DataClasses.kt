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

interface OtherStuffApi {
    @GET("forecast")
    suspend fun getOtherStuff(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String
    ): OtherStuffResponse
}

data class OtherStuffResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_units: Current_Units,
    val current: Current_Data
)

data class Current_Units(
    val time: String,
    val interval: String,
    val relative_humidity_2m: String,
    val precipitation: String,
    val surface_pressure: String,
    val wind_speed_10m: String
)

data class Current_Data(
    val time: String,
    val interval: Int,
    val relative_humidity_2m: Int,
    val precipitation: Double,
    val surface_pressure: Double,
    val wind_speed_10m: Double
)

