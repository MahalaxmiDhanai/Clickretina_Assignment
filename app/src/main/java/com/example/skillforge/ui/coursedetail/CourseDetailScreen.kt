package com.example.skillforge.ui.coursedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.data.model.Course
import com.example.skillforge.theme.CardBorder
import com.example.skillforge.theme.Cream
import com.example.skillforge.theme.StarColor
import com.example.skillforge.theme.Teal100
import com.example.skillforge.theme.Teal700
import com.example.skillforge.theme.TextSecondary
import com.example.skillforge.ui.UiState
import com.example.skillforge.ui.components.ErrorView
import com.example.skillforge.ui.components.LessonRow
import com.example.skillforge.ui.components.LevelBadge
import com.example.skillforge.ui.components.LoadingView

@Composable
fun CourseDetailScreen(
    viewModel: CourseDetailViewModel,
    onBack: () -> Unit,
    onLessonClick: (categoryId: String, courseId: String, lessonId: String) -> Unit,
    categoryId: String
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(message = s.message, onRetry = viewModel::loadCourse)
            is UiState.Success -> {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    CourseDetailContent(
                        course = s.data,
                        categoryId = categoryId,
                        onBack = onBack,
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseDetailContent(
    course: Course,
    categoryId: String,
    onBack: () -> Unit,
    onLessonClick: (categoryId: String, courseId: String, lessonId: String) -> Unit
) {
    var isFollowing by remember { mutableStateOf(false) }
    val totalMinutes = course.lessons.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // Hero image + back button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }

        // Course info
        item {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                // Level badge + tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LevelBadge(level = course.level)
                    course.tags.take(2).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Teal100, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Teal700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = course.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(16.dp))

                // Stats row
                StatChipsRow(
                    rating = course.rating,
                    students = course.studentsEnrolled,
                    hours = course.durationHours,
                    level = course.level
                )

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(Modifier.height(20.dp))

                // Instructor card
                InstructorCard(
                    name = course.instructor.name,
                    title = course.instructor.title,
                    avatarUrl = course.instructor.avatarUrl,
                    isFollowing = isFollowing,
                    onFollowToggle = { isFollowing = !isFollowing }
                )

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = CardBorder)
                Spacer(Modifier.height(20.dp))

                // Description
                Text(
                    text = "About this course",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(24.dp))

                // Course content header
                Text(
                    text = "Course Content",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${course.lessons.size} lessons · $totalMinutes min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Lesson list
        items(course.lessons, key = { it.id }) { lesson ->
            LessonRow(
                lesson = lesson,
                onClick = { onLessonClick(categoryId, course.id, lesson.id) },
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 20.dp)
            )
            HorizontalDivider(
                color = CardBorder,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Bottom enroll CTA
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal700),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Enroll Now — Free",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChipsRow(
    rating: Double,
    students: Int,
    hours: Double,
    level: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Rating chip
        StatChip {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = StarColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = rating.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        // Duration chip
        StatChip {
            Text(
                text = "${hours}h",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        // Students chip
        StatChip {
            Text(
                text = "${formatCount(students)} students",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatChip(content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Teal100, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        content()
    }
}

@Composable
private fun InstructorCard(
    name: String,
    title: String,
    avatarUrl: String,
    isFollowing: Boolean,
    onFollowToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Teal100)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        if (isFollowing) {
            OutlinedButton(
                onClick = onFollowToggle,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Teal700)
            ) {
                Text("Following", color = Teal700, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        } else {
            Button(
                onClick = onFollowToggle,
                colors = ButtonDefaults.buttonColors(containerColor = Teal700),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Follow", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return if (count >= 1000) "${count / 1000}.${(count % 1000) / 100}k" else count.toString()
}
