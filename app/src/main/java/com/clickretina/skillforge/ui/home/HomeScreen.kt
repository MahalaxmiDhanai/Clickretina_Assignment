package com.clickretina.skillforge.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.skillforge.data.model.Category
import com.clickretina.skillforge.theme.Cream
import com.clickretina.skillforge.theme.Teal700
import com.clickretina.skillforge.ui.UiState
import com.clickretina.skillforge.ui.components.CategoryChip
import com.clickretina.skillforge.ui.components.CourseCard
import com.clickretina.skillforge.ui.components.ErrorView
import com.clickretina.skillforge.ui.components.LoadingView

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
            is UiState.Error   -> ErrorView(message = s.message, onRetry = viewModel::loadCatalog)
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
    val filteredCourses = if (selectedCategoryId != null)
        allCourses.filter { it.first == selectedCategoryId }
    else allCourses

    LazyColumn(
        contentPadding = PaddingValues(bottom = 40.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ─── Header ───────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .background(Color(0xF5FBFAF8))
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp, bottom = 14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Welcome back",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9A9890)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Find your next skill",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    // Bell + Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFECEBE6), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF5C5C54),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Teal700)
                        ) {
                            Text(
                                "AS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search courses, topics…",
                            fontSize = 15.sp,
                            color = Color(0xFFA6A49C)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFA6A49C),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor   = Color.White,
                        unfocusedIndicatorColor = Color(0xFFECEBE6),
                        focusedIndicatorColor   = Teal700
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // ─── Categories ────────────────────────────────────────────────────
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Categories",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0FB5A4),
                    modifier = Modifier.clickable { selectedCategoryId = null }
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategoryId == category.id,
                        onClick = {
                            selectedCategoryId =
                                if (selectedCategoryId == category.id) null else category.id
                        }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        // ─── Popular Courses ───────────────────────────────────────────────
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Popular courses",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0FB5A4),
                    modifier = Modifier.clickable { selectedCategoryId = null }
                )
            }
        }

        items(filteredCourses, key = { it.second.id }) { (categoryId, course) ->
            CourseCard(
                course = course,
                onClick = { onCourseClick(categoryId, course.id) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

