package com.example.skillforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.data.model.Lesson
import com.example.skillforge.theme.FreePillBg
import com.example.skillforge.theme.FreePillText
import com.example.skillforge.theme.LockedPillBg
import com.example.skillforge.theme.LockedPillText
import com.example.skillforge.theme.Teal700
import com.example.skillforge.theme.TextSecondary

@Composable
fun LessonRow(
    lesson: Lesson,
    isCurrentLesson: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (isCurrentLesson) Teal700.copy(alpha = 0.06f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Play or Lock icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = if (isCurrentLesson) Teal700 else Teal700.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            if (lesson.isFree || isCurrentLesson) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = if (isCurrentLesson) Color.White else Teal700,
                    modifier = Modifier.padding(1.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Locked",
                    tint = TextSecondary
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrentLesson) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrentLesson) Teal700 else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isCurrentLesson) "Now playing · ${lesson.durationMinutes} min" else "${lesson.durationMinutes} min",
                style = MaterialTheme.typography.labelMedium,
                color = if (isCurrentLesson) Teal700.copy(alpha = 0.7f) else TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.width(8.dp))

        // FREE or LOCKED pill
        FreeLockPill(isFree = lesson.isFree)
    }
}

@Composable
fun FreeLockPill(isFree: Boolean, modifier: Modifier = Modifier) {
    val bg = if (isFree) FreePillBg else LockedPillBg
    val textColor = if (isFree) FreePillText else LockedPillText
    val label = if (isFree) "FREE" else "LOCKED"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = bg, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
