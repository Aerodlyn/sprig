package io.aerodlyn.sprig.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.aerodlyn.sprig.data.entity.CareScheduleEntity
import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.data.model.DuePlant
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CareScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: CareScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: CareScheduleEntity)

    @Query("SELECT * FROM care_schedules WHERE plant_id = :plantId AND care_type = :careType LIMIT 1")
    suspend fun getScheduleForPlantAndType(plantId: Long, careType: CareType): CareScheduleEntity?

    @Query(
        """
        SELECT 
            p.id AS plant_id,
            p.name AS plant_name,
            p.location AS plant_location,
            p.photo_uri AS plant_photo_uri,
            p.species_id AS plant_species_id,
            p.created_at AS plant_created_at,
            p.archived_at AS plant_archived_at,
            s.id AS schedule_id,
            s.plant_id AS schedule_plant_id,
            s.care_type AS schedule_care_type,
            s.interval_days AS schedule_interval_days,
            s.next_due_on AS schedule_next_due_on
        FROM care_schedules s
        INNER JOIN plants p ON s.plant_id = p.id
        WHERE p.archived_at IS NULL AND s.next_due_on <= :today
        ORDER BY s.next_due_on ASC
    """
    )
    fun getDuePlants(today: LocalDate): Flow<List<DuePlant>>
}
