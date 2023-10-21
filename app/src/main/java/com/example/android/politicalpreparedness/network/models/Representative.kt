package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Representative(
    val official: Official,
    val office: Office
) : Parcelable