package com.example.bmi.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bmi.R
import com.example.bmi.ui.ChartType
import com.example.bmi.ui.StatisticsChartView

//@Preview
//@Composable
//fun StatisticsPreview()
//{
//    StatisticsScreen(onUpdateClick = {}, currentInterval = ChartInterval.DAY, onIntervalClick = {}, dailyBmi = emptyList(), dailyWeight = emptyList(), weeklyBmi = emptyList(), weeklyWeight = emptyList(), monthlyBmi = emptyList(), monthlyWeight = emptyList(), timeMarkers = emptyList())
//}


@Composable
fun StatisticsScreen(uiState: StatisticsUiState,
                     onIntent: (StatisticsIntent) -> Unit)
{
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFFEAEAEE))
        .statusBarsPadding()
        .navigationBarsPadding()
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            StatisticsTopBar()

            StatisticsContent(
                uiState = uiState,
                onIntent = onIntent)
        }
    }
}

@Composable
fun StatisticsTopBar()
{
    Text(
        text = stringResource(R.string.statistics),
        color = Color.Black,
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(top = 18.dp, start = 15.dp),
        letterSpacing = (-0.01).em,
    )
}

@Composable
fun StatisticsContent(
    uiState: StatisticsUiState,
    onIntent: (StatisticsIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 23.dp)
            .verticalScroll(rememberScrollState())
    ) {

        StatisticsPeriodSelector(currentInterval = uiState.currentInterval, onIntervalClick = { interval ->
            onIntent(
                StatisticsIntent.IntervalClick(interval)
            )
        })

        BmiTitle(onUpdateClick = {
            onIntent(
                StatisticsIntent.UpdateClick
            )
        })

        BmiChart(currentInterval = uiState.currentInterval,
            dailyBmi = uiState.dailyBmi,
            weeklyBmi = uiState.weeklyBmi,
            timeMarkers = uiState.timeMarkers,
            monthlyBmi = uiState.monthlyBmi)

        WeightTitle(onUpdateClick = {
            onIntent(
                StatisticsIntent.UpdateClick
            )
        })

        WeightChart(currentInterval = uiState.currentInterval,
            dailyWeight = uiState.dailyWeight,
            weeklyWeight = uiState.weeklyWeight,
            timeMarkers = uiState.timeMarkers,
            monthlyWeight = uiState.monthlyWeight)
    }
}

@Composable
fun StatisticsPeriodSelector(
    currentInterval: ChartInterval,
    onIntervalClick: (ChartInterval) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp,
                end = 15.dp,
            )
            .height(40.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x80FFFFFF))
    ) {

        PeriodItem(
            text = stringResource(R.string.day),
            selected = currentInterval == ChartInterval.DAY,
            modifier = Modifier.weight(1f),
            onClick = {
                onIntervalClick(ChartInterval.DAY)
            }
        )

        PeriodItem(
            text = stringResource(R.string.week),
            selected = currentInterval == ChartInterval.WEEK,
            modifier = Modifier.weight(1f),
            onClick = {
                onIntervalClick(ChartInterval.WEEK)
            }
        )

        PeriodItem(
            text = stringResource(R.string.month),
            selected = currentInterval == ChartInterval.MONTH,
            modifier = Modifier.weight(1f),
            onClick = {
                onIntervalClick(ChartInterval.MONTH)
            }
        )
    }
}

@Composable
fun PeriodItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        RoundedCornerShape(18.dp)
                    )
                    .background(
                        Color.White
                    )
            )
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black.copy(alpha = if (selected) 1f else 0.2f),
            letterSpacing = (-0.01).em
        )
    }
}

@Composable
fun BmiTitle(onUpdateClick: () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, top = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.bmi),
            color = Color.Black,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = stringResource(R.string.update),
            color = Color(0xFF3659CF),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(end = 16.5.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },//关闭交互反馈
                    onClick = onUpdateClick
                )
        )

    }
}


@Composable
fun BmiChart(
    currentInterval: ChartInterval,
    dailyBmi: List<ChartPoint>,
    weeklyBmi: List<ChartPoint>,
    monthlyBmi: List<ChartPoint>,
    timeMarkers: List<TimeMarker>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 10.dp
            )
            .height(243.dp)
            .background(
                Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF264CCA),
                    Color(0xFF5077FA)
                )
            ), shape = RoundedCornerShape(12.dp)),

        factory = { context ->
            StatisticsChartView(context, ChartType.BMI)
        },

        update = { chartView ->
            chartView.timeAxis.setMarkers(timeMarkers)

            chartView.update(
                interval = currentInterval,
                points = when (currentInterval) {
                    ChartInterval.DAY -> dailyBmi
                    ChartInterval.WEEK -> weeklyBmi
                    ChartInterval.MONTH -> monthlyBmi
                }
            )
        }
    )
}
@Composable
fun WeightTitle(onUpdateClick: () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, top = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.weight),
            color = Color.Black,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = stringResource(R.string.update),
            color = Color(0xFF3659CF),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(end = 16.5.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },//关闭交互反馈
                    onClick = onUpdateClick
                )
        )

    }
}

@Composable
fun WeightChart(
    currentInterval: ChartInterval,
    dailyWeight: List<ChartPoint>,
    weeklyWeight: List<ChartPoint>,
    monthlyWeight: List<ChartPoint>,
    timeMarkers: List<TimeMarker>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 10.dp
            )
            .height(243.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF931E),
                        Color(0xFFFF8537)
                    )
                ), shape = RoundedCornerShape(12.dp)),

        factory = { context ->
            StatisticsChartView(context, ChartType.WEIGHT)
        },
        update = { chartView ->
            chartView.timeAxis.setMarkers(timeMarkers)

            chartView.update(
                interval = currentInterval,
                points = when (currentInterval) {
                    ChartInterval.DAY -> dailyWeight
                    ChartInterval.WEEK -> weeklyWeight
                    ChartInterval.MONTH -> monthlyWeight
                }
            )
        }
    )
}
