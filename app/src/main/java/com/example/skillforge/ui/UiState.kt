package com.example.skillforge.ui

import com.example.skillforge.data.model.SkillforgeResponse

/** Generic UI state used across all screens */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
