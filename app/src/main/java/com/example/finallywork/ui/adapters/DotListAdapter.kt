package com.example.finallywork.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finallywork.R
import com.example.finallywork.databinding.ItemDotListBinding

class DotListAdapter(private val context: Context) :
    RecyclerView.Adapter<DotListAdapter.DotListViewHolder>() {
    var list: List<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotListViewHolder {
        return DotListViewHolder(
            ItemDotListBinding.inflate(
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

    inner class DotListViewHolder(private val binding: ItemDotListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            item: String,
        ) {
            binding.value.text =
                context.getString(R.string.dot_list_formatter, "", item)

        }
    }
}