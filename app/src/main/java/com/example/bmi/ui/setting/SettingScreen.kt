package com.example.bmi.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bmi.R

//@Preview
//@Composable
//fun SettingScreenPreview() {
//    SettingScreen()
//}



@Composable
fun SettingScreen(
    isLogin: Boolean,
    isChecked: Boolean,

    onBackClick: () -> Unit,
    onPersonalClick: () -> Unit,
    onSyncClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onAdsClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
            .statusBarsPadding()
    ) {
        SettingTopBar(
            onBack = onBackClick
        )

        SettingContent(
            isLogin = isLogin,
            isChecked = isChecked,
            onPersonalClick = onPersonalClick,
            onSyncClick = onSyncClick,
            onLanguageClick = onLanguageClick,
            onFeedbackClick = onFeedbackClick,
            onAdsClick = onAdsClick,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingTopBar(
    onBack: () -> Unit
)
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
                .clickable {
                    onBack()
                }
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
fun SettingContent(
    isLogin: Boolean,
    isChecked: Boolean,
    onPersonalClick: () -> Unit,
    onSyncClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onAdsClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
)
{
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()))
    {
        PersonalCard(
            isLogin = isLogin,
            onPersonalClick = onPersonalClick,
            onSyncClick = onSyncClick
        )

        SettingContainer1(
            isChecked = isChecked,
            onLanguageClick = onLanguageClick,
            onCheckedChange = onCheckedChange
        )

        SettingContainer2(
            isLogin = isLogin,
            onAdsClick = onAdsClick,
            onFeedbackClick = onFeedbackClick
        )

    }
}

@Composable
fun PersonalCard(
    isLogin: Boolean,
    onPersonalClick: () -> Unit,
    onSyncClick: () -> Unit
)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 15.dp, end = 15.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFFFFFFF))
            .clickable {
                onPersonalClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isLogin) {
                Spacer(
                    modifier = Modifier.width(10.dp)
                )
                Image(
                    painter = painterResource(R.drawable.img_user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                )

            }
            Spacer(
                modifier = Modifier.width(15.dp)
            )
            Column{
                if (isLogin)
                    Spacer(
                        modifier = Modifier.height(1.dp)
                    ) else
                        Spacer(
                            modifier = Modifier.height(16.5.dp)
                        )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = if (isLogin) {
                            "Cassie"
                        } else {
                            stringResource(R.string.set_backup)
                        },
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
                    text = if (isLogin) {
                        "cassiexiao@gmail.com"
                    } else {
                        "Synchronize your data"
                    },
                    fontSize = 16.sp,
                    color = Color(0xFF444444),
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_regular)
                    ),
                    letterSpacing = (-0.01).em,
                    modifier = Modifier
                )
                }
        }





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
                .clickable {
                    onSyncClick()
                }
        )
    }
}

@Composable
fun SettingContainer1(
    isChecked: Boolean,
    onLanguageClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        LanguageItem(onClick = onLanguageClick)

        Divider()

        GoogleFitItem(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun LanguageItem(onClick: () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 10.dp, start = 15.dp)
        .clickable{
            onClick()
    },
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
fun GoogleFitItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
){

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 10.dp, start = 15.dp, end = 15.dp),
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

        Spacer(
            modifier = Modifier.weight(1f)
        )


        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.8f)
        )

    }
}

@Composable
fun SettingContainer2(
    isLogin: Boolean,
    onAdsClick: () -> Unit,
    onFeedbackClick: () -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.5.dp, start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
    ) {
        AdsItem(onClick = onAdsClick)

        Divider()

        RateUsItem()

        Divider()

        FeedbackItem(onClick = onFeedbackClick)

        if (!isLogin) {
            Divider()

            PolicyItem()
        }
    }
}

@Composable
fun AdsItem(onClick: () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(top = 3.5.dp, start = 15.dp)
        .clickable{
            onClick()
        },
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
fun FeedbackItem(onClick: () -> Unit)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(start = 15.dp)
        .clickable{
            onClick()
        },
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