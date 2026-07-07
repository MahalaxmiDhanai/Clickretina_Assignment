package com.example.skillforge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skillforge.data.remote.RetrofitInstance
import com.example.skillforge.data.repository.SkillforgeRepositoryImpl
import com.example.skillforge.ui.coursedetail.CourseDetailScreen
import com.example.skillforge.ui.coursedetail.CourseDetailViewModel
import com.example.skillforge.ui.home.HomeScreen
import com.example.skillforge.ui.home.HomeViewModel
import com.example.skillforge.ui.lesson.LessonScreen
import com.example.skillforge.ui.lesson.LessonViewModel

@Composable
fun SkillforgeNavHost(
    navController: NavHostController = rememberNavController()
) {
    // Simple manual DI: shared repository instance across all ViewModels
    val repository = remember { SkillforgeRepositoryImpl(RetrofitInstance.api) }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val vm: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            })
            HomeScreen(
                viewModel = vm,
                onCourseClick = { categoryId, courseId ->
                    navController.navigate("course_detail/$categoryId/$courseId")
                }
            )
        }

        composable("course_detail/{categoryId}/{courseId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable

            val vm: CourseDetailViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return CourseDetailViewModel(repository, categoryId, courseId) as T
                    }
                }
            )
            CourseDetailScreen(
                viewModel = vm,
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onLessonClick = { catId, cId, lessonId ->
                    navController.navigate("lesson/$catId/$cId/$lessonId")
                }
            )
        }

        composable("lesson/{categoryId}/{courseId}/{lessonId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable

            val vm: LessonViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LessonViewModel(repository, categoryId, courseId, lessonId) as T
                    }
                }
            )
            LessonScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
