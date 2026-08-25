package com.example.bmi.ui.result.category

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.bmi.R
import com.example.bmi.databinding.ItemBmiTypeBinding

object BmiCategoryViewHelper {

    fun createAdultCategoryItems(): List<BmiCategoryItem> {
        return listOf(
            BmiCategoryItem(
                category = BmiCategory.VSU,
                name = "Very Severely Underweight",
                range = "<16.0",
                backgroundColor = R.color.vsu_checked,
            ),
            BmiCategoryItem(
                category = BmiCategory.SU,
                name = "Severely Underweight",
                range = "16.0-16.9",
                backgroundColor = R.color.su_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "17.0-18.4",
                backgroundColor = R.color.underweight_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "18.5-24.9",
                backgroundColor = R.color.normal_cycle
            ),
            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "25.0-29.9",
                backgroundColor = R.color.overweight_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese Class I",
                range = "30.0-34.9",
                backgroundColor = R.color.oc1_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_II,
                name = "Obese Class II",
                range = "35.0-39.9",
                backgroundColor = R.color.oc2_checked
            ),
            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_III,
                name = "Obese Class III",
                range = "≥40.0",
                backgroundColor = R.color.oc3_checked
            )
        )
    }

    fun createChildCategoryItems(
        threshold: ChildBmiThreshold
    ): List<BmiCategoryItem> {
        return listOf(

            BmiCategoryItem(
                category = BmiCategory.UNDERWEIGHT,
                name = "Underweight",
                range = "<${formatBmi(threshold.underweight)}",
                backgroundColor = R.color.underweight_checked
            ),

            BmiCategoryItem(
                category = BmiCategory.NORMAL,
                name = "Normal",
                range = "${formatBmi(threshold.underweight)}-${formatBmi(threshold.normal)}",
                backgroundColor = R.color.normal_cycle
            ),

            BmiCategoryItem(
                category = BmiCategory.OVERWEIGHT,
                name = "Overweight",
                range = "${formatBmi(threshold.normal)}-${formatBmi(threshold.overweight)}",
                backgroundColor = R.color.overweight_checked
            ),

            BmiCategoryItem(
                category = BmiCategory.OBESE_CLASS_I,
                name = "Obese",
                range = "≥${formatBmi(threshold.overweight)}",
                backgroundColor = R.color.oc1_checked
            )
        )

    }

    private fun formatBmi(value: Float): String {
        return String.format("%.1f", value)
    }

    fun setup(
        container: ViewGroup,
        items: List<BmiCategoryItem>,
        selectedCategory: BmiCategory,
        inflater: LayoutInflater
    ) {
        container.removeAllViews()

        items.forEach { item ->

            val itemBinding = ItemBmiTypeBinding.inflate(
                inflater,
                container,
                false
            )

            itemBinding.typeText.text = inflater.context.getString(item.category.displayName)
            itemBinding.typeRange.text = item.range
            itemBinding.typeCycle.background =
                AppCompatResources.getDrawable(
                    inflater.context,
                    R.drawable.bg_type_cycle
                )
            // 根据分类设置圆形颜色
            itemBinding.typeCycle.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        inflater.context,
                        item.category.colorRes
                    )
                )

            if (item.category == selectedCategory) {
                itemBinding.root.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        inflater.context,
                        item.backgroundColor
                    )
                )
                // 选中状态的圆也不用 cycle_selected 图片
                itemBinding.typeCycle.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            inflater.context,
                            R.color.white
                        )
                    )
                itemBinding.typeText.setTextColor(
                    itemBinding.typeText.context.getColor(
                        R.color.white
                    )
                )
                itemBinding.typeText.typeface =
                    ResourcesCompat.getFont(itemBinding.typeText.context, R.font.montserrat_extrabold)
                itemBinding.typeText.alpha = 1f
                itemBinding.typeRange.setTextColor(
                    itemBinding.typeRange.context.getColor(
                        R.color.white
                    )
                )
                itemBinding.typeRange.typeface =
                    ResourcesCompat.getFont(itemBinding.typeRange.context, R.font.montserrat_extrabold)
                itemBinding.typeRange.alpha = 1f
            }

            container.addView(itemBinding.root)
        }
    }
}