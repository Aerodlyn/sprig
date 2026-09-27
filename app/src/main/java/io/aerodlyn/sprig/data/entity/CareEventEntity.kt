package io.aerodlyn.sprig.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.aerodlyn.sprig.data.model.CareType
import java.time.Instant

@Entity(
    tableName = "care_events",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plant_id"])]
)
data class CareEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "plant_id")
    val plantId: Long,

    @ColumnInfo(name = "care_type")
    val careType: CareType = CareType.WATERING,

    @ColumnInfo(name = "completed_at")
    val completedAt: Instant = Instant.now(),

    val notes: String? = null
)
