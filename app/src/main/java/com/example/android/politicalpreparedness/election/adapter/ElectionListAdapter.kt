package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.databinding.ListHeaderBinding
import com.example.android.politicalpreparedness.network.models.Election


class ElectionListAdapter(
    private val listTitle: String,
    private val clickListener: ElectionListener
) :
    ListAdapter<ElectionListViewItem, RecyclerView.ViewHolder>(ElectionDiffCallback) {

    companion object {
        // Item types for recycler view
        private const val HEADER_VIEW_TYPE = 0
        private const val ELECTION_ITEM_VIEW_TYPE = 1
    }

    /**
     * DiffUtil class managing the modifications on the associated recycler view
     */
    object ElectionDiffCallback : DiffUtil.ItemCallback<ElectionListViewItem>() {
        override fun areItemsTheSame(
            oldItem: ElectionListViewItem,
            newItem: ElectionListViewItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ElectionListViewItem,
            newItem: ElectionListViewItem
        ): Boolean {
            return oldItem == newItem
        }
    }


    /**
     * ViewHolder class for header of the recycler view
     */
    class ListHeaderViewHolder private constructor(val binding: ListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // Inflate the header layout and create the header view holder
            fun from(parent: ViewGroup): ListHeaderViewHolder {
                return ListHeaderViewHolder(
                    ListHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        // Bind the header view holder
        fun bind(title: String) {
            binding.apply {
                listHeaderTitle = title
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
        fun bind(listItem: Election, clickListener: ElectionListener) {
            binding.apply {
                electionListItem = listItem
                electionClickListener = clickListener
                executePendingBindings()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ElectionListViewItem.Header -> HEADER_VIEW_TYPE
            is ElectionListViewItem.ElectionListItem -> ELECTION_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> ListHeaderViewHolder.from(parent)
            ELECTION_ITEM_VIEW_TYPE -> ElectionListItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW_TYPE -> (holder as ListHeaderViewHolder).bind(listTitle)
            ELECTION_ITEM_VIEW_TYPE -> {
                val electionListItem = getItem(position) as ElectionListViewItem.ElectionListItem
                (holder as ElectionListItemViewHolder).bind(
                    electionListItem.election,
                    clickListener
                )
            }
        }
    }


    /**
     * Click listener class used to handle the click on a recycler view item
     */
    class ElectionListener(val clickListener: (election: Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }
}