package com.example.android.politicalpreparedness.network.models

data class Address (
        val line1: String,
        val line2: String? = null,
        val city: String,
        val state: String,
        val zip: String
) {
    fun toFormattedString(): String {
        var output = line1.plus(", ")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus(", ")
        output = output.plus("$zip $city, $state")
        return output
    }
}