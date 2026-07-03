// DashboardViewModel.kt – fetches balance / positions from repository
package com.hermes.trading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.trading.api.BalanceData
import com.hermes.trading.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _positions = MutableStateFlow<List<BalanceData>>(emptyList())
    val positions: StateFlow<List<BalanceData>> = _positions.asStateFlow()

    private val _isEngineRunning = MutableStateFlow(false)
    val isEngineRunning: StateFlow<Boolean> = _isEngineRunning.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // Fetch balance via repository
                val balance = authRepository.fetchBalance()
                _balance.value = balance
                _positions.value = emptyList() // placeholder until positions endpoint is ready
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
