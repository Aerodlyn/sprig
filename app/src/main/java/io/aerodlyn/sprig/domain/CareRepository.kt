package io.aerodlyn.sprig.domain

import androidx.room.withTransaction
import io.aerodlyn.sprig.data.db.SprigDatabase
import io.aerodlyn.sprig.data.entity.CareEventEntity
import io.aerodlyn.sprig.data.entity.CareScheduleEntity
import io.aerodlyn.sprig.data.entity.PlantEntity
import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.data.model.DuePlant
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface CareRepository {
    fun getDuePlants(today: LocalDate): Flow<List<DuePlant>>

    fun getAllActivePlants(): Flow<List<PlantEntity>>

    fun getPlantById(plantId: Long): Flow<PlantEntity?>

    fun getCareEventsForPlant(plantId: Long): Flow<List<CareEventEntity>>

    suspend fun addPlantWithSchedule(
        plant: PlantEntity,
        careType: CareType = CareType.WATERING,
        intervalDays: Int,
        firstNextDueOn: LocalDate
    ): Long

    suspend fun completeCare(
        plantId: Long,
        careType: CareType = CareType.WATERING,
        completedOn: LocalDate,
        completedAt: Instant = Instant.now(),
        notes: String? = null
    )
}

class CareRepositoryImpl(
    private val database: SprigDatabase
) : CareRepository {
    private val plantDao = database.plantDao()
    private val careScheduleDao = database.careScheduleDao()
    private val careEventDao = database.careEventDao()

    override fun getDuePlants(today: LocalDate): Flow<List<DuePlant>> {
        return careScheduleDao.getDuePlants(today)
    }

    override fun getAllActivePlants(): Flow<List<PlantEntity>> {
        return plantDao.getAllActivePlants()
    }

    override fun getPlantById(plantId: Long): Flow<PlantEntity?> {
        return plantDao.getPlantById(plantId)
    }

    override fun getCareEventsForPlant(plantId: Long): Flow<List<CareEventEntity>> {
        return careEventDao.getEventsForPlant(plantId)
    }

    override suspend fun addPlantWithSchedule(
        plant: PlantEntity,
        careType: CareType,
        intervalDays: Int,
        firstNextDueOn: LocalDate
    ): Long {
        return database.withTransaction {
            val plantId = plantDao.insertPlant(plant)
            val schedule = CareScheduleEntity(
                plantId = plantId,
                careType = careType,
                intervalDays = intervalDays,
                nextDueOn = firstNextDueOn
            )
            careScheduleDao.insertSchedule(schedule)
            plantId
        }
    }

    override suspend fun completeCare(
        plantId: Long,
        careType: CareType,
        completedOn: LocalDate,
        completedAt: Instant,
        notes: String?
    ) {
        database.withTransaction {
            val schedule = careScheduleDao.getScheduleForPlantAndType(plantId, careType)
                ?: throw IllegalArgumentException("No schedule found for plant $plantId and careType $careType")

            val careEvent = CareEventEntity(
                plantId = plantId,
                careType = careType,
                completedAt = completedAt,
                notes = notes
            )
            careEventDao.insertEvent(careEvent)

            // Recalculate next_due_on from actual completion date
            val newNextDueOn = completedOn.plusDays(schedule.intervalDays.toLong())
            val updatedSchedule = schedule.copy(nextDueOn = newNextDueOn)
            careScheduleDao.updateSchedule(updatedSchedule)
        }
    }
}
