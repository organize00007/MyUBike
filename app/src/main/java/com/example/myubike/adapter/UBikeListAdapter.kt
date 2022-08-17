package com.example.myubike.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myubike.databinding.UBikeListItemBinding
import com.example.myubike.model.UBike
import com.example.myubike.ui.UBikeFragment

class UBikeListAdapter(private val uBikeListFragment: UBikeFragment) : ListAdapter<UBike, UBikeListAdapter.UBikeListViewHolder>(DiffCallback) {

    class UBikeListViewHolder(val binding: UBikeListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    companion object DiffCallback : DiffUtil.ItemCallback<UBike>() {
        override fun areItemsTheSame(oldItem: UBike, newItem: UBike): Boolean {
            return oldItem.sno == newItem.sno
        }

        override fun areContentsTheSame(oldItem: UBike, newItem: UBike): Boolean {
            return oldItem.sno == newItem.sno
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UBikeListViewHolder {
        val binding = UBikeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UBikeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UBikeListViewHolder, position: Int) {
        holder.binding.item = getItem(position)
        holder.itemView.setOnClickListener {
            uBikeListFragment.moveCameraToSelectedLocation(position)
        }
        holder.binding.btnNavigation.setOnClickListener {
            AlertDialog.Builder(uBikeListFragment.requireContext())
                .setTitle("確認")
                .setMessage("請選擇移動方式")
                .setNegativeButton("取消", null)
                .setPositiveButton("步行") { _, _ ->
                    uBikeListFragment.showPath(position, "walking")
                }
                .setNeutralButton("開車") { _, _ ->
                    uBikeListFragment.showPath(position, "driving")
                }
                .show()
        }
    }
}