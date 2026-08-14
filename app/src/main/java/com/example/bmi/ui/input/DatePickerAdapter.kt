package com.example.bmi.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R

class DatePickerAdapter(
    private val data: List<String>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DatePickerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvDate: TextView =
            view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_date_picker,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.tvDate.text = data[position]

        // 默认灰色
        holder.tvDate.alpha = 0.3f

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}