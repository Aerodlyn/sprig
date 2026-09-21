package io.aerodlyn.sprig.data.model

import androidx.room.Embedded
import io.aerodlyn.sprig.data.entity.CareScheduleEntity
import io.aerodlyn.sprig.data.entity.PlantEntity

data class DuePlant(
    @Embedded(prefix = "plant_")
    val plant: PlantEntity,

    @Embedded(prefix = "schedule_")
    val schedule: CareScheduleEntity
)
