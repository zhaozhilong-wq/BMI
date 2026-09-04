package com.example.bmi.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.bmi.R

@Preview
@Composable
fun SettingScreenPreview() {
    SettingScreen()
}



@Composable
fun SettingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
            .statusBarsPadding()
    ) {
        SettingTopBar()

        SettingContent()
    }
}

@Composable
fun SettingTopBar()
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.5.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Back",
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .offset(x = 15.dp , y = 18.5.dp)
        )
        Text(
            text = stringResource(R.string.mine),
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .offset(x = 49.dp , y = 17.5.dp)

        )

    }
}

@Composable
fun SettingContent()
{
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()))
    {
        PersonalCard()

        SettingContainer1()

        SettingContainer2()

    }
}

@Composable
fun PersonalCard()
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 15.dp, end = 15.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        Image(
            painter = painterResource(R.drawable.img_user),
            contentDescription = null,
            modifier = Modifier
                .size(65.dp)
                .offset(
                    x = 10.dp,
                    y = 4.dp
                )
                .clip(CircleShape)
        )

        Row(
            modifier = Modifier
                .offset(
                    x = 90.dp,
                    y = 19.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.set_backup),
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                letterSpacing = (-0.01).em,
            )

            Spacer(modifier = Modifier.width(4.5.dp))

            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                Modifier
                    .size(22.dp)
            )
        }


        Text(
            text = "Synchronize your data",
            fontSize = 14.sp,
            color = Color(0xFF444444),
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em,
            modifier = Modifier
                .offset(
                    x = 90.dp,
                    y = 44.5.dp
                )
        )

        Image(
            painter = painterResource(R.drawable.ic_autorenew_black_24px),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
                .offset(
                    x = (-15).dp,
                    y = 28.dp
                )
        )
    }
}

@Composable
fun SettingContainer1()
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        LanguageItem()

        Divider()

        GoogleFitItem()
    }
}

@Composable
fun LanguageItem()
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 10.dp, start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFFFA000),RoundedCornerShape(8.dp))
        ){
            Image(
                painter = painterResource(R.drawable.globe),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.language_title),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

    }
}

@Composable
fun Divider()
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 55.dp,end = 25.dp)
            .height(1.dp)
            .background(Color(0xFFDDDDDD))
    )
}

@Composable
fun GoogleFitItem(){
    var checked by remember {
        mutableStateOf(false)
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 10.dp, start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(
                    width = 0.5.dp,
                    color = Color(0xFF8E8E93).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
        ){
            Image(
                painter = painterResource(R.drawable.bit_map),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.connect_with_google_fit),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

        Spacer(modifier = Modifier.width(30.dp))

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            modifier = Modifier.scale(0.8f)
        )

    }
}

@Composable
fun SettingContainer2()
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.5.dp, start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        AdsItem()

        Divider()

        RateUsItem()

        Divider()

        FeedbackItem()

        Divider()

        PolicyItem()
    }
}

@Composable
fun AdsItem()
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 3.5.dp, start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF607D8B),RoundedCornerShape(8.dp))
        ){
            Image(
                painter = painterResource(R.drawable.icon_1),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.remove_ad),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

    }
}

@Composable
fun RateUsItem()
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF607D8B),RoundedCornerShape(8.dp))
        ){
            Image(
                painter = painterResource(R.drawable.icon_2),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.rate_us),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

    }
}

@Composable
fun FeedbackItem()
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF607D8B),RoundedCornerShape(8.dp))
        ){
            Image(
                painter = painterResource(R.drawable.icon_3),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.feedback),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

    }
}

@Composable
fun PolicyItem()
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(start = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF607D8B),RoundedCornerShape(8.dp))
        ){
            Image(
                painter = painterResource(R.drawable.icon_4),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = stringResource(R.string.privacy_policy),
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            letterSpacing = (-0.01).em
        )

    }
}