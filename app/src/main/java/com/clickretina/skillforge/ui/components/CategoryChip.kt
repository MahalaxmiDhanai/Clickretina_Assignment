package com.clickretina.skillforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.skillforge.data.model.Category

// Parses a hex color string like "#2dd4bf" to Color
private fun parseHexColor(hex: String): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val argb = ("FF$cleaned").toLong(16)
        Color(argb.toInt())
    } catch (e: Exception) {
        Color(0xFF2DD4BF)
    }
}

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = parseHexColor(category.iconColor)
    val iconBg = iconColor.copy(alpha = 0.16f)
    val cardBg = if (isSelected) iconColor.copy(alpha = 0.12f) else Color.White
    val borderColor = if (isSelected) iconColor else Color(0xFFECEBE6)

    Box(
        modifier = modifier
            .size(width = 148.dp, height = 148.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            // Colored icon square
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(iconColor)
                        .align(androidx.compose.ui.Alignment.Center)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
                color = Color(0xFF1A1A1A),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${category.courseCount} courses",
                fontSize = 12.sp,
                color = Color(0xFF9A9890)
            )
        }
    }
}

