package io.aerodlyn.sprig.domain

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.aerodlyn.sprig.data.db.SprigDatabase
import io.aerodlyn.sprig.data.entity.PlantEntity
import io.aerodlyn.sprig.data.model.CareType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class CareRepositoryTest {

    private lateinit var database: SprigDatabase
    private lateinit var repository: CareRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SprigDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = CareRepositoryImpl(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun completeCare_onTime_advancesNextDueOnByInterval() = runTest {
        val plant = PlantEntity(name = "Monstera", location = "Living Room")
        val intervalDays = 7
        val dueOn = LocalDate.of(2026, 9, 10)
        val plantId = repository.addPlantWithSchedule(plant, CareType.WATERING, intervalDays, dueOn)

        val completedOn = LocalDate.of(2026, 9, 10)
        repository.completeCare(plantId, CareType.WATERING, completedOn, Instant.now())

        val events = repository.getCareEventsForPlant(plantId).first()
        assertEquals(1, events.size)

        val schedule =
            database.careScheduleDao().getScheduleForPlantAndType(plantId, CareType.WATERING)
        assertNotNull(schedule)
        assertEquals(LocalDate.of(2026, 9, 17), schedule?.nextDueOn)
    }

    @Test
    fun completeCare_fourDaysLate_advancesNextDueOnFromActualCompletionDate() = runTest {
        val plant = PlantEntity(name = "Fiddle Leaf Fig", location = "Bedroom")
        val intervalDays = 7
        val dueOn = LocalDate.of(2026, 9, 10)
        val plantId = repository.addPlantWithSchedule(plant, CareType.WATERING, intervalDays, dueOn)

        // Watered 4 days late on Sept 14
        val completedOn = LocalDate.of(2026, 9, 14)
        repository.completeCare(
            plantId,
            CareType.WATERING,
            completedOn,
            Instant.now(),
            notes = "Watered late"
        )

        val events = repository.getCareEventsForPlant(plantId).first()
        assertEquals(1, events.size)
        assertEquals("Watered late", events[0].notes)

        // Recalculates from ACTUAL completion date (Sept 14 + 7 days = Sept 21)
        val schedule =
            database.careScheduleDao().getScheduleForPlantAndType(plantId, CareType.WATERING)
        assertNotNull(schedule)
        assertEquals(LocalDate.of(2026, 9, 21), schedule?.nextDueOn)
    }

    @Test
    fun completeCare_earlyCompletion_advancesNextDueOnFromEarlyCompletionDate() = runTest {
        val plant = PlantEntity(name = "Snake Plant", location = "Hallway")
        val intervalDays = 10
        val dueOn = LocalDate.of(2026, 9, 20)
        val plantId = repository.addPlantWithSchedule(plant, CareType.WATERING, intervalDays, dueOn)

        // Watered 2 days early on Sept 18
        val completedOn = LocalDate.of(2026, 9, 18)
        repository.completeCare(plantId, CareType.WATERING, completedOn, Instant.now())

        // Recalculates from early completion date (Sept 18 + 10 days = Sept 28)
        val schedule =
            database.careScheduleDao().getScheduleForPlantAndType(plantId, CareType.WATERING)
        assertNotNull(schedule)
        assertEquals(LocalDate.of(2026, 9, 28), schedule?.nextDueOn)
    }

    @Test
    fun getDuePlants_returnsDueAndOverduePlantsSortedByDueDateAscending() = runTest {
        val today = LocalDate.of(2026, 9, 20)

        val plant1 = PlantEntity(name = "Pothos", location = "Kitchen")
        repository.addPlantWithSchedule(plant1, CareType.WATERING, 7, LocalDate.of(2026, 9, 15))

        val plant2 = PlantEntity(name = "ZZ Plant", location = "Office")
        repository.addPlantWithSchedule(plant2, CareType.WATERING, 14, LocalDate.of(2026, 9, 20))

        val plant3 = PlantEntity(name = "Calathea", location = "Bathroom")
        repository.addPlantWithSchedule(plant3, CareType.WATERING, 5, LocalDate.of(2026, 9, 21))

        val duePlants = repository.getDuePlants(today).first()

        assertEquals(2, duePlants.size)
        assertEquals("Pothos", duePlants[0].plant.name)
        assertEquals(LocalDate.of(2026, 9, 15), duePlants[0].schedule.nextDueOn)

        assertEquals("ZZ Plant", duePlants[1].plant.name)
        assertEquals(LocalDate.of(2026, 9, 20), duePlants[1].schedule.nextDueOn)
    }

    @Test
    fun getDuePlants_filtersOutArchivedPlants() = runTest {
        val today = LocalDate.of(2026, 9, 20)

        val plant = PlantEntity(name = "Dead Fern", location = "Balcony")
        val plantId =
            repository.addPlantWithSchedule(plant, CareType.WATERING, 7, LocalDate.of(2026, 9, 10))

        var duePlants = repository.getDuePlants(today).first()
        assertEquals(1, duePlants.size)

        database.plantDao().archivePlant(plantId, Instant.now())

        duePlants = repository.getDuePlants(today).first()
        assertTrue(duePlants.isEmpty())
    }
}
