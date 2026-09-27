package io.aerodlyn.sprig.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.aerodlyn.sprig.data.entity.CareEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CareEventEntity): Long

    @Query("SELECT * FROM care_events WHERE plant_id = :plantId ORDER BY completed_at DESC")
    fun getEventsForPlant(plantId: Long): Flow<List<CareEventEntity>>
}
