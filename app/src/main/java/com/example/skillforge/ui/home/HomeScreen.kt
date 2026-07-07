package com.example.skillforge.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.data.model.Category
import com.example.skillforge.data.model.Course
import com.example.skillforge.theme.Cream
import com.example.skillforge.theme.DividerGray
import com.example.skillforge.theme.Teal700
import com.example.skillforge.theme.TextSecondary
import com.example.skillforge.ui.UiState
import com.example.skillforge.ui.components.CategoryChip
import com.example.skillforge.ui.components.CourseCard
import com.example.skillforge.ui.components.ErrorView
import com.example.skillforge.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onCourseClick: (categoryId: String, courseId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(message = s.message, onRetry = viewModel::loadCatalog)
            is UiState.Success -> {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    HomeContent(
                        categories = s.data.categories,
                        onCourseClick = onCourseClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    categories: List<Category>,
    onCourseClick: (categoryId: String, courseId: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val allCourses = categories.flatMap { cat -> cat.courses.map { cat.id to it } }
    val filteredCourses = if (selectedCategoryId != null) {
        allCourses.filter { it.first == selectedCategoryId }
    } else allCourses

    LazyColumn(
        contentPadding = PaddingValues(bottom = 32.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Greeting header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Welcome back 👋",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Find your next skill",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Avatar placeholder
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Teal700)
                    ) {
                        Text("SF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search courses, topics…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = DividerGray,
                        focusedIndicatorColor = Teal700
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Categories section
        item {
            SectionHeader(
                title = "Categories",
                actionLabel = "See all",
                onAction = {}
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategoryId == category.id,
                        onClick = {
                            selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                        }
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
        }

        // Popular courses section header
        item {
            SectionHeader(
                title = "Popular Courses",
                actionLabel = "See all",
                onAction = {}
            )
            Spacer(Modifier.height(12.dp))
        }

        // Course cards
        items(filteredCourses, key = { it.second.id }) { (categoryId, course) ->
            CourseCard(
                course = course,
                onClick = { onCourseClick(categoryId, course.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        TextButton(onClick = onAction) {
            Text(
                text = actionLabel,
                color = Teal700,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}
