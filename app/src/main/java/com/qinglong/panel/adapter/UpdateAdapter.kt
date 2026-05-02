package com.qinglong.panel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qinglong.panel.databinding.ItemUpdateBinding
import com.qinglong.panel.utils.QingLongUpdater

class UpdateAdapter : ListAdapter<QingLongUpdater.UpdateInfo, UpdateAdapter.UpdateViewHolder>(
    UpdateDiffCallback()
) {

    private var onUpdateClick: ((QingLongUpdater.UpdateInfo) -> Unit)? = null

    fun setOnUpdateClickListener(listener: (QingLongUpdater.UpdateInfo) -> Unit) {
        onUpdateClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateViewHolder {
        val binding = ItemUpdateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpdateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpdateViewHolder, position: Int) {
        val update = getItem(position)
        holder.bind(update)
    }

    inner class UpdateViewHolder(
        private val binding: ItemUpdateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(update: QingLongUpdater.UpdateInfo) {
            binding.tvUpdateName.text = update.name
            binding.tvVersion.text = update.latestVersion
            binding.tvDescription.text = update.description
            binding.tvCurrentVersion.text = "当前: ${update.currentVersion}"

            binding.btnUpdate.setOnClickListener {
                onUpdateClick?.invoke(update)
            }
        }
    }

    class UpdateDiffCallback : DiffUtil.ItemCallback<QingLongUpdater.UpdateInfo>() {
        override fun areItemsTheSame(
            oldItem: QingLongUpdater.UpdateInfo,
            newItem: QingLongUpdater.UpdateInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: QingLongUpdater.UpdateInfo,
            newItem: QingLongUpdater.UpdateInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
}
