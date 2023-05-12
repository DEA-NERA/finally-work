package com.example.finallywork.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.R
import com.example.finallywork.databinding.ItemTimeBinding
import com.example.finallywork.models.Appointment
import com.example.finallywork.models.Doctor
import java.text.SimpleDateFormat

class TimeAdapter(private val context: Context) :
    RecyclerView.Adapter<TimeAdapter.DotListViewHolder>() {
    var list: List<Appointment>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotListViewHolder {
        return DotListViewHolder(
            ItemTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DotListViewHolder, position: Int) {
        list?.let { list ->
            holder.onBind(list[position])
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    inner class DotListViewHolder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: Appointment,
        ) {
            itemView.isClickable = item.isAvailable

            if (!item.isAvailable) {
                binding.container.setCardBackgroundColor(context.getColor(R.color.gray))
            } else {
                binding.container.setCardBackgroundColor(context.getColor(R.color.light_green))
            }
            binding.time.text = SimpleDateFormat(Doctor.TIME_FORMAT_PATTERN).format(item.date.time)
        }
    }
}