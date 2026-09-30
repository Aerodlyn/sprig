package io.aerodlyn.sprig.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: SprigDatabase? = null

        fun getDatabase(context: Context): SprigDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SprigDatabase::class.java,
                    "sprig_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
