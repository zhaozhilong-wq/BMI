package com.example.bmi.ui.input


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PickerAdapter(
    private var data: List<String>,
    private val itemLayoutId: Int,
    private val textViewId: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {

    private var selectedPosition =
        RecyclerView.NO_POSITION

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        lateinit var textView: TextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    itemLayoutId,
                    parent,
                    false
                )

        val holder = ViewHolder(view)

        holder.textView =
            view.findViewById(textViewId)

        return holder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.textView.text =
            data[position]

        holder.textView.alpha =
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

        if (position !in data.indices) {//有效范围下标判断
            return
        }

        if (selectedPosition == position) {
            return
        }

        selectedPosition = position

        notifyDataSetChanged()//重新检查一下整个列表并刷新
    }

    fun updateData(newData: List<String>) {

        data = newData

        notifyDataSetChanged()
    }
}