package com.example.bmi.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R

class DatePickerAdapter(
    private var data: List<String>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DatePickerAdapter.ViewHolder>() {

    private var selectedPosition =
        RecyclerView.NO_POSITION

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvDate: TextView =
            view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
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

        holder.tvDate.text =
            data[position]

        // 唯一决定透明度的地方
        holder.tvDate.alpha =
            if (position == selectedPosition) {
                1f
            } else {
                0.3f
            }

        holder.itemView.setOnClickListener {

            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelectedPosition(position: Int) {

        if (position !in data.indices) {
            return
        }

        if (selectedPosition == position) {
            return
        }

        selectedPosition = position

        notifyDataSetChanged()
    }

    fun updateData(
        newData: List<String>
    ) {

        data = newData

        notifyDataSetChanged()
    }
}