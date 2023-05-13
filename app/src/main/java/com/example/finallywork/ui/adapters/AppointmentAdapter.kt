package com.example.finallywork.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.databinding.ItemUserAppointmentBinding
import com.example.finallywork.models.Doctor
import com.example.finallywork.models.UserAppointment
import java.text.SimpleDateFormat
import java.util.Date

class AppointmentAdapter(private val context: Context) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {
    var list: List<UserAppointment>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onCancelClick: ((UserAppointment) -> Unit)? = null
    var onRateClick: ((UserAppointment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(
            ItemUserAppointmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        list?.let { list ->
            holder.onBind(list[position])
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    inner class AppointmentViewHolder(private val binding: ItemUserAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: UserAppointment
        ) {
            Doctor.getById(item.doctorId,
                onSuccess = {
                    binding.doctorName.text = it.lastName + " " + it.firstName
                    binding.appointmentDate.text =
                        SimpleDateFormat(UserAppointment.DATE_FORMAT_PATTERN).format(item.date.time)
                    binding.appointmentTime.text =
                        SimpleDateFormat(UserAppointment.TIME_FORMAT_PATTERN).format(item.date.time)
                    if (Date() < item.date && !item.isRated) {
                        binding.rateAppointmentButton.visibility = View.GONE
                        binding.cancelAppointmentButton.visibility = View.VISIBLE
                    } else if (Date() > item.date && !item.isRated) {
                        binding.rateAppointmentButton.visibility = View.VISIBLE
                        binding.cancelAppointmentButton.visibility = View.GONE
                    } else {
                        binding.rateAppointmentButton.visibility = View.GONE
                        binding.cancelAppointmentButton.visibility = View.GONE
                    }
                    binding.cancelAppointmentButton.setOnClickListener {
                        onCancelClick?.invoke(item)
                    }
                    binding.rateAppointmentButton.setOnClickListener {
                        onRateClick?.invoke(item)
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}