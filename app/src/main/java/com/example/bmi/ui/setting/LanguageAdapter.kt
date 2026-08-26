package com.example.bmi.ui.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R

class LanguageAdapter(
    private val languages: List<LanguageItem>,
    private var selectedPosition: Int,
    private val onLanguageClick: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val languageName: TextView =
            itemView.findViewById(R.id.languageName)

        val check: ImageView =
            itemView.findViewById(R.id.check)

        val divider: View =
            itemView.findViewById(R.id.divider)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_language,
                parent,
                false
            )

        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        position: Int
    ) {
        holder.languageName.text = languages[position].name

        // 当前选中项显示 ✓
        holder.check.visibility =
            if (position == selectedPosition) {
                View.VISIBLE
            } else {
                View.GONE
            }

        holder.divider.visibility =
            if (position == languages.size - 1) {
                View.GONE
            } else {
                View.VISIBLE
            }

        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)

            onLanguageClick(
                languages[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }
}