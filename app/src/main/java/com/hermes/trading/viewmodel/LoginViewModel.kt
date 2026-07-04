package com.hermes.trading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.trading.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(akValue: String, secValue: String, passValue: String) {
        when {
            akValue.isBlank() -> {
                _uiState.value = LoginUiState.Error("API Key is required")
                return
            }
            secValue.length < 8 -> {
                _uiState.value = LoginUiState.Error("API Secret seems too short")
                return
            }
            passValue.length < 4 -> {
                _uiState.value = LoginUiState.Error("Passphrase is required")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                authRepository.saveCredentials(akValue, secValue, passValue)
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
