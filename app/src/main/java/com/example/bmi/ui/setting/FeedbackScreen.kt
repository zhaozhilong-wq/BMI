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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun FeedbackScreenPreview()
{
    FeedbackScreen(
        onBackClick = {},
        onSaveClick = {}
    )
}

@Composable
fun FeedbackScreen(onBackClick: () -> Unit,
                   onSaveClick: () -> Unit)
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEE))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HeaderFeedBack(onBackClick = onBackClick)

        InputArea(onSaveClick = onSaveClick)
    }
}

@Composable
fun HeaderFeedBack(onBackClick: () -> Unit)
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
                .clickable{
                    onBackClick()
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.feedback),
            fontSize = 20.sp,
            color = Color(0xFF000000),
            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
            letterSpacing = (-0.01).em
        )
    }
}

@Composable
fun InputArea(onSaveClick: () -> Unit)
{
    var text by remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(
            start = 15.dp,
            end = 15.dp,
            top = 64.5.dp,
            bottom = 40.dp
        )
        .imePadding()
        .background(Color(0xFFFFFFFF), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
    ){
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            placeholder = { Text(text = stringResource(R.string.feedback_or_suggestion),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                color = Color.Black.copy(alpha = 0.5f),
                letterSpacing = (-0.01).em)},
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_regular)),
                color = Color.Black,
                letterSpacing = (-0.01).em
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
            )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    bottom = 10.dp
                )
                .height(55.dp)
                .background(
                    color = Color(0xFF3659CF),
                    shape = RoundedCornerShape(27.5.dp)
                )
                .clickable {
                    onSaveClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.save),
                fontSize = 20.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.White,
                letterSpacing = (-0.01).em
            )
        }

    }
}