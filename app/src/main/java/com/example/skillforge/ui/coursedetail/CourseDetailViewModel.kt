package com.example.skillforge.ui.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.model.Course
import com.example.skillforge.data.repository.SkillforgeRepository
import com.example.skillforge.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val repository: SkillforgeRepository,
    private val categoryId: String,
    private val courseId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Course>>(UiState.Loading)
    val uiState: StateFlow<UiState<Course>> = _uiState

    init {
        loadCourse()
    }

    fun loadCourse() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getCatalog()
            _uiState.value = result.fold(
                onSuccess = { response ->
                    val course = response.categories
                        .find { it.id == categoryId }
                        ?.courses
                        ?.find { it.id == courseId }
                    if (course != null) UiState.Success(course)
                    else UiState.Error("Course not found")
                },
                onFailure = { UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}
