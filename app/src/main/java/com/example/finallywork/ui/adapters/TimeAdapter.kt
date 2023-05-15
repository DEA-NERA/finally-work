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
import java.util.Date


class TimeAdapter(private val context: Context) :
    RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {
    var list: List<Appointment>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var views = ArrayList<TimeViewHolder>()
    var onItemClick: ((Appointment) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val holder = TimeViewHolder(
            ItemTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        views.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        list?.let { list ->
            holder.onBind(list[position])
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    inner class TimeViewHolder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: Appointment,
        ) {
            fillColors()
            itemView.setOnClickListener {
                if (item.isAvailable && Date() < item.date) {
                    onItemClick?.invoke(item)
                    fillColors()
                    binding.root.setCardBackgroundColor(context.getColor(R.color.main_green))
                }
            }

            binding.time.text = SimpleDateFormat(Doctor.TIME_FORMAT_PATTERN).format(item.date.time)
        }

        private fun fillColors() {
            for (i in 0 until views.size) {
                list?.let { appointments ->
                    if (!appointments[i].isAvailable || Date() > appointments[i].date) {
                        views[i].binding.root.setCardBackgroundColor(context.getColor(R.color.gray))
                    } else {
                        views[i].binding.root.setCardBackgroundColor(context.getColor(R.color.light_green))
                    }
                }
            }

        }
    }
}