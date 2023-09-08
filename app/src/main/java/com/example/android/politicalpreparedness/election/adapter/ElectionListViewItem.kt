package com.example.android.politicalpreparedness.election.adapter

import com.example.android.politicalpreparedness.network.models.Election

sealed class ElectionListViewItem {

    abstract val id: Int

    // Use this object as the header of the elections recycler views
    object Header : ElectionListViewItem() {
        override val id = Int.MIN_VALUE
    }

    // Use this class definition as the list item of the elections recycler views
    data class ElectionListItem(val election: Election) : ElectionListViewItem() {
        override val id = election.id
    }
}