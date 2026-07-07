package com.example.skillforge.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.model.Course
import com.example.skillforge.data.model.Lesson
import com.example.skillforge.data.repository.SkillforgeRepository
import com.example.skillforge.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LessonUiData(
    val currentLesson: Lesson,
    val course: Course
)

class LessonViewModel(
    private val repository: SkillforgeRepository,
    private val categoryId: String,
    private val courseId: String,
    initialLessonId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LessonUiData>>(UiState.Loading)
    val uiState: StateFlow<UiState<LessonUiData>> = _uiState.asStateFlow()

    private val _currentLessonId = MutableStateFlow(initialLessonId)
    val currentLessonId: StateFlow<String> = _currentLessonId.asStateFlow()

    // Track mock progress (0f..1f)
    private val _progress = MutableStateFlow(0.35f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    init {
        loadLesson()
    }

    fun loadLesson() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getCatalog()
            _uiState.value = result.fold(
                onSuccess = { response ->
                    val course = response.categories
                        .find { it.id == categoryId }
                        ?.courses
                        ?.find { it.id == courseId }
                    val lesson = course?.lessons?.find { it.id == _currentLessonId.value }
                    if (course != null && lesson != null) {
                        UiState.Success(LessonUiData(currentLesson = lesson, course = course))
                    } else {
                        UiState.Error("Lesson not found")
                    }
                },
                onFailure = { UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun selectLesson(lessonId: String) {
        _currentLessonId.value = lessonId
        _progress.value = 0f
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val newLesson = currentState.data.course.lessons.find { it.id == lessonId }
            if (newLesson != null) {
                _uiState.value = UiState.Success(
                    currentState.data.copy(currentLesson = newLesson)
                )
            }
        }
    }
}
