package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ListHeaderBinding
import com.example.android.politicalpreparedness.databinding.RepresentativeListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.network.models.Representative

class RepresentativeListAdapter(private val listTitle: String) :
    ListAdapter<RepresentativeListViewItem, RecyclerView.ViewHolder>(RepresentativeDiffCallback) {

    companion object {
        // Item types for recycler view
        private const val HEADER_VIEW_TYPE = 0
        private const val REPRESENTATIVE_ITEM_VIEW_TYPE = 2
    }

    /**
     * DiffUtil class managing the modifications on the associated recycler view.
     * Since in this case there is no id associated to the list item, the function areItemsTheSame
     * check the entire data object content instead of just the id
     */
    object RepresentativeDiffCallback : ItemCallback<RepresentativeListViewItem>() {
        override fun areItemsTheSame(
            oldItem: RepresentativeListViewItem,
            newItem: RepresentativeListViewItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RepresentativeListViewItem,
            newItem: RepresentativeListViewItem
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
     * ViewHolder class for the Representative list items of the recycler view
     */
    class RepresentativeListItemViewHolder private constructor(val binding: RepresentativeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            // Inflate the list item layout and create the item view holder
            fun from(parent: ViewGroup): RepresentativeListItemViewHolder {
                return RepresentativeListItemViewHolder(
                    RepresentativeListItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        // Bind the list item view holder
        fun bind(listItem: Representative) {
            binding.apply {
                representativeListItem = listItem

                // Hide social profile links at the beginning, to show then only the available ones
                representativeWww.visibility = View.GONE
                representativeFacebook.visibility = View.GONE
                representativeTwitter.visibility = View.GONE

                executePendingBindings()
            }

            listItem.official.channels?.let { channels -> showSocialLinks(channels) }
            listItem.official.urls?.let { urls -> showWWWLinks(urls) }
        }

        /*
        Show social apps icons if there is an available link to social profiles
         */
        private fun showSocialLinks(channels: List<Channel>) {
            // Set up facebook url
            val facebookUrl = channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
            if (!facebookUrl.isNullOrBlank()) {
                enableLink(binding.representativeFacebook, facebookUrl)
            }

            // Set up twitter url
            val twitterUrl = channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
            if (!twitterUrl.isNullOrBlank()) {
                enableLink(binding.representativeTwitter, twitterUrl)
            }
        }

        /*
        Show website icon if there is an available website
         */
        private fun showWWWLinks(urls: List<String>) {
            // Set up website url
            enableLink(binding.representativeWww, urls.first())
        }

        /*
        Make the corresponding view visible and then prepare the intent to the web page
         */
        private fun enableLink(view: ImageView, url: String) {
            view.visibility = View.VISIBLE
            view.setOnClickListener {
                val intent = Intent(ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent)
            }
        }
    }



    fun addHeaderAndSubmitList(list: List<Representative>?) {
        submitList(when (list) {
            null -> listOf(RepresentativeListViewItem.Header)
            else -> listOf(RepresentativeListViewItem.Header) +
                    list.map { RepresentativeListViewItem.RepresentativeListItem(it) }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RepresentativeListViewItem.Header -> HEADER_VIEW_TYPE
            is RepresentativeListViewItem.RepresentativeListItem -> REPRESENTATIVE_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> ListHeaderViewHolder.from(parent)
            REPRESENTATIVE_ITEM_VIEW_TYPE -> RepresentativeListItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW_TYPE -> (holder as ListHeaderViewHolder).bind(listTitle)
            REPRESENTATIVE_ITEM_VIEW_TYPE -> {
                val representativeListItem =
                    getItem(position) as RepresentativeListViewItem.RepresentativeListItem
                (holder as RepresentativeListItemViewHolder).bind(representativeListItem.representative)
            }
        }
    }
}