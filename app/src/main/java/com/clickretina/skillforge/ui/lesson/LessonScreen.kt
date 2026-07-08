package com.clickretina.skillforge.ui.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.clickretina.skillforge.theme.CardBorder
import com.clickretina.skillforge.theme.Cream
import com.clickretina.skillforge.theme.Teal700
import com.clickretina.skillforge.theme.TextSecondary
import com.clickretina.skillforge.ui.UiState
import com.clickretina.skillforge.ui.components.ErrorView
import com.clickretina.skillforge.ui.components.LessonRow
import com.clickretina.skillforge.ui.components.LoadingView
import kotlinx.coroutines.delay

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
            .background(Color(0xFF0F1720))
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingView(modifier = Modifier.background(Cream))
            is UiState.Error   -> ErrorView(
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
                        onProgressUpdate = viewModel::updateProgress,
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
    onProgressUpdate: (Float) -> Unit,
    onLessonSelect: (String) -> Unit
) {
    val lesson = data.currentLesson
    val course = data.course
    var isPlaying by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lessons", "Notes", "Resources")

    // Simulate playback: increment progress every second while playing
    LaunchedEffect(isPlaying, currentLessonId) {
        if (isPlaying) {
            while (isPlaying && progress < 1f) {
                delay(1000L)
                val next = (progress + 1f / (lesson.durationMinutes * 60f)).coerceAtMost(1f)
                onProgressUpdate(next)
                if (next >= 1f) { isPlaying = false; break }
            }
        }
    }

    val totalSeconds = lesson.durationMinutes * 60
    val elapsedSeconds = (totalSeconds * progress).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // ─── Video Player ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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
            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(0.45f), Color.Black.copy(0.75f))
                        )
                    )
            )
            // Back button
            IconButton(
                onClick = {
                    isPlaying = false
                    onBack()
                },
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
            // Center Play / Pause button
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            // Progress bar + timestamps
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF2DD4BF),
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(formatTime(elapsedSeconds), color = Color.White, fontSize = 11.sp)
                    Spacer(Modifier.weight(1f))
                    Text(formatTime(totalSeconds), color = Color.White.copy(0.6f), fontSize = 11.sp)
                }
            }
        }

        // ─── Content Area ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Cream)
        ) {
            // Eyebrow + title + description
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
                    lineHeight = 22.sp,
                    color = Color(0xFF4B5563)
                )
            }

            // ─── Tabs ───────────────────────────────────────────────────────
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Teal700
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

            // ─── Tab Content ────────────────────────────────────────────────
            when (selectedTab) {
                0 -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(course.lessons, key = { it.id }) { l ->
                            LessonRow(
                                lesson = l,
                                isCurrentLesson = l.id == currentLessonId,
                                onClick = {
                                    isPlaying = false
                                    onLessonSelect(l.id)
                                },
                                modifier = Modifier.background(Color.White)
                            )
                            HorizontalDivider(
                                color = CardBorder,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
                1 -> PlaceholderTab("📝  No notes yet.\nTap play and jot notes while the lesson runs.")
                2 -> PlaceholderTab("📁  No resources attached to this lesson.")
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

