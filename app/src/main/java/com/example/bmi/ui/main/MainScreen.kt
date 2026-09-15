package com.example.bmi.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import com.example.bmi.R
import com.example.bmi.ui.input.InputEvent
import com.example.bmi.ui.input.InputScreen
import com.example.bmi.ui.input.InputUiState
import com.example.bmi.ui.result.ResultEvent
import com.example.bmi.ui.result.ResultMode
import com.example.bmi.ui.result.ResultScreen
import com.example.bmi.ui.result.ResultUiState
import com.example.bmi.ui.statistics.StatisticsEvent
import com.example.bmi.ui.statistics.StatisticsScreen
import com.example.bmi.ui.statistics.StatisticsUiState
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    currentPage: Int,
    onPageChanged: (Int) -> Unit,

    inputUiState: InputUiState,
    inputDispatch:(InputEvent)->Unit,
    onUserClick: () -> Unit,

    resultUiState: ResultUiState,
    mode: ResultMode,
    resultDispatch: (ResultEvent) -> Unit,
    onBack: () -> Unit,
    onRecent: () -> Unit,
    onBackgroundClick: () -> Unit,
    onSave: () -> Unit,

    statisticsUiState: StatisticsUiState,
    statisticsDispatch: (StatisticsEvent) -> Unit,
    onUpdate: () -> Unit


) {

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = {
            3
        }
    )

    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->

            when (page) {

                0 -> InputScreen(inputUiState, inputDispatch, onUserClick)

                1 -> ResultScreen(resultUiState, mode, resultDispatch, onBack, onRecent, onBackgroundClick, onSave)

                2 -> StatisticsScreen(statisticsUiState, statisticsDispatch, onUpdate)
            }
        }


        NavigationBar(containerColor = Color.White){
            val selectedColor = Color.Black
            val unselectedColor = colorResource(R.color.icon_gray)

            NavigationBarItem(
                selected = pagerState.currentPage == 0,
                onClick = {
                    onPageChanged(0)
                },
                icon = {
                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            R.drawable.calculator
                        ),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = "Calculator",
                        color = if (pagerState.currentPage == 0) {
                            selectedColor
                        } else {
                            unselectedColor
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                        letterSpacing = (-0.01).sp
                    )
                }
            )

            NavigationBarItem(
                selected = pagerState.currentPage == 1,
                onClick = {
                    onPageChanged(1)
                },
                icon = {
                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            R.drawable.bmi
                        ),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = "BMI",
                        color = if (pagerState.currentPage == 0) {
                            selectedColor
                        } else {
                            unselectedColor
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                        letterSpacing = (-0.01).sp
                    )
                }
            )

            NavigationBarItem(
                selected = pagerState.currentPage == 2,
                onClick = {
                    onPageChanged(2)
                },
                icon = {
                    androidx.compose.foundation.Image(
                        painter = painterResource(
                            R.drawable.statistics
                        ),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = "Statistics",
                        color = if (pagerState.currentPage == 0) {
                            selectedColor
                        } else {
                            unselectedColor
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                        letterSpacing = (-0.01).sp
                    )
                }
            )
        }
    }
}