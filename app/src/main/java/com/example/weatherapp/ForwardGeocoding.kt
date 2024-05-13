package com.example.weatherapp

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("api_key") api_key: String
    ): GeocodeResponse
}
data class GeocodeResponse(
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("licence") val licence: String,
    @SerializedName("osm_type") val osmType: String,
    @SerializedName("osm_id") val osmId: Long,
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("address") val address: Address,
    @SerializedName("boundingbox") val boundingbox: List<String>
)

data class Address(
    @SerializedName("building") val building: String,
    @SerializedName("house_number") val houseNumber: String,
    @SerializedName("road") val road: String,
    @SerializedName("city") val city: String,
    @SerializedName("county") val county: String,
    @SerializedName("state") val state: String,
    @SerializedName("ISO3166-2-lvl4") val isoCode: String,
    @SerializedName("postcode") val postcode: String,
    @SerializedName("country") val country: String,
    @SerializedName("country_code") val countryCode: String
)
