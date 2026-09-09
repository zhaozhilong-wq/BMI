package com.example.bmi.ui.recent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.bmi.R
import com.example.bmi.data.entity.BmiRecord
import com.example.bmi.ui.result.category.BmiClassifier

//@Preview
//@Composable
//fun PreviewRecentScreen() {
//    RecentScreen(onBackClick = {}, onItemClick = {}, recordLists = listOf(
//        BmiRecord(1, 70.0, 170.0, "kg", "cm", 22.0, 25, "1", false, 10, 10, 1, 1,1),
//        BmiRecord(2, 80.0, 180.0, "kg", "cm", 26.0, 25, "1", false, 10, 10, 1, 1,1),
//        BmiRecord(3, 90.0, 190.0, "kg", "cm", 24.0, 25, "1", false, 10, 10, 1, 1,1)
//    ))
//}


@Composable
fun RecentScreen(
    uiState: RecentUiState,
    onIntent: (RecentIntent) -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderRecent(onBackClick = { onIntent(RecentIntent.BackClick) })
        BMIRecordList(onItemClick = { onIntent(RecentIntent.RecordClick(it)) }, recordLists = uiState.records)
    }
}

@Composable
fun HeaderRecent(onBackClick: () -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.5.dp, start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onBackClick()
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.recent),
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em
        )
    }
}

@Composable
fun BMIRecordList(onItemClick: (id: Long) -> Unit, recordLists : List<BmiRecord>)
{
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp, start = 15.dp, end = 15.dp, bottom = 15.dp),
        contentPadding = PaddingValues(0.dp)
    ){
        items(recordLists){record->
            BMIRecordItem(record = record, onItemClick = onItemClick)
        }
    }
}

@Composable
fun BMIRecordItem(record: BmiRecord,onItemClick: (id: Long) -> Unit)
{
    val times = listOf(
        stringResource(R.string.morning),
        stringResource(R.string.afternoon),
        stringResource(R.string.evening),
        stringResource(R.string.night)
    )
    val category = BmiClassifier.classify(record)
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 15.dp)
        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(15.dp))
        .clickable {
            onItemClick(record.id)
        }
    ){
        Text(
            text = String.format("%.1f", record.bmi),
            fontSize = 27.sp,
            color = Color(0xFF000000),
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .padding(top = 13.5.dp, start = 24.dp)
        )

        Row(modifier = Modifier.padding(start = 24.dp, top = 49.5.dp, bottom = 16.5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = colorResource(category.colorRes), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = stringResource(category.displayName),
                fontSize = 16.sp,
                color = Color(0xFF000000),
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                letterSpacing = (-0.01).em,
            )
        }

        Text(
            text = "${months[record.month]} ${record.day}, ${record.year}\n" +
                    times[record.time],
            fontSize = 15.sp,
            color = Color(0xFF444444),
            fontFamily = FontFamily(Font(R.font.montserrat_regular)),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .wrapContentWidth()
                .height(36.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 41.5.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = "Arrow Right",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp)
                .size(18.dp)
        )



    }
}


private val months = listOf(
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "June",
    "July",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
)