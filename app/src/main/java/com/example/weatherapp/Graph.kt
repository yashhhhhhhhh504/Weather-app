package com.example.weatherapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine

@Composable
fun DisplayGraph(viewModel: PastViewModel, pastMonthlyApi: PastMonthlyApi, text: String, lat: Double, long: Double, isCelsius: Boolean, conversionFactor: Double){
    var weatherTest by remember { mutableStateOf<retrieveData?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    if (weatherTest == null && errorMessage == null){
        CircularProgressIndicator()
    } else if (errorMessage != null) {
        Text(text = errorMessage!!)
    } else {
        for (i in 0 until weatherTest!!.daily.temperature_2m_max.size) {
            viewModel.insert(
                PastData(
                    weatherTest!!.daily.time[i],
                    weatherTest!!.daily.temperature_2m_min[i],
                    weatherTest!!.daily.temperature_2m_max[i]
                )
            )
        }
        YCharts(viewModel, isCelsius, conversionFactor)
    }
    LaunchedEffect(text) {
        try {
            val response = pastMonthlyApi.getPastData(
                lat,
                long,
                "temperature_2m_max,temperature_2m_min",
                30
            )
            weatherTest = response
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}

@Composable
fun YCharts(viewModel: PastViewModel, isCelsius: Boolean, conversionFactor: Double){
    val pastData = viewModel.allPastData.observeAsState(emptyList())
    if (pastData.value.isEmpty()) {
        return
    }
    val pointsDataX = pastData.value.mapIndexed { index, pastData ->
        if (isCelsius) {
            Point(index.toFloat(), (pastData.minTemp.toFloat() * conversionFactor + 32).toFloat())
        } else{
            Point(index.toFloat(), pastData.minTemp.toFloat())
        }
    }
    val pointsDataY = pastData.value.mapIndexed { index, pastData ->
        if (isCelsius) {
            Point(index.toFloat(), (pastData.maxTemp.toFloat() * conversionFactor + 32).toFloat())
        } else{
            Point(index.toFloat(), pastData.maxTemp.toFloat())
        }
    }
    val steps = 5
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color(0xFF070F2B))
        .steps(pointsDataX.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(Color(0xFF9290C3))
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color(0xFF070F2B))
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .labelData { i -> i.toString()
        }.build()
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsDataX,
                    LineStyle(
                        color = Color(0xFF7AB1CF),
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = Color(0xFF7AB1CF)
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF7AB1CF),
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                ),
                Line(
                    dataPoints = pointsDataY,
                    LineStyle(
                        color = Color(0xFF9271C7),
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = Color(0xFF9271C7)
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outline, alpha = 0.5f),
        backgroundColor = Color(0xFF070F2B)
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}
