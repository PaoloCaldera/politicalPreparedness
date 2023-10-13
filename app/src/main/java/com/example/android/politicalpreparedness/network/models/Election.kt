package com.example.android.politicalpreparedness.network.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.*
import com.squareup.moshi.*
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "election_table")
@Parcelize
data class Election(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "electionDay") val electionDay: Date,
    @Embedded(prefix = "division_") @Json(name = "ocdDivisionId") val division: Division
) : Parcelable {

    @SuppressLint("SimpleDateFormat")
    fun formatDateForList(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(electionDay)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateForDetail(): String {
        return SimpleDateFormat("E, dd MMMM yyyy").format(electionDay)
    }
}