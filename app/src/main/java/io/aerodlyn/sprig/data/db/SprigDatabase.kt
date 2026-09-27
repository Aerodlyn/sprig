package io.aerodlyn.sprig.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.aerodlyn.sprig.data.dao.CareEventDao
import io.aerodlyn.sprig.data.dao.CareScheduleDao
import io.aerodlyn.sprig.data.dao.PlantDao
import io.aerodlyn.sprig.data.entity.CareEventEntity
import io.aerodlyn.sprig.data.entity.CareScheduleEntity
import io.aerodlyn.sprig.data.entity.PlantEntity
import io.aerodlyn.sprig.data.entity.SpeciesEntity

@Database(
    entities = [
        PlantEntity::class,
        CareScheduleEntity::class,
        CareEventEntity::class,
        SpeciesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SprigDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun careScheduleDao(): CareScheduleDao

    abstract fun careEventDao(): CareEventDao
}
