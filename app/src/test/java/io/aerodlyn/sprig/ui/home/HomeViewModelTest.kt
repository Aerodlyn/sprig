package io.aerodlyn.sprig.ui.home

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.aerodlyn.sprig.data.db.SprigDatabase
import io.aerodlyn.sprig.data.entity.PlantEntity
import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.domain.CareRepository
import io.aerodlyn.sprig.domain.CareRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: SprigDatabase
    private lateinit var repository: CareRepository

    private val fixedToday = LocalDate.of(2026, 9, 20)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SprigDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = CareRepositoryImpl(database)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun uiState_whenNoPlantsDue_emitsEmptyState() = runTest {
        val viewModel = HomeViewModel(repository, todayProvider = { fixedToday })

        val state = viewModel.uiState.first { it !is HomeUiState.Loading }
        assertTrue(state is HomeUiState.Empty)
    }

    @Test
    fun uiState_whenPlantsAreDue_emitsSuccessWithFormattedOverdueItems() = runTest {
        // Plant 1: 3 days overdue
        val p1 = PlantEntity(name = "Monstera", location = "Living Room")
        repository.addPlantWithSchedule(p1, CareType.WATERING, 7, fixedToday.minusDays(3))

        // Plant 2: Due today
        val p2 = PlantEntity(name = "Snake Plant", location = "Office")
        repository.addPlantWithSchedule(p2, CareType.WATERING, 14, fixedToday)

        val viewModel = HomeViewModel(repository, todayProvider = { fixedToday })

        val state = viewModel.uiState.first { it !is HomeUiState.Loading }
        assertTrue(state is HomeUiState.Success)

        val successState = state as HomeUiState.Success
        assertEquals(2, successState.duePlants.size)

        val item1 = successState.duePlants[0]
        assertEquals("Monstera", item1.plantName)
        assertEquals(OverdueLevel.OVERDUE, item1.overdueLevel)
        assertEquals("Overdue by 3 days", item1.overdueText)

        val item2 = successState.duePlants[1]
        assertEquals("Snake Plant", item2.plantName)
        assertEquals(OverdueLevel.DUE_TODAY, item2.overdueLevel)
        assertEquals("Due today", item2.overdueText)
    }

    @Test
    fun completeCare_advancesScheduleAndRemovesPlantFromDueList() = runTest {
        val p1 = PlantEntity(name = "Fiddle Leaf", location = "Bedroom")
        val plantId = repository.addPlantWithSchedule(p1, CareType.WATERING, 7, fixedToday.minusDays(1))

        val viewModel = HomeViewModel(repository, todayProvider = { fixedToday })

        val initial = viewModel.uiState.first { it !is HomeUiState.Loading }
        assertTrue(initial is HomeUiState.Success)
        assertEquals(1, (initial as HomeUiState.Success).duePlants.size)

        // Complete care
        viewModel.completeCare(plantId, CareType.WATERING)

        val next = viewModel.uiState.first { it is HomeUiState.Empty }
        assertTrue(next is HomeUiState.Empty)
    }
}
