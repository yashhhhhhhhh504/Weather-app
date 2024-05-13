package com.example.weatherapp



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.ui.graphics.Paint

import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class GraphActivity : ComponentActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val viewModel: PastViewModel by viewModels {
        PastViewModelFactory((application as PastApplication).repository)
    }
    val weatherService: WeeklyApi = retrofit.create(WeeklyApi::class.java)

    private val pastMonthlyApi: PastMonthlyApi = retrofit.create(PastMonthlyApi::class.java)
    val weeklyApi: WeeklyApi = retrofit.create(WeeklyApi::class.java)
    var lat = 0.0
    var long = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val unique = intent.getStringExtra("unique")
        lat = intent.getDoubleExtra("lat", 0.0)
        long = intent.getDoubleExtra("long", 0.0)
        setContent {
            WeatherAppTheme {
                Surface(
                    color = Color(0xFF070F2B),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (unique != null) {
                        GraphScreen(viewModel,pastMonthlyApi,unique,weeklyApi,lat,long,"text",weatherService)
                    }
                }
            }
        }
    }
}
@Composable
fun Datagetter(weeklyApi: WeeklyApi, text: String, lat: Double, long: Double){
    var weatherTest by remember { mutableStateOf<retrieveData?>(null)}
    var errorMessage by remember { mutableStateOf<String?>(null)}
    if (weatherTest == null) {
        CircularProgressIndicator()
    } else if (errorMessage != null) {
        Text(text = errorMessage!!)
    } else {
        Surface(
            color = Color(0xFF9290C3),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )  {
            val minTemp = weatherTest!!.daily.temperature_2m_min
            val maxTemp = weatherTest!!.daily.temperature_2m_max
            graphmaker(minTemp,maxTemp)
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
fun GraphScreen(viewModel: PastViewModel,pastMonthlyApi: PastMonthlyApi,unique: String,weeklyApi: WeeklyApi,lat: Double,long: Double,text: String,weatherService: WeeklyApi) {

    LazyColumn(
        modifier = Modifier.padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        item{
            Text(
                text = "Past 30 Days",
                color = Color(0xFFD3D3D3),
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )
            DisplayGraph(viewModel = viewModel, pastMonthlyApi = pastMonthlyApi, text= unique,lat,long, isCelsius = true, conversionFactor = 1.8)
        }
        item{
            Text(
                text = "Next 7 Days",
                color = Color(0xFFD3D3D3),
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )
            Datagetter(weeklyApi = weeklyApi , text = text, lat = lat, long = long)
            Spacer(modifier = Modifier.height(50.dp))
        }
        item{
            Row(
                modifier = Modifier.padding(15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ){

                comparison(pastMonthlyApi = pastMonthlyApi, text = text)
            }
        }
    }




}

@Composable
fun graphmaker(minTemp: List<Double>,maxTemp: List<Double>){

    val pointsDataX = minTemp.mapIndexed { index, temp ->
        Point(index.toFloat(), temp.toFloat())
    }
    val pointsDataY = maxTemp.mapIndexed { index, temp ->
        Point(index.toFloat(), temp.toFloat())
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
@Composable
fun TemperatureComparisonBarGraph(
    avgmaxtemp: Double,
    avgmintemp: Double,
    currmaxtemp: Double,
    currmintemp: Double
) {
    val maxValue = maxOf(avgmaxtemp, avgmintemp, currmaxtemp, currmintemp)
    val barWidth = 100f
    val barSpacing = 30f
    val maxBarHeight = 200f
    val labelSpacing = 8.dp
    val barColors = listOf(Color(0xFFADD8E6), Color(0xFF000080), Color(0xFFE6E6FA), Color(0xFF800080))
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly

    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxBarHeight.dp)
        ) {
            drawIntoCanvas {
                val paint = Paint().asFrameworkPaint().apply {
                    color = Color(0xFFD3D3D3).toArgb()
                    textSize = 16.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw bars
                val barValues = listOf(avgmaxtemp, currmaxtemp, avgmintemp, currmintemp)
                val barGroups = listOf(
                    listOf(0, 1), // avgmaxtemp and currmaxtemp
                    listOf(2, 3)  // avgmintemp and currmintemp
                )

                for ((groupIndex, group) in barGroups.withIndex()) {
                    val groupStartX = groupIndex * (3 * barWidth + 2 * barSpacing) // Updated
                    for ((index, valueIndex) in group.withIndex()) {
                        val value = barValues[valueIndex]
                        val barHeight = (value / maxValue) * canvasHeight
                        val startX = groupStartX + index * (barWidth + barSpacing)
                        val startY = canvasHeight - barHeight
                        drawRect(
                            color = barColors[valueIndex],
                            topLeft = Offset(startX, startY.toFloat()),
                            size = Size(barWidth, barHeight.toFloat())
                        )

                        // Draw labels
                        val labelText = when (valueIndex) {
                            0 -> "Avg Max"
                            1 -> "Curr Max"
                            2 -> "Avg Min"
                            else -> "Curr Min"
                        }
                        val textX = startX + barWidth / 2
                        val textY = startY - labelSpacing.toPx()
                        drawContext.canvas.nativeCanvas.drawText(
                            labelText,
                            textX,
                            textY.toFloat(),
                            paint
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add legend
        Row(modifier = Modifier.fillMaxWidth()) {
            for ((index, color) in barColors.withIndex().take(2)) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (index) {
                        0 -> "Avg Max"
                        1 -> "Curr Max"
                        else -> ""
                    },
                    color = Color(0xFFD3D3D3),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            for ((index, color) in barColors.withIndex().drop(2)) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (index) {
                        2 -> "Avg Min"
                        3 -> "Curr Min"
                        else -> ""
                    },
                    color = Color(0xFFD3D3D3),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}


@Composable
fun comparison(pastMonthlyApi: PastMonthlyApi, text: String){
    var weatherTest by remember { mutableStateOf<retrieveData?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var avgmaxtemp by remember { mutableStateOf<Double?>(0.0) }
    var avgmintemp by remember { mutableStateOf<Double?>(0.0) }
    var maxtemplist = remember { mutableStateListOf<Double>() }
    var mintemplist = remember { mutableStateListOf<Double>() }
    var currmaxtemp by remember { mutableStateOf<Double?>(0.0) }
    var currmintemp by remember { mutableStateOf<Double?>(0.0) }

    if (weatherTest == null) {
        CircularProgressIndicator()
    } else if (errorMessage != null) {
        Text(text = errorMessage!!)
    } else {

        println(weatherTest!!.daily)

    }
    avgmaxtemp = maxtemplist.average()
    avgmintemp = mintemplist.average()
    LaunchedEffect(text) {
        try {
            val response = pastMonthlyApi.getPastData(
                28.7041,
                77.1025,
                "temperature_2m_max,temperature_2m_min",
                30
            )
            weatherTest = response
        } catch (e: Exception) {
            errorMessage = e.message
        }
        for (i in 0 until 30) {


            maxtemplist.add(weatherTest!!.daily.temperature_2m_max[i])
            mintemplist.add(weatherTest!!.daily.temperature_2m_min[i])

        }
        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        currmaxtemp = weatherTest!!.daily.temperature_2m_max[weatherTest!!.daily.time.indexOf(currentDate)]
        currmintemp = weatherTest!!.daily.temperature_2m_min[weatherTest!!.daily.time.indexOf(currentDate)]
    }

    currmaxtemp?.let { currmintemp?.let { it1 -> TemperatureComparisonBarGraph(avgmaxtemp = avgmaxtemp!!, avgmintemp = avgmintemp!!, currmaxtemp = it, currmintemp = it1) } }}
