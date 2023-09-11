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
import com.example.android.politicalpreparedness.databinding.RepresentativeListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(private val clickListener: RepresentativeListener) :
    ListAdapter<Representative, RepresentativeListAdapter.RepresentativeListItemViewHolder>(
        RepresentativeDiffCallback
    ) {

    object RepresentativeDiffCallback : ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem == newItem
        }

    }

    class RepresentativeListItemViewHolder(val binding: RepresentativeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): RepresentativeListItemViewHolder {
                return RepresentativeListItemViewHolder(
                    RepresentativeListItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }

        fun bind(item: Representative) {
            //binding.representative = item
            //binding.representativePhoto.setImageResource(R.drawable.ic_profile)

            //TODO: Show social links ** Hint: Use provided helper methods
            //TODO: Show www link ** Hint: Use provided helper methods

            binding.executePendingBindings()
        }

        private fun showSocialLinks(channels: List<Channel>) {
            val facebookUrl = getFacebookUrl(channels)
            if (!facebookUrl.isNullOrBlank()) {
                enableLink(binding.representativeFacebook, facebookUrl)
            }

            val twitterUrl = getTwitterUrl(channels)
            if (!twitterUrl.isNullOrBlank()) {
                enableLink(binding.representativeTwitter, twitterUrl)
            }
        }

        private fun showWWWLinks(urls: List<String>) {
            enableLink(binding.representativeWww, urls.first())
        }

        private fun getFacebookUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
        }

        private fun getTwitterUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
        }

        private fun enableLink(view: ImageView, url: String) {
            view.visibility = View.VISIBLE
            view.setOnClickListener { setIntent(url) }
        }

        private fun setIntent(url: String) {
            val uri = Uri.parse(url)
            val intent = Intent(ACTION_VIEW, uri)
            itemView.context.startActivity(intent)
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepresentativeListItemViewHolder {
        return RepresentativeListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeListItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class RepresentativeListener(val clickListener: (representative: Representative) -> Unit) {
        fun onClick(representative: Representative) = clickListener(representative)
    }
}