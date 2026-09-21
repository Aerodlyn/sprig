package io.aerodlyn.sprig.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.aerodlyn.sprig.data.model.CareType
import java.time.LocalDate

@Entity(
    tableName = "care_schedules",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["plant_id"]),
        Index(value = ["next_due_on"])
    ]
)
data class CareScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "plant_id")
    val plantId: Long,

    @ColumnInfo(name = "care_type")
    val careType: CareType = CareType.WATERING,

    @ColumnInfo(name = "interval_days")
    val intervalDays: Int,

    @ColumnInfo(name = "next_due_on")
    val nextDueOn: LocalDate
)
