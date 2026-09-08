package com.example.bmi.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bmi.R

@Preview
@Composable
fun SyncDialogPreview() {
    SyncDialog(
        onDismiss = {},
        onDoneClick = {}
    )
}


@Composable
fun SyncDialog(
    onDismiss: () -> Unit,
    onDoneClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        Box(
            modifier = Modifier
                .width(310.dp)
                .height(399.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {

            Image(
                painter = painterResource(R.drawable.smile),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 30.dp)
            )

            Text(
                text = stringResource(
                    R.string.sorry_for_inconvenience
                ),
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp
                    )
                    .offset(y = 103.dp)
            )

            Text(
                text = stringResource(
                    R.string.data_sync_adjust_toast
                ),
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp
                    )
                    .offset(y = 167.dp)
            )

            Text(
                text = stringResource(R.string.done),
                fontSize = 20.sp,
                color = Color.White,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(
                        start = 30.dp,
                        end = 30.dp
                    )
                    .align(Alignment.BottomCenter)
                    .offset(y = (-22).dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF3659CF))
                    .clickable {
                        onDoneClick()
                    }
                    .wrapContentHeight(
                        Alignment.CenterVertically
                    )
            )
        }
    }
}