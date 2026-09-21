package io.aerodlyn.sprig.data.db

import androidx.room.TypeConverter
import io.aerodlyn.sprig.data.model.CareType
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }

    @TypeConverter
    fun fromLocalDateString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromCareType(value: String?): CareType? {
        return value?.let { CareType.valueOf(it) }
    }

    @TypeConverter
    fun careTypeToString(careType: CareType?): String? {
        return careType?.name
    }
}
