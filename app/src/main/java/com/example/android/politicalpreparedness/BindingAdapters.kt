package com.example.android.politicalpreparedness

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListViewItem
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("header", "listData", "onItemClick")
fun bindElectionListData(
    recyclerView: RecyclerView,
    header: String,
    listData: List<ElectionListViewItem>,
    onItemClick: (Election) -> Unit
) {
    val adapter =
        ElectionListAdapter(header, ElectionListAdapter.ElectionListener { onItemClick(it) })
    adapter.submitList(listData)
    recyclerView.adapter = adapter
}


@BindingAdapter("status")
fun bindSaveRemoveFab(fab: FloatingActionButton, election: Election?) {
    if (election == null)
        fab.setImageResource(R.drawable.ic_save)        // To save/follow
    else
        fab.setImageResource(R.drawable.ic_remove)      // To remove/unfollow
}