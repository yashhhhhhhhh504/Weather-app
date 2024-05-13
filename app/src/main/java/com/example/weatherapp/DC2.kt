package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeeklyApi {
    @GET("forecast")
    suspend fun getWeeklyWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String
    ): retrieveData
}

interface PastMonthlyApi {
    @GET("forecast")
    suspend fun getPastData(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String,
        @Query("past_days") past_days: Int,
    ): retrieveData
}

data class retrieveData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val daily_units: U2,
    val daily: DailyD2
)

data class U2(
    val time: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String
)

data class DailyD2(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)