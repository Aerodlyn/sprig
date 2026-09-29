package io.aerodlyn.sprig.data.db

import androidx.room.withTransaction
import io.aerodlyn.sprig.data.entity.CareScheduleEntity
import io.aerodlyn.sprig.data.entity.PlantEntity
import io.aerodlyn.sprig.data.model.CareType
import java.time.LocalDate

object DatabaseInitializer {

    suspend fun seedIfEmpty(database: SprigDatabase, today: LocalDate = LocalDate.now()) {
        val plantDao = database.plantDao()
        val careScheduleDao = database.careScheduleDao()

        val isDbEmpty = database.withTransaction {
            val cursor = database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM plants")
            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
            count == 0
        }

        if (isDbEmpty) {
            database.withTransaction {
                val samplePlants = listOf(
                    Triple("Monstera Deliciosa", "Living Room", Pair(7, today.minusDays(3))),
                    Triple("Fiddle Leaf Fig", "Bedroom", Pair(10, today.minusDays(1))),
                    Triple("Snake Plant", "Office", Pair(14, today)),
                    Triple("Pothos", "Kitchen", Pair(7, today.plusDays(4)))
                )

                for ((name, location, schedule) in samplePlants) {
                    val (intervalDays, nextDueOn) = schedule
                    val plantId = plantDao.insertPlant(
                        PlantEntity(
                            name = name,
                            location = location
                        )
                    )
                    careScheduleDao.insertSchedule(
                        CareScheduleEntity(
                            plantId = plantId,
                            careType = CareType.WATERING,
                            intervalDays = intervalDays,
                            nextDueOn = nextDueOn
                        )
                    )
                }
            }
        }
    }
}
