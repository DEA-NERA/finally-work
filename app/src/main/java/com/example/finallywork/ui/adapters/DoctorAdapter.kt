package com.example.finallywork.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.R
import com.example.finallywork.databinding.ItemDoctorBinding
import com.example.finallywork.models.Doctor
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

class DoctorAdapter(private val context: Context, private val isAdmin: Boolean) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {
    var doctorList: List<Doctor>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onDeleteClick: ((Doctor) -> Unit)? = null
    var onEditClick: ((Doctor) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        doctorList?.let { list ->
            holder.onBind(list[position])
        }
    }

    override fun getItemCount(): Int = doctorList?.size ?: 0

    inner class DoctorViewHolder(private val binding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: Doctor
        ) {
            binding.NameDoctor.text = item.lastName + " " + item.firstName
            binding.RatingDoctor.text = item.rating.toString()
            val age = Period.between(
                item.dateOfBirth.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                LocalDate.now()
            ).years
            binding.AgeDoctor.text =
                context.getString(R.string.dot_list_formatter, "вік:", age.toString())
            val experience = Period.between(
                item.dateStartWork.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                LocalDate.now()
            ).years
            binding.ExpirienceDoctor.text =
                context.getString(R.string.dot_list_formatter, "стаж:", experience.toString())

            val adapter = DotListAdapter(context)
            adapter.list = item.specializations
            binding.SpecializationsList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.SpecializationsList.adapter = adapter

            binding.button.setOnClickListener {
                if (binding.DetailsGroup.visibility == View.GONE) {
                    binding.DetailsGroup.visibility = View.VISIBLE
                    binding.card.setCardBackgroundColor(context.getColor(R.color.light_green))
                    if (isAdmin) {
                        binding.editDoctorButton.visibility = View.VISIBLE
                        binding.deleteDoctorButton.visibility = View.VISIBLE
                        binding.createAppointmentButton.visibility = View.GONE
                    } else {
                        binding.createAppointmentButton.visibility = View.VISIBLE
                        binding.editDoctorButton.visibility = View.GONE
                        binding.deleteDoctorButton.visibility = View.GONE
                    }
                    binding.button.rotation = 180F
                } else {
                    binding.DetailsGroup.visibility = View.GONE
                    binding.card.setCardBackgroundColor(context.getColor(R.color.white))
                    binding.button.rotation = 0F
                }
            }

            binding.deleteDoctorButton.setOnClickListener {
                onDeleteClick?.invoke(item)
            }
            binding.editDoctorButton.setOnClickListener {
                onEditClick?.invoke(item)
            }
        }

    }
}