package io.aerodlyn.sprig.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "common_name")
    val commonName: String,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String,

    @ColumnInfo(name = "default_interval_days")
    val defaultIntervalDays: Int
)
