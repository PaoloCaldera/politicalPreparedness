package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListHeaderBinding
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
// import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election


class ElectionListAdapter(
    private val listTitle: String,
    private val clickListener: ElectionListener
) :
    ListAdapter<Election, RecyclerView.ViewHolder>(ElectionDiffCallback) {

    // Item types for recycler view
    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1

    object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem == newItem
        }
    }


    /**
     * ViewHolder class for header of the recycler view
     */
    class ElectionListHeaderViewHolder private constructor(val binding: ElectionListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // Inflate the header layout and create the header view holder
            fun from(parent: ViewGroup): ElectionListHeaderViewHolder {
                return ElectionListHeaderViewHolder(
                    ElectionListHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        // Bind the header view holder
        fun bind(title: String) {
            binding.apply {
                electionListTitle = title
                executePendingBindings()
            }

        }
    }

    /**
     * ViewHolder class for the Election list items of the recycler view
     */
    class ElectionListItemViewHolder private constructor(val binding: ElectionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // Inflate the list item layout and create the item view holder
            fun from(parent: ViewGroup): ElectionListItemViewHolder {
                return ElectionListItemViewHolder(
                    ElectionListItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        // Bind the list item view holder
        fun bind(listItem: Election) {
            binding.apply {
                electionListItem = listItem
                executePendingBindings()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW_TYPE else ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW_TYPE)
            ElectionListHeaderViewHolder.from(parent)
        else
            ElectionListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_VIEW_TYPE)
            (holder as ElectionListHeaderViewHolder).bind(listTitle)
        else
            (holder as ElectionListItemViewHolder).bind(getItem(position - 1))
    }
}

// TODO: Create ElectionListener

