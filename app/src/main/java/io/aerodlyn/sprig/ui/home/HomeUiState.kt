package io.aerodlyn.sprig.ui.home

import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.data.model.DuePlant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Success(val duePlants: List<DuePlantItem>) : HomeUiState
}

enum class OverdueLevel {
    DUE_TODAY,
    OVERDUE
}

data class DuePlantItem(
    val plantId: Long,
    val plantName: String,
    val location: String,
    val careType: CareType,
    val intervalDays: Int,
    val nextDueOn: LocalDate,
    val overdueLevel: OverdueLevel,
    val overdueText: String
)

fun DuePlant.toDuePlantItem(today: LocalDate): DuePlantItem {
    val daysOverdue = ChronoUnit.DAYS.between(schedule.nextDueOn, today)
    val level = if (daysOverdue > 0) OverdueLevel.OVERDUE else OverdueLevel.DUE_TODAY
    val text = when {
        daysOverdue > 1 -> "Overdue by $daysOverdue days"
        daysOverdue == 1L -> "Overdue by 1 day"
        else -> "Due today"
    }

    return DuePlantItem(
        plantId = plant.id,
        plantName = plant.name,
        location = plant.location,
        careType = schedule.careType,
        intervalDays = schedule.intervalDays,
        nextDueOn = schedule.nextDueOn,
        overdueLevel = level,
        overdueText = text
    )
}
