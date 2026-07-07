package com.example.skillforge.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.model.SkillforgeResponse
import com.example.skillforge.data.repository.SkillforgeRepository
import com.example.skillforge.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SkillforgeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<SkillforgeResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<SkillforgeResponse>> = _uiState

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getCatalog()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}
