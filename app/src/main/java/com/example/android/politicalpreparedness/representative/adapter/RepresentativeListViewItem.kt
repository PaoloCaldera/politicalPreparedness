package com.example.android.politicalpreparedness.representative.adapter

import com.example.android.politicalpreparedness.representative.model.Representative

sealed class RepresentativeListViewItem {

    object Header : RepresentativeListViewItem()

    data class RepresentativeListItem(val representative: Representative) :
        RepresentativeListViewItem()

}