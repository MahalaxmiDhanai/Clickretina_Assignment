package com.example.skillforge.ui.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.theme.CardBorder
import com.example.skillforge.theme.Cream
import com.example.skillforge.theme.Teal700
import com.example.skillforge.theme.TextSecondary
import com.example.skillforge.ui.UiState
import com.example.skillforge.ui.components.ErrorView
import com.example.skillforge.ui.components.LessonRow
import com.example.skillforge.ui.components.LoadingView

@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val currentLessonId by viewModel.currentLessonId.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1720)) // Dark player background
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingView(modifier = Modifier.background(Cream))
            is UiState.Error -> ErrorView(
                message = s.message,
                onRetry = viewModel::loadLesson,
                modifier = Modifier.background(Cream)
            )
            is UiState.Success -> {
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    LessonContent(
                        data = s.data,
                        progress = progress,
                        currentLessonId = currentLessonId,
                        onBack = onBack,
                        onLessonSelect = viewModel::selectLesson
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContent(
    data: LessonUiData,
    progress: Float,
    currentLessonId: String,
    onBack: () -> Unit,
    onLessonSelect: (String) -> Unit
) {
    val lesson = data.currentLesson
    val course = data.course
    var isPlaying by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Notes", "Resources")

    // Elapsed/total mock timestamps
    val totalSeconds = lesson.durationMinutes * 60
    val elapsedSeconds = (totalSeconds * progress).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // ─── Video Player Section ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Color(0xFF0F1720))
                .statusBarsPadding()
        ) {
            // Thumbnail as video backdrop
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(0.5f), Color.Black.copy(0.7f))
                        )
                    )
            )
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            // Center play/pause button
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            // Progress bar + timestamps at the bottom of the player
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = Teal700,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = formatTime(elapsedSeconds),
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = formatTime(totalSeconds),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // ─── Content area (white) ──────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Cream)
        ) {
            // Lesson eyebrow + title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "LESSON ${course.lessons.indexOf(lesson) + 1} · ${course.title.uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }

            // ─── Tab Row ───────────────────────────────────────────────
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Teal700,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = Teal700,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            // ─── Tab Content ───────────────────────────────────────────
            when (selectedTab) {
                0 -> {
                    // Lessons list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(course.lessons, key = { it.id }) { l ->
                            LessonRow(
                                lesson = l,
                                isCurrentLesson = l.id == currentLessonId,
                                onClick = { onLessonSelect(l.id) },
                                modifier = Modifier.background(Color.White)
                            )
                            HorizontalDivider(
                                color = CardBorder,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
                1 -> PlaceholderTab("📝 No notes yet.\nTap the pencil icon during playback to add notes.")
                2 -> PlaceholderTab("📁 No resources attached to this lesson yet.")
            }
        }
    }
}

@Composable
private fun PlaceholderTab(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 24.sp
        )
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
