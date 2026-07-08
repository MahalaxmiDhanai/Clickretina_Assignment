package com.clickretina.skillforge.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SkillforgeColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = SurfaceWhite,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal700,
    secondary = Teal500,
    onSecondary = SurfaceWhite,
    background = Cream,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = DividerGray,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = LevelAdvanced,
)

@Composable
fun SkillforgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SkillforgeColorScheme,
        typography = SkillforgeTypography,
        content = content
    )
}

