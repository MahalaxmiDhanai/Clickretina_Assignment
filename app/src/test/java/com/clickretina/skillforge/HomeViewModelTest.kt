package com.clickretina.skillforge

import com.clickretina.skillforge.data.model.Category
import com.clickretina.skillforge.data.model.Course
import com.clickretina.skillforge.data.model.Instructor
import com.clickretina.skillforge.data.model.Lesson
import com.clickretina.skillforge.data.model.Meta
import com.clickretina.skillforge.data.model.SkillforgeResponse
import com.clickretina.skillforge.data.repository.SkillforgeRepository
import com.clickretina.skillforge.ui.UiState
import com.clickretina.skillforge.ui.home.HomeViewModel
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

// ──────────────────────────────────────────────────────────────────────────────
// Fake repository for testing — returns a hardcoded catalog
// ──────────────────────────────────────────────────────────────────────────────
private val fakeCatalog = SkillforgeResponse(
    meta = Meta(app = "Skillforge", version = "1.0", generatedAt = "2026-06-22"),
    categories = listOf(
        Category(
            id = "cat_android",
            name = "Android Development",
            description = "Build modern Android apps",
            iconColor = "#2dd4bf",
            courseCount = 1,
            courses = listOf(
                Course(
                    id = "course_kotlin_101",
                    title = "Kotlin Fundamentals",
                    subtitle = "Everything you need to start writing Kotlin",
                    thumbnailUrl = "https://example.com/thumb.png",
                    level = "Beginner",
                    durationHours = 6.5,
                    rating = 4.7,
                    studentsEnrolled = 18420,
                    language = "English",
                    lastUpdated = "2026-03-12",
                    tags = listOf("Kotlin", "Basics", "JVM"),
                    instructor = Instructor(
                        id = "inst_aarav",
                        name = "Aarav Sharma",
                        title = "Senior Android Engineer",
                        avatarUrl = "https://example.com/avatar.png",
                        bio = "10+ years building Android apps."
                    ),
                    description = "Learn Kotlin from zero.",
                    lessons = listOf(
                        Lesson(
                            id = "les_k1",
                            title = "Welcome & Setup",
                            durationMinutes = 8,
                            isFree = true,
                            videoUrl = "https://example.com/v/k1",
                            content = "Set up Android Studio."
                        ),
                        Lesson(
                            id = "les_k2",
                            title = "Variables & Null Safety",
                            durationMinutes = 15,
                            isFree = true,
                            videoUrl = "https://example.com/v/k2",
                            content = "Learn val vs var."
                        ),
                        Lesson(
                            id = "les_k3",
                            title = "Functions & Lambdas",
                            durationMinutes = 18,
                            isFree = false,
                            videoUrl = "https://example.com/v/k3",
                            content = "Write functions."
                        )
                    )
                )
            )
        )
    )
)

private class FakeSuccessRepository : SkillforgeRepository {
    override suspend fun getCatalog(): Result<SkillforgeResponse> = Result.success(fakeCatalog)
}

private class FakeErrorRepository(private val msg: String) : SkillforgeRepository {
    override suspend fun getCatalog(): Result<SkillforgeResponse> =
        Result.failure(Exception(msg))
}

// ──────────────────────────────────────────────────────────────────────────────
// Tests
// ──────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeViewModel emits Success with correct category data from fake repository`() = runTest {
        val vm = HomeViewModel(FakeSuccessRepository())
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first()
        assertTrue("Expected Success state", state is UiState.Success)

        val data = (state as UiState.Success).data
        assertEquals(1, data.categories.size)

        val category = data.categories[0]
        assertEquals("cat_android", category.id)
        assertEquals("Android Development", category.name)
        assertEquals(1, category.courseCount)
    }

    @Test
    fun `HomeViewModel success state contains course with correct fields`() = runTest {
        val vm = HomeViewModel(FakeSuccessRepository())
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first() as UiState.Success
        val course = state.data.categories[0].courses[0]

        assertEquals("course_kotlin_101", course.id)
        assertEquals("Kotlin Fundamentals", course.title)
        assertEquals(4.7, course.rating, 0.001)
        assertEquals(6.5, course.durationHours, 0.001)
        assertEquals("Beginner", course.level)
        assertEquals("Aarav Sharma", course.instructor.name)
    }

    @Test
    fun `HomeViewModel lessons have correct isFree values`() = runTest {
        val vm = HomeViewModel(FakeSuccessRepository())
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first() as UiState.Success
        val lessons = state.data.categories[0].courses[0].lessons

        assertEquals(3, lessons.size)
        assertTrue("First lesson should be free", lessons[0].isFree)
        assertTrue("Second lesson should be free", lessons[1].isFree)
        assertTrue("Third lesson should be locked", !lessons[2].isFree)
    }

    @Test
    fun `HomeViewModel emits Error state when repository fails`() = runTest {
        val errorMsg = "Network unavailable"
        val vm = HomeViewModel(FakeErrorRepository(errorMsg))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.first()
        assertTrue("Expected Error state", state is UiState.Error)
        assertEquals(errorMsg, (state as UiState.Error).message)
    }
}

