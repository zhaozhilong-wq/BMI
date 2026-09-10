package com.example.bmi.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.bmi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiDialDialog(
    uiState: ResultUiState,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,

        // 原来的 XML 是 24dp 顶部圆角
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        ),

        // 白色背景
        containerColor = Color.White,

        // 原来的 XML 没有顶部拖拽条
        dragHandle = null,
    ) {

        val record = uiState.record

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            // =========================
            // 标题
            // =========================

            Text(
                text = stringResource(
                    if (record?.isChild == true) {
                        R.string.bmi_teenager_tip
                    } else {
                        R.string.bmi_adult_tip
                    }
                ),
                fontSize = 20.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.Black,
                letterSpacing = (-0.01).em,
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 20.dp
                )
            )

            // =========================
            // 儿童副标题
            // =========================

            if (record?.isChild == true) {

                val gender =
                    if (record.gender == "Male") {
                        stringResource(R.string.gender_boy)
                    } else {
                        stringResource(R.string.gender_girl)
                    }

                Text(
                    text = stringResource(
                        R.string.bmi_teenager_info_tip,
                        record.age.toString(),
                        gender
                    ),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_regular)
                    ),
                    color = Color.Black.copy(alpha = 0.7f),
                    letterSpacing = (-0.01).em,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 2.5.dp
                    )
                )
            }

            // =========================
            // 表盘
            // =========================

            BmiDialViewSection(
                record = record,
                dialConfig = uiState.dialConfig,
                isDialog = true,
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
            )

            // =========================
            // BMI 分类列表
            // =========================

            BmiCategoryList(
                modifier = Modifier.padding(
                    top = 25.dp
                ),
                record = record,
                selectedCategory = uiState.category,
                childThreshold = uiState.childThreshold
            )

            // =========================
            // GOT IT
            // =========================

            Text(
                text = "GOT IT",
                fontSize = 20.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_extrabold)
                ),
                color = Color.White,
                letterSpacing = (-0.01).em,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 40.dp,
                        end = 40.dp,
                        top = 23.dp,
                        bottom = 16.dp
                    )
                    .height(55.dp)
                    .clip(
                        RoundedCornerShape(27.dp)
                    )
                    .background(
                        Color(0xFF3659CF)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = onDismiss
                    )
                    .wrapContentHeight(
                        Alignment.CenterVertically
                    )
            )
        }
    }
}