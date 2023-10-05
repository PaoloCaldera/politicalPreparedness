package com.example.android.politicalpreparedness.network.jsonadapter

import android.annotation.SuppressLint
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
class ElectionAdapter {

    /**
     * Adapter methods for converting the division value into a more suitable format
     */

    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }


    /**
     * Adapter methods for converting string typed dates into Date objects
     */

    @FromJson
    fun dateFromJson (electionDay: String): Date {
        return SimpleDateFormat("yyyy-MM-dd").parse(electionDay)!!
    }

    @ToJson
    fun dateToJson (date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd").format(date)
    }
}