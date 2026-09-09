package com.example.bmi.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.ui.BmiDialConfig
import com.example.bmi.ui.BmiDialView
import com.example.bmi.ui.result.category.BmiCategory
import com.example.bmi.ui.result.category.BmiCategoryItem
import com.example.bmi.ui.result.category.BmiCategoryViewHelper
import com.example.bmi.ui.result.category.ChildBmiThreshold
import java.util.Locale

//@Preview
//@Composable
//fun ResultScreenPreview()
//{
//    ResultScreen(record = null, mode = ResultMode.NORMAL, category = BmiCategory.NORMAL,null, {}, {}, {}, {}, {}, {})
//}


@Composable
fun ResultScreen(
    uiState: ResultUiState,
    mode: ResultMode,
    onIntent: (ResultIntent) -> Unit,
)
{
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .statusBarsPadding()
    )
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            ))
        {
            ResultTopBar(
                record = uiState.record,
                mode = mode,
                onBack = { onIntent(ResultIntent.Back) },
                onDelete = { uiState.record?.let { onIntent(ResultIntent.DeleteRecord(it.id)) } },
                onRecent = { onIntent(ResultIntent.Recent) },
                onDiscard = {  }
            )

            Column(
            ){
                ResultContent(
                    record = uiState.record,
                    mode = mode,
                    category = uiState.category,
                    dialConfig = uiState.dialConfig,
                    onHelp = { onIntent(ResultIntent.Help) }
                )
            }
        }

        if (mode == ResultMode.NEW_USER || mode == ResultMode.NORMAL)
        {
            SaveButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
@Composable
fun ResultTopBar(
    record: BmiRecord?,
    mode: ResultMode,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onRecent: () -> Unit,
    onDiscard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(min = 40.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        when (mode) {

            ResultMode.NORMAL,
            ResultMode.NEW_USER -> {

                Text(
                    text = stringResource(R.string.discard),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_regular)
                    ),
                    color = Color.Black,
                    letterSpacing = (-0.01).em,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 15.5.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            onClick = onDiscard
                        )
                )
            }

            ResultMode.HISTORY -> {

                Image(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            start = 15.dp,
                            top = 17.dp,
                            bottom = 17.dp
                        )
                        .size(24.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            onBack()
                        }
                )
            }

            ResultMode.LATEST -> {
                Text(
                    text = stringResource(R.string.bmi),
                    fontSize = 24.sp,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extrabold)
                    ),
                    color = Color.Black,
                    letterSpacing = (-0.01).em,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 15.dp,
                            top = 18.dp
                        )
                )
            }
        }


        if (
            mode == ResultMode.LATEST &&
            record != null
        ) {
            Text(
                text = formatRecordDate(record),
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color.Black,
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 15.dp,
                        top = 52.dp
                    )
            )
        }

        if (mode == ResultMode.LATEST) {

            Text(
                text = stringResource(R.string.recent),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color(0xFF3659CF),
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        end = 15.dp,
                        top = 18.dp
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = onRecent
                    )
            )
        }

        if (mode == ResultMode.HISTORY) {

            Text(
                text = stringResource(R.string.delete),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color.Black.copy(alpha = 0.5f),
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 15.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = onDelete
                    )
            )
        }
    }
}

@Composable
fun ResultContent(
    record: BmiRecord?,
    mode: ResultMode,
    category: BmiCategory?,
    dialConfig: BmiDialConfig?,
    onHelp: () -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        BmiDialSection(record = record, dialConfig = dialConfig)

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(40.dp)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    category?.colorRes?.let { colorResource(it) } ?: Color.Black
                )
                .padding(horizontal = 20.dp)
                .align(Alignment.CenterHorizontally)
                .then(
                    if (mode != ResultMode.LATEST) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            onClick = onHelp
                        )
                    } else {
                        Modifier
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = category?.let {
                    stringResource(it.displayName)
                } ?: "",
                fontSize = 19.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.White,
                letterSpacing = (-0.01).em
            )

            // LATEST 不显示问号
            if (mode != ResultMode.LATEST) {
                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Image(
                    painter = painterResource(
                        R.drawable.help_circle
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = formatBmiInputData(
                record = record,
                bmiInputData = stringResource(R.string.bmi_input_data),
                male = stringResource(R.string.male),
                female = stringResource(R.string.female)
            ),
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black.copy(alpha = 0.5f),
            letterSpacing = (-0.01).em,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.5.dp)
        )

        if(mode == ResultMode.LATEST || mode == ResultMode.NEW_USER){

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 25.dp
                )
                .clip(
                    RoundedCornerShape(15.dp)
                )
                .background(
                    Color(0xFFF4F4F4)
                )
                .padding(17.dp)
        ) {

            Text(
                text = "👍 Thumbs Up! You’ve done a great job and now only need to keep your lifestyle healthy to stay in this range.",
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color.Black,
                letterSpacing = (-0.01).em,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mode == ResultMode.HISTORY || mode == ResultMode.NORMAL){ RecommendationSection(mode) }
    }
}

@Composable
private fun BmiDialSection(
    record: BmiRecord?,
    dialConfig: BmiDialConfig?
) {

    val targetRotation =
        if (record != null && dialConfig != null) {
            bmiToPointerRotation(
                record.bmi.toFloat(),
                dialConfig
            )
        } else {
            -68.6f
        }

    val targetBmi = record?.bmi?.toFloat() ?: 0f

    val animatedBmi by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetBmi,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 800
        ),
        label = "bmi"
    )

    val animatedRotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 1200
        ),
        label = "pointer"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(183.dp)
        ) {

            /*
             * dialContainer
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .padding(
                        start = 10.dp,
                        end = 11.dp,
                        top = 11.dp
                    )
            ) {
                AndroidView(
                    factory = { context ->
                        BmiDialView(context)
                    },
                    update = { view ->
                        dialConfig?.let {
                            view.setConfig(it)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Image(
                    painter = painterResource(R.drawable.result_pointer),
                    contentDescription = null,
                    modifier = Modifier
                        .size(53.dp, 92.dp)
                        .align(Alignment.BottomCenter)
                        .offset(x = (-8).dp, y = 5.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(
                                pivotFractionX = 38.543f / 53f,
                                pivotFractionY = 77.617f / 92f
                            )
                            rotationZ = animatedRotation
                        }
                )
            }

        }

        Text(
            text = "Your BMI is...",
            fontSize = 18.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.5.dp)
        )

        Text(
            text = String.format(
                Locale.US,
                "%.1f",
                animatedBmi
            ),
            fontSize = 64.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()

        )
    }
}



@Composable
private fun BmiCategoryItem(
    type: String,
    range: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(27.dp)
            .clip(
                RoundedCornerShape(13.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .padding(start = 15.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Text(
            text = type,
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black.copy(alpha = 0.7f),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        )

        Text(
            text = range,
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black.copy(alpha = 0.7f),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(end = 13.dp)
        )
    }
}

@Composable
private fun RecommendationSection(mode: ResultMode) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 22.5.dp,
                bottom = 32.5.dp
            )
    ) {

        RecommendationDivider(mode = mode)

        Text(
            text = "Apps you might need",
            fontSize = 16.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_extrabold)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    top = 24.5.dp
                )
        )

        RecommendItem(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp
                )
        )

        RecommendItem(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp
                )
        )

        RecommendItem(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 107.5.dp
                )
        )
    }
}

@Composable
private fun RecommendItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(79.dp)
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(
                Color(0xFFF4F4F4)
            )
    ) {

        Image(
            painter = painterResource(
                R.drawable.recommend_cover
            ),
            contentDescription = null,
            modifier = Modifier
                .size(65.dp)
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )

        Text(
            text = "Home Workout - No Equipments",
            fontSize = 14.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black,
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 79.dp,
                    top = 15.dp
                )
        )

        Text(
            text = "Weight Loss, Lose Belly Fat",
            fontSize = 12.sp,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            color = Color.Black.copy(alpha = 0.8f),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 79.dp,
                    top = 37.dp
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 79.dp,
                    bottom = 13.dp
                )
        ) {

            repeat(5) {
                Image(
                    painter = painterResource(
                        R.drawable.star
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 1.dp)
                )
            }

            Text(
                text = "4.8",
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier
                    .padding(start = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(
                    width = 29.5.dp,
                    height = 14.5.dp
                )
                .background(
                    Color(0xFF54A529).copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "AD",
                fontSize = 8.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecommendationDivider(
    mode: ResultMode,
    text: String = "May 6, 2021 Morning"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)

    ) {
        // 横线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .padding(start = 22.5.dp,end = 22.5.dp)
                .align(Alignment.Center)
                .alpha(0.2f)
                .background(Color.Black)
        )

        // 中间文字
        if (mode == ResultMode.HISTORY)
        {
            Text(
                text = text,
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.Black.copy(alpha = 0.5f),
                letterSpacing = (-0.01).em,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(153.5.dp)
                    .height(20.dp)
                    .align(Alignment.Center)
                    .background(Color.White)
            )
        }
    }
}


@Composable
private fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Text(
        text = stringResource(R.string.save),
        color = Color.White,
        fontSize = 20.sp,
        fontFamily = FontFamily(
            Font(R.font.montserrat_extrabold)
        ),
        letterSpacing = (-0.01).em,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 30.dp,
                end = 30.dp,
                bottom = 15.dp
            )
            .height(55.dp)
            .clip(RoundedCornerShape(27.dp))
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

private fun formatRecordDate(record: BmiRecord): String {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "June",
        "July", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    return "${months[record.month]} ${record.day}, ${record.year}"
}

private fun bmiToPointerRotation(
    bmi: Float,
    config: BmiDialConfig
): Float {

    val ratio =
        ((bmi - config.minBmi) /
                (config.maxBmi - config.minBmi))
            .coerceIn(0f, 1f)

    return -68.6f + ratio * 180f
}

private fun formatBmiInputData(
    record: BmiRecord?,
    bmiInputData: String,
    male: String,
    female: String
): String {

    if (record == null) return ""

    val weight = if (record.weightUnit == "kg") {
        String.format(
            Locale.US,
            "%.2f kg",
            record.weightKg
        )
    } else {
        String.format(
            Locale.US,
            "%.2f lb",
            record.weightKg / 0.45359237
        )
    }

    val height = if (record.heightUnit == "cm") {
        String.format(
            Locale.US,
            "%.1f cm",
            record.heightCm
        )
    } else {
        val totalInches = kotlin.math.round(
            record.heightCm / 2.54
        ).toInt()

        val feet = totalInches / 12
        val inches = totalInches % 12

        "$feet ft $inches in"
    }

    val gender = if (record.gender == "Male") {
        male
    } else {
        female
    }

    return String.format(
        Locale.US,
        bmiInputData,
        weight,
        height,
        gender,
        record.age
    )
}