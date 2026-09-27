package io.aerodlyn.sprig.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.aerodlyn.sprig.data.entity.PlantEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Query("UPDATE plants SET archived_at = :archivedAt WHERE id = :plantId")
    suspend fun archivePlant(plantId: Long, archivedAt: Instant = Instant.now())

    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlantById(plantId: Long): Flow<PlantEntity?>

    @Query("SELECT * FROM plants WHERE archived_at IS NULL ORDER BY name ASC")
    fun getAllActivePlants(): Flow<List<PlantEntity>>
}
