// DashboardViewModel.kt – fetches balance + positions from Bitget API in parallel
package com.hermes.trading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.trading.api.PositionData
import com.hermes.trading.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Idle)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _positions = MutableStateFlow<List<PositionData>>(emptyList())
    val positions: StateFlow<List<PositionData>> = _positions.asStateFlow()

    private val _isEngineRunning = MutableStateFlow(false)
    val isEngineRunning: StateFlow<Boolean> = _isEngineRunning.asStateFlow()

    /**
     * Refreshes the dashboard by calling Bitget API in parallel:
     *   - GET /api/v5/account/balance  (USDT total)
     *   - GET /api/v2/mix/position/all-position (open futures positions)
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val (balance, positions) = awaitAll(
                    async { authRepository.fetchBalance() },
                    async { authRepository.fetchPositions() }
                )
                @Suppress("UNCHECKED_CAST")
                val bal = (balance as? Double) ?: 0.0
                @Suppress("UNCHECKED_CAST")
                val pos = (positions as? List<PositionData>) ?: emptyList()

                _balance.value = bal
                _positions.value = pos
                _uiState.value = DashboardUiState.Success
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.localizedMessage ?: "Unable to load dashboard")
            }
        }
    }

    fun toggleEngine() {
        _isEngineRunning.value = !_isEngineRunning.value
    }
}

sealed interface DashboardUiState {
    object Idle : DashboardUiState
    object Loading : DashboardUiState
    object Success : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
