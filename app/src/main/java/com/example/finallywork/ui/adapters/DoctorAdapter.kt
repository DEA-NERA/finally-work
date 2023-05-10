package com.example.finallywork.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.databinding.ItemDoctorBinding
import com.example.finallywork.models.Doctor

class DoctorAdapter(private val context: Context) :
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
            holder.onBind(list[position], onItemClick)
        }
    }

    override fun getItemCount(): Int = DoctorList?.size ?: 0

    inner class DoctorViewHolder(private val binding: ItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(
            item: Doctor,
            onItemClick: ((Doctor) -> Unit)?,
        ) {

            binding.NameDoctor.text = item.lastName + item.firstName
            binding.RatingDoctor.text = item.rating.toString()

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}