package com.example.bmi.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmi.R

class AgePickerAdapter(
    private val ages: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AgePickerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAge: TextView = view.findViewById(R.id.tvNumber)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picker_number, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.tvAge.text = ages[position].toString()

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }

        holder.tvAge.alpha = 0.3f
    }

    override fun getItemCount(): Int {
        return ages.size
    }
}