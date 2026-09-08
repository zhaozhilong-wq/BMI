package com.example.bmi.ui.setting



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bmi.R


@Preview
@Composable
fun LogDialog(
) {
    LogBottomSheet(isLogin = true, onDismiss = {}, onLoginClick = {}, onLogoutClick = {})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBottomSheet(
    isLogin: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFEAEAEE),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {

            // 用户信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        top = 19.dp,
                        end = 20.dp
                    ),
                verticalAlignment = Alignment.Top
            ) {

                Image(
                    painter = painterResource(R.drawable.img_user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                )

                Column(
                    modifier = Modifier
                        .padding(
                            start = 15.dp,
                            top = 15.dp
                        )
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cassie",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(
                                Font(R.font.montserrat_extrabold)
                            )
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = "cassiexiao@gmail.com",
                        fontSize = 16.sp,
                        color = Color(0xFF888888)
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(R.drawable.x),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onDismiss()
                        }
                )
            }

            // 登录 / 登出
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 37.dp
                    )
                    .height(55.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color.White)
                    .clickable {
                        if (isLogin) {
                            onLogoutClick()
                        } else {
                            onLoginClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        if (isLogin) {
                            R.string.log_out
                        } else {
                            R.string.log_in
                        }
                    ),
                    color = if (isLogin) {
                        Color(0xFFF4333C)
                    } else {
                        Color.Black
                    },
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extrabold)
                    )
                )
            }

            // Cancel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 15.dp
                    )
                    .height(55.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color.White)
                    .clickable {
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extrabold)
                    )
                )
            }
        }
    }
}