package com.example.bmi.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.bmi.R

@Preview
@Composable
fun PreviewLanguageSettingScreen() {
    LanguageSettingScreen()
}


@Composable
fun LanguageSettingScreen()
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
    ) {
        Header()

        LanguageItems()
    }

}

@Composable
fun Header()
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
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Language Options",
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em
        )
    }
}

@Composable
fun LanguageItems()
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 69.5.dp,
                bottom = 20.dp
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.5.dp),
            contentPadding = PaddingValues(0.dp)
        ) {

            items(languages) { language ->

                Item(
                    name = language.name,
                    selected = true,
                    onClick = {
                    }
                )
            }
        }
    }
}

@Composable
fun Item(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(49.5.dp)

    ) {

        Text(
            text = name,
            fontSize = 16.sp,
            color = Color.Black,
            letterSpacing = (-0.01).em,
            fontFamily = FontFamily(
                Font(R.font.montserrat_regular)
            ),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp)
        )

        if (selected) {
            Image(
                painter = painterResource(R.drawable.check),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 15.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 15.dp,
                    end = 15.dp
                ),
            thickness = 1.dp,
            color = Color(0xFFD4D4D5)
        )
    }
}
val languages = listOf(
    LanguageItem("en", "English"),
    LanguageItem("pt", "Português"),
    LanguageItem("ru", "Русский"),
    LanguageItem("de", "Deutsch"),
    LanguageItem("zh-TW", "繁體中文"),
    LanguageItem("zh-CN", "简体中文"),
    LanguageItem("fr", "Français"),
    LanguageItem("es", "Español"),
    LanguageItem("it", "Italiano"),
    LanguageItem("ko", "한국어"),
    LanguageItem("ar", "العربية"),
    LanguageItem("fa", "فارسی"),
    LanguageItem("id", "Bahasa Indonesia"),
    LanguageItem("ja", "日本語"),
    LanguageItem("nl", "Nederlands"),
    LanguageItem("pl", "Polski"),
    LanguageItem("th", "ไทย"),
    LanguageItem("tr", "Türkçe"),
    LanguageItem("vi", "Tiếng Việt"),
)
