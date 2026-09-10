package com.example.bmi.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bmi.R

@Composable
fun ConfirmDeleteDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel
    ) {
        Box(
            modifier = Modifier
                .width(301.dp)
                .height(154.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(Color.White)
        ) {

            Text(
                text = stringResource(R.string.delete_conf),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.Black,
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 15.dp,
                        top = 25.dp
                    )
            )

            Text(
                text = stringResource(R.string.are_you_sur),
                fontSize = 13.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_regular)
                ),
                color = Color.Black,
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = 15.dp,
                        end = 20.dp,
                        top = 50.dp
                    )
            )

            Text(
                text = stringResource(R.string.cancel),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color(0xFF3659CF),
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 84.dp,
                        bottom = 19.5.dp
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = onCancel
                    )
            )

            Text(
                text = stringResource(R.string.delete),
                fontSize = 16.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color(0xFF3659CF),
                letterSpacing = (-0.01).em,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 24.5.dp,
                        bottom = 19.5.dp
                    )
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