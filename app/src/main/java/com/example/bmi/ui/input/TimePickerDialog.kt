package com.example.bmi.ui.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.bmi.R
import kotlinx.coroutines.launch

private val MontserratExtraBold =
    FontFamily(
        Font(R.font.montserrat_extrabold)
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerBottomSheet(
    timeSlot: Int,

    onDismiss: () -> Unit,

    onDone: (Int) -> Unit
) {

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,

        sheetState = sheetState,

        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),

        containerColor = Color(0xFFEAEAEE),

        dragHandle = null
    ) {

        TimePickerContent(
            timeSlot = timeSlot,

            onCancel = onDismiss,

            onDone = onDone
        )
    }
}


@Composable
private fun TimePickerContent(
    timeSlot: Int,

    onCancel: () -> Unit,

    onDone: (Int) -> Unit
) {

    var selectedTime by remember {
        mutableIntStateOf(timeSlot)
    }

    val times = listOf(
        stringResource(R.string.morning),
        stringResource(R.string.afternoon),
        stringResource(R.string.evening),
        stringResource(R.string.night)
    )

    val timeState =
        rememberLazyListState()

    val scope =
        rememberCoroutineScope()

    /*
     * 初始化到当前选中的时间
     */
    LaunchedEffect(Unit) {

        timeState.scrollToItem(
            selectedTime
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
            .background(Color.White)
    ) {

        Text(
            text = stringResource(R.string.log_weight_time),

            fontSize = 20.sp,

            color = Color.Black,

            fontFamily = MontserratExtraBold,

            letterSpacing = (-0.01).em,

            modifier = Modifier.padding(
                start = 20.dp,
                top = 20.dp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp)
        ) {

            TimePickerColumn(
                items = times,

                selectedIndex = selectedTime,

                state = timeState,

                onItemSelected = { index ->

                    selectedTime = index

                }
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 15.dp
                )
                .height(50.dp)
        ) {

            TimeButton(
                text = stringResource(R.string.cancel),

                backgroundColor = Color(0xFFF1F1F1),

                textColor = Color.Black,

                modifier = Modifier.weight(1f),

                onClick = onCancel
            )

            Spacer(
                modifier = Modifier.width(15.dp)
            )

            TimeButton(
                text = stringResource(R.string.done),

                backgroundColor = Color(0xFF3659CF),

                textColor = Color.White,

                modifier = Modifier.weight(1f),

                onClick = {

                    onDone(selectedTime)

                }
            )
        }
    }
}

@Composable
private fun TimePickerColumn(
    items: List<String>,
    selectedIndex: Int,
    state: LazyListState,
    onItemSelected: (Int) -> Unit
) {

    val itemHeight = 36.dp

    val flingBehavior =
        rememberSnapFlingBehavior(
            lazyListState = state,
            snapPosition = SnapPosition.Center
        )
    val scope =
        rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
    ) {

        val sidePadding =
            (maxHeight - itemHeight) / 2

        LazyColumn(
            state = state,

            flingBehavior = flingBehavior,

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                top = sidePadding,
                bottom = sidePadding
            ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            itemsIndexed(
                items = items,

                key = { index, _ ->
                    index
                }
            ) { index, item ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {

                            scope.launch {
                                state.animateScrollToItem(
                                    index
                                )
                            }
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = item,

                        modifier =
                            Modifier.fillMaxWidth(),

                        textAlign =
                            TextAlign.Center,

                        fontSize = 14.sp,

                        color =
                            Color.Black.copy(
                                alpha =
                                    if (
                                        index ==
                                        selectedIndex
                                    ) {
                                        1f
                                    } else {
                                        0.3f
                                    }
                            ),

                        fontFamily =
                            MontserratExtraBold,

                        letterSpacing =
                            (-0.01).em
                    )
                }
            }
        }

        /*
         * 上 PickLine
         */
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = sidePadding
                )
                .width(80.dp)
                .height(1.dp)
                .background(
                    Color(0x803659CF)
                )
        )

        /*
         * 下 PickLine
         */
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(
                    y = sidePadding + itemHeight
                )
                .width(80.dp)
                .height(1.dp)
                .background(
                    Color(0x803659CF)
                )
        )
    }

    /*
     * 滑动结束：
     *
     * 找真正处于中间的 item
     */
    LaunchedEffect(state) {

        snapshotFlow {
            state.isScrollInProgress
        }.collect { isScrolling ->

            if (!isScrolling) {

                val layoutInfo =
                    state.layoutInfo

                val center =
                    (
                            layoutInfo.viewportStartOffset +
                                    layoutInfo.viewportEndOffset
                            ) / 2

                val centerItem =
                    layoutInfo.visibleItemsInfo
                        .minByOrNull { item ->

                            kotlin.math.abs(
                                (item.offset + item.size / 2) -
                                        center
                            )
                        }

                centerItem?.let { item ->

                    if (item.index in items.indices) {

                        onItemSelected(
                            item.index
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(50.dp)
            .clip(
                RoundedCornerShape(25.dp)
            )
            .background(backgroundColor)
            .clickable {
                onClick()
            },

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,

            fontSize = 18.sp,

            color = textColor,

            fontFamily =
                MontserratExtraBold,

            letterSpacing =
                (-0.01).em
        )
    }
}