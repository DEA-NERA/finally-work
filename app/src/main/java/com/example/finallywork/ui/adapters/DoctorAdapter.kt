package com.example.finallywork.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.models.Doctor

class DoctorAdapter(private val context: Context):
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    var DoctorList: List<Doctor>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onItemClick: ((Doctor) -> Unit)? = null

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
        DoctorList?.let { list ->
            holder.onBind(list[position], onItemClick, chosenDoctor)
        }
    }

    override fun getItemCount(): Int = DoctorList?.size ?: 0

    inner class DoctorViewHolder(private val binding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            item: Doctor,
            onItemClick: ((Doctor) -> Unit)?,
            chosenDoctor: Doctor?
        ) {
            binding.itemDoctorInfo.text =
                context.getString(R.string.Doctor_name_address_formatter, item.name, item.address)

            if (item == chosenDoctor) {
                binding.itemDoctorIcon.setImageResource(R.drawable.ic_check)
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}