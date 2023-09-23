package com.example.android.politicalpreparedness.database

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}