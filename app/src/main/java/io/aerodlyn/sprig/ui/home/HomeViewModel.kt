package io.aerodlyn.sprig.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.domain.CareRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val careRepository: CareRepository,
    private val todayProvider: () -> LocalDate = { LocalDate.now() }
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = careRepository
        .getDuePlants(todayProvider())
        .map { duePlants ->
            if (duePlants.isEmpty()) {
                HomeUiState.Empty
            } else {
                val today = todayProvider()
                val items = duePlants.map { it.toDuePlantItem(today) }
                HomeUiState.Success(items)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    fun completeCare(plantId: Long, careType: CareType = CareType.WATERING) {
        viewModelScope.launch {
            careRepository.completeCare(
                plantId = plantId,
                careType = careType,
                completedOn = todayProvider()
            )
        }
    }

    class Factory(
        private val careRepository: CareRepository,
        private val todayProvider: () -> LocalDate = { LocalDate.now() }
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(careRepository, todayProvider) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
