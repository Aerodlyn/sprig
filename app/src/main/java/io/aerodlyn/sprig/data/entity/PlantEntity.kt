package io.aerodlyn.sprig.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "plants",
    foreignKeys = [
        ForeignKey(
            entity = SpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["species_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["species_id"])]
)
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val location: String,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,

    @ColumnInfo(name = "species_id")
    val speciesId: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @ColumnInfo(name = "archived_at")
    val archivedAt: Instant? = null
)
