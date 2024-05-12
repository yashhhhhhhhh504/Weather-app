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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Settings : ComponentActivity() {
    private var isCelsius = true
    private var isMM = true
    private var isKMH = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            isCelsius = extras.getBoolean("isCelsius", true)
            isMM = extras.getBoolean("isMM", true)
            isKMH = extras.getBoolean("isKMH", true)
        }
        setContent {
            SecondActivityUI()
        }
    }

    @Composable
    fun SecondActivityUI() {
        Surface(
            color = Color(0xFF070F2B),
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Temperature Unit", color = Color(0xFFD3D3D3))
                Button(
                    onClick = {
                        changeTempUnit()
                    }
                ) {
                    Text("Change to ${if (isCelsius) "Fahrenheit" else "Celsius"}")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Text("Precipitation Unit", color = Color(0xFFD3D3D3))
                Button(onClick = { changePrecipUnit() }) {
                    Text("Change to ${if (isMM) "inches" else "millimeters"}")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "Wind Speed Unit", color = Color(0xFFD3D3D3))
                Button(onClick = { changeWindUnit() }) {
                    Text("Change to ${if (isKMH) "mph" else "km/h"}")
                }
            }
        }
    }

    private fun changeTempUnit() {
        val intent = Intent()
        val conversionFactor = if (isCelsius) 1.8 else 5.0 / 9.0
        intent.putExtra("conversionFactor", conversionFactor)
        setResult(RESULT_OK, intent)
        finish()
    }
    private fun changePrecipUnit() {
        val intent = Intent()
        val conversionFactor = if (isMM) 0.039 else 25.4
        intent.putExtra("conversionFactor2", conversionFactor)
        setResult(RESULT_OK, intent)
        finish()
    }
    private fun changeWindUnit() {
        val intent = Intent()
        val conversionFactor = if (isKMH) 0.621371 else 1.60934
        intent.putExtra("conversionFactor3", conversionFactor)
        setResult(RESULT_OK, intent)
        finish()
    }
}
