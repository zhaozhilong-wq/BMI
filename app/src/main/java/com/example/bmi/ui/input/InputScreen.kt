package com.example.bmi.ui.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.bmi.R
import kotlinx.coroutines.launch

@Preview
@Composable
fun InputScreenPreview() {
//传入空值看效果
    InputScreen(InputUiState(), onWeightChanged = {}, onWeightFocusChanged = {}, onWeightUnitSelected = {}, onHeightCmChanged = {}, onHeightFtChanged = {}, onHeightInChanged = {}, onHeightCmFocusChanged = {}, onHeightFtFocusChanged = {}, onHeightInFocusChanged = {}, onHeightUnitSelected = {}, onDateSelected = { _, _, _ -> }, onTimeSelected = {  }, onAgeSelected = {}, onGenderSelected = {}, onCalculateClick = {}, onUserClick = {})
}


@Composable
fun InputScreen(
    uiState: InputUiState,

    onWeightChanged: (String) -> Unit,
    onWeightFocusChanged: (Boolean) -> Unit,
    onWeightUnitSelected: (Boolean) -> Unit,

    onHeightCmChanged: (String) -> Unit,
    onHeightFtChanged: (String) -> Unit,
    onHeightInChanged: (String) -> Unit,
    onHeightCmFocusChanged: (Boolean) -> Unit,
    onHeightFtFocusChanged: (Boolean) -> Unit,
    onHeightInFocusChanged: (Boolean) -> Unit,
    onHeightUnitSelected: (Boolean) -> Unit,

    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (Int) -> Unit,
    onAgeSelected: (Int) -> Unit,
    onGenderSelected: (Boolean) -> Unit,
    onCalculateClick: () -> Unit,
    onUserClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
            .statusBarsPadding()

            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                focusManager.clearFocus()
            }
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            InputTopBar(
                onUserClick = onUserClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 19.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    WeightSection(
                        uiState = uiState,
                        onWeightChanged = onWeightChanged,
                        onWeightFocusChanged = onWeightFocusChanged,
                        onWeightUnitSelected = onWeightUnitSelected,
                        modifier = Modifier.weight(1f)
                    )

                    HeightSection(
                        uiState = uiState,
                        onHeightCmChanged = onHeightCmChanged,
                        onHeightFtChanged = onHeightFtChanged,
                        onHeightInChanged = onHeightInChanged,
                        onHeightCmFocusChanged = onHeightCmFocusChanged,
                        onHeightFtFocusChanged = onHeightFtFocusChanged,
                        onHeightInFocusChanged = onHeightInFocusChanged,
                        onHeightUnitSelected = onHeightUnitSelected,
                        modifier = Modifier.weight(1f)
                    )
                }

                DateTimeSection(
                    uiState = uiState,
                    onDateClick = {
                        showDatePicker = true
                    },
                    onTimeClick = {
                        showTimePicker = true
                    }
                )

                AgeSection(
                    age = uiState.age,
                    onAgeSelected = onAgeSelected
                )

                GenderSection(
                    selectedMale = uiState.isMale,
                    onGenderSelected = onGenderSelected
                )


            }

        }

        CalculateButton(
            onClick = onCalculateClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        if (showDatePicker) {

            DatePickerBottomSheet(
                year = uiState.year,
                month = uiState.month,
                day = uiState.day,

                onDismiss = {
                    showDatePicker = false
                },

                onDone = { year, month, day ->

                    showDatePicker = false

                    onDateSelected(
                        year,
                        month,
                        day
                    )
                }
            )
        }

        if (showTimePicker) {

            TimePickerBottomSheet(
                timeSlot = uiState.timeSlot,

                onDismiss = {
                    showTimePicker = false
                },

                onDone = { timeSlot ->

                    showTimePicker = false

                    onTimeSelected(timeSlot)
                }
            )
        }

    }
}

@Composable
fun InputTopBar(
    onUserClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.calculator),
            fontSize = 24.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(R.drawable.user),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    onClick = onUserClick
                )
        )
    }
}

@Composable
fun WeightSection(
    uiState: InputUiState,
    onWeightChanged: (String) -> Unit,
    onWeightFocusChanged: (Boolean) -> Unit,
    onWeightUnitSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = stringResource(R.string.weight),
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(start = 52.dp)
                .padding(bottom = 15.dp)
        )

        InputTextField(
            value = uiState.weightText,
            onValueChange = onWeightChanged,
            onFocusChanged = onWeightFocusChanged,
            maxLength = 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        UnitSelector(
            leftText = stringResource(R.string.unit_lb),
            rightText = stringResource(R.string.unit_kg),
            leftSelected = !uiState.isWeightKg,
            onLeftClick = {
                onWeightUnitSelected(false)
            },
            onRightClick = {
                onWeightUnitSelected(true)
            }
        )
    }
}


@Composable
fun UnitSelector(
    leftText: String,
    rightText: String,
    leftSelected: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Color(0x80FFFFFF)
            )
    ) {

        UnitItem(
            text = leftText,
            selected = leftSelected,
            modifier = Modifier.weight(1f),
            onClick = onLeftClick
        )

        UnitItem(
            text = rightText,
            selected = !leftSelected,
            modifier = Modifier.weight(1f),
            onClick = onRightClick
        )
    }
}



@Composable
fun UnitItem(
    text: String,
    selected: Boolean,
    modifier: Modifier,
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
            color = Color.Black.copy(alpha = if (selected) 1f else 0.2f)
        )
    }
}


@Composable
fun HeightSection(
    uiState: InputUiState,
    onHeightCmChanged: (String) -> Unit,
    onHeightFtChanged: (String) -> Unit,
    onHeightInChanged: (String) -> Unit,
    onHeightCmFocusChanged: (Boolean) -> Unit,
    onHeightFtFocusChanged: (Boolean) -> Unit,
    onHeightInFocusChanged: (Boolean) -> Unit,
    onHeightUnitSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = stringResource(R.string.height),
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(start = 56.dp)
                .padding(bottom = 15.dp)
        )

        if (uiState.isHeightCm) {

            InputTextField(
                value = uiState.heightCmText,
                onValueChange = onHeightCmChanged,
                onFocusChanged = onHeightCmFocusChanged,
                maxLength = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            )

        } else {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                horizontalArrangement = Arrangement.spacedBy(5.5.dp)
            ) {

                HeightInputWithUnit(
                    value = uiState.heightFtText,
                    unit = "'",
                    onValueChange = onHeightFtChanged,
                    onFocusChanged = onHeightFtFocusChanged,
                    modifier = Modifier.weight(140f)
                )

                HeightInputWithUnit(
                    value = uiState.heightInText,
                    unit = "\"",
                    onValueChange = onHeightInChanged,
                    onFocusChanged = onHeightInFocusChanged,
                    modifier = Modifier.weight(169f)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        UnitSelector(
            leftText = stringResource(R.string.unit_ft_in),
            rightText = stringResource(R.string.unit_cm),
            leftSelected = !uiState.isHeightCm,
            onLeftClick = {
                onHeightUnitSelected(false)
            },
            onRightClick = {
                onHeightUnitSelected(true)
            }
        )
    }
}



@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = { text ->
            if (text.matches(Regex("^\\d*\\.?\\d*$"))) {
                if (text.length <= maxLength) {
                    onValueChange(text)
                }
            }
        },
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .onFocusChanged {
                onFocusChanged(it.isFocused)
            },
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.01).em
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun HeightInputWithUnit(
    value: String,
    unit: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
    ) {

        InputTextField(
            value = value,
            onValueChange = onValueChange,
            onFocusChanged = onFocusChanged,
            maxLength = if (unit == "'") 2 else 1,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = unit,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 22.dp)
        )
    }
}

@Composable
fun DateTimeSection(
    uiState: InputUiState,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = stringResource(R.string.log_weight_time),
            modifier = Modifier
                .fillMaxWidth(),
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.01).em
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp
                )
                .height(60.dp)
        ) {

            DateItem(
                text = "${monthName(uiState.month)} ${uiState.day},${uiState.year}",
                onClick = onDateClick,
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(15.dp)
            )

            DateItem(
                text = stringResource(
                    when (uiState.timeSlot) {
                        0 -> R.string.morning
                        1 -> R.string.afternoon
                        2 -> R.string.evening
                        else -> R.string.night
                    }
                ),
                onClick = onTimeClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
private fun DateItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            fontSize = 20.sp,
            color = Color.Black,
            letterSpacing = (-0.01).em,
            textAlign = TextAlign.Center
        )
    }
}
private fun monthName(month: Int): String {
    return when (month) {
        0 -> "Jan"
        1 -> "Feb"
        2 -> "Mar"
        3 -> "Apr"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "Aug"
        8 -> "Sep"
        9 -> "Oct"
        10 -> "Nov"
        11 -> "Dec"
        else -> ""
    }
}

@Composable
fun AgeSection(
    age: Int,
    onAgeSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {

        Text(
            text = stringResource(R.string.age),
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        AgePicker(
            selectedAge = age,
            onAgeSelected = onAgeSelected
        )
    }
}
@Composable
private fun AgePicker(
    selectedAge: Int,
    onAgeSelected: (Int) -> Unit
) {
    val ages = (2..99).toList()

    val listState = rememberLazyListState()

    val itemWidth = 70.dp

    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState
    )

    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(
                start = 25.dp,
                end = 15.dp
            )
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
    ) {

        val sidePadding = (maxWidth - itemWidth) / 2

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = sidePadding,
                end = sidePadding
            ),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            items(
                items = ages,
                key = { it }
            ) { age ->

                AgeItem(
                    age = age,
                    selected = age == selectedAge,
                    onClick = {
                        onAgeSelected(age)

                        // 点击后滑到中间
                        scope.launch {
                            listState.animateScrollToItem(
                                index = age - 2
                            )
                        }
                    }
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.age_pointer),
            contentDescription = null,
            modifier = Modifier
                .width(15.dp)
                .height(11.dp)
                .align(Alignment.BottomCenter)
        )
    }
    LaunchedEffect(Unit) {
        listState.scrollToItem(
            index = 25 - 2
        )
    }

    // 滑动结束后确定当前选中年龄
    LaunchedEffect(listState) {

        snapshotFlow {
            listState.isScrollInProgress
        }.collect { isScrolling ->

            if (!isScrolling) {

                val layoutInfo = listState.layoutInfo

                val center =
                    (layoutInfo.viewportStartOffset +
                            layoutInfo.viewportEndOffset) / 2

                val centerItem =
                    layoutInfo.visibleItemsInfo.minByOrNull { item ->

                        kotlin.math.abs(
                            (item.offset + item.size / 2) - center
                        )
                    }

                centerItem?.let { item ->

                    val age = ages[item.index]

                    if (age != selectedAge) {
                        onAgeSelected(age)
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeItem(
    age: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(72.dp)
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = age.toString(),
            fontSize = 32.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(
                if (selected) 1f else 0.3f
            )
        )
    }
}
@Composable
fun GenderSection(
    selectedMale: Boolean,
    onGenderSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 25.dp,
                end = 15.dp,
                top = 15.dp
            )
            .height(90.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        GenderItem(
            selected = selectedMale,
            icon = R.drawable.male,
            text = stringResource(R.string.male),
            onClick = {
                onGenderSelected(true)
            },
            modifier = Modifier.weight(1f)
        )

        GenderItem(
            selected = !selectedMale,
            icon = R.drawable.female,
            text = stringResource(R.string.female),
            onClick = {
                onGenderSelected(false)
            },
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
private fun GenderItem(
    selected: Boolean,
    icon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color = Color.White, shape = RoundedCornerShape(15.dp))
            .alpha(
                if (selected) 1f else 0.3f
            )
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            )
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.TopCenter)
                .offset(y = 20.dp)
        )

        if (selected) {
            Image(
                painter = painterResource(
                    R.drawable.check_circle
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset(
                        x = (-7).dp,
                        y = 6.dp
                    )
            )
        }

        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 58.dp)
        )
    }
}

@Composable
fun CalculateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.CALCULATE),
        color = Color.White,
        fontSize = 20.sp,
        fontFamily = FontFamily(
            Font(R.font.montserrat_extrabold)
        ),
        letterSpacing = (-0.01).em,
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(
                start = 15.dp,
                end = 15.dp,
                bottom = 20.dp
            )
            .height(57.5.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF3659CF))
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            )
            .wrapContentHeight(
                align = Alignment.CenterVertically
            )
    )
}
