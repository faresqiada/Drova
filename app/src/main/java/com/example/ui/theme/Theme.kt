package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * DROVA Brand Color Tokens
 * - Primary: #00A896 (Vibrant Turquoise / Teal brand hero)
 * - Accent: #02C39A (High-energy Mint / Cyan accent)
 * - Deep: #0F172A (Architectural Slate / Charcoal foundation)
 * - Background: #F8FAFC (Ultra-clean modern off-white canvas)
 */
@Immutable
data class DrovaCustomColors(
    val primary: Color,
    val accent: Color,
    val deep: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderFocused: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primaryHover: Color,
    val primaryLight: Color,
    val primaryContainer: Color,
    val success: Color,
    val successContainer: Color,
    val successText: Color,
    val warning: Color,
    val warningContainer: Color,
    val warningText: Color,
    val error: Color,
    val errorContainer: Color,
    val errorText: Color,
    val info: Color,
    val infoContainer: Color,
    val infoText: Color
)

val DrovaLightColors = DrovaCustomColors(
    primary = DrovaPrimary,               // #00A896
    accent = DrovaAccent,                 // #02C39A
    deep = DrovaDeep,                     // #0F172A
    background = DrovaBackground,         // #F8FAFC
    surface = DrovaSurface,               // #FFFFFF
    surfaceVariant = DrovaSurfaceVariant, // #F1F5F9
    border = DrovaBorder,                 // #E2E8F0
    borderFocused = DrovaBorderFocused,   // #00A896
    textPrimary = DrovaTextPrimary,       // #0F172A
    textSecondary = DrovaTextSecondary,   // #64748B
    textMuted = DrovaTextMuted,           // #94A3B8
    primaryHover = DrovaTurquoiseHover,   // #028090
    primaryLight = DrovaTurquoiseLight,   // #E6F7F5
    primaryContainer = DrovaTurquoiseContainer, // #CCF2ED
    success = DrovaSuccess,               // #10B981
    successContainer = DrovaSuccessContainer,
    successText = DrovaSuccessText,
    warning = DrovaWarning,               // #F59E0B
    warningContainer = DrovaWarningContainer,
    warningText = DrovaWarningText,
    error = DrovaError,                   // #EF4444
    errorContainer = DrovaErrorContainer,
    errorText = DrovaErrorText,
    info = DrovaInfo,                     // #0284C7
    infoContainer = DrovaInfoContainer,
    infoText = DrovaInfoText
)

val DrovaDarkColors = DrovaCustomColors(
    primary = DrovaPrimary,
    accent = DrovaAccent,
    deep = DrovaDeep,
    background = DrovaDeep,
    surface = DrovaCharcoalLight,
    surfaceVariant = DrovaCharcoalMuted,
    border = DrovaCharcoalMuted,
    borderFocused = DrovaPrimary,
    textPrimary = DrovaSurface,
    textSecondary = DrovaTextMuted,
    textMuted = DrovaTextSecondary,
    primaryHover = DrovaTurquoiseHover,
    primaryLight = DrovaCharcoalLight,
    primaryContainer = DrovaDeepTeal,
    success = DrovaSuccess,
    successContainer = Color(0xFF064E3B),
    successText = Color(0xFF6EE7B7),
    warning = DrovaWarning,
    warningContainer = Color(0xFF78350F),
    warningText = Color(0xFFFDE68A),
    error = DrovaError,
    errorContainer = Color(0xFF7F1D1D),
    errorText = Color(0xFFFECACA),
    info = DrovaInfo,
    infoContainer = Color(0xFF0C4A6E),
    infoText = Color(0xFFBAE6FD)
)

val LocalDrovaColors = staticCompositionLocalOf { DrovaLightColors }

// ============================================================================
// Material 3 ColorScheme Semantic Extensions for DROVA Brand
// ============================================================================

val ColorScheme.drovaPrimary: Color
    get() = primary

val ColorScheme.drovaAccent: Color
    get() = secondary

val ColorScheme.drovaDeep: Color
    get() = tertiary

val ColorScheme.drovaBackground: Color
    get() = background

val ColorScheme.drovaSurface: Color
    get() = surface

val ColorScheme.drovaSurfaceVariant: Color
    get() = surfaceVariant

val ColorScheme.drovaBorder: Color
    get() = outline

val ColorScheme.drovaTextPrimary: Color
    get() = onBackground

val ColorScheme.drovaTextSecondary: Color
    get() = onSurfaceVariant

val ColorScheme.drovaSuccess: Color
    get() = DrovaSuccess

val ColorScheme.drovaWarning: Color
    get() = DrovaWarning

val ColorScheme.drovaError: Color
    get() = error

val ColorScheme.drovaInfo: Color
    get() = DrovaInfo

/**
 * Accessor for DrovaCustomColors via MaterialTheme
 */
val MaterialTheme.drovaColors: DrovaCustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalDrovaColors.current

/**
 * Accessor for DrovaSpecializedTypography via MaterialTheme
 */
val MaterialTheme.drovaTypography: DrovaSpecializedTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalDrovaSpecializedTypography.current

/**
 * Direct theme accessor object
 */
object DrovaAppTheme {
    val colors: DrovaCustomColors
        @Composable
        @ReadOnlyComposable
        get() = LocalDrovaColors.current

    val typography: DrovaSpecializedTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalDrovaSpecializedTypography.current
}

// ============================================================================
// Material 3 ColorScheme Definitions
// Primary: #00A896 | Accent: #02C39A | Deep: #0F172A | Background: #F8FAFC
// ============================================================================

val DrovaLightColorScheme = lightColorScheme(
    primary = DrovaPrimary,                      // #00A896
    onPrimary = Color.White,
    primaryContainer = DrovaTurquoiseLight,
    onPrimaryContainer = DrovaTurquoiseHover,
    secondary = DrovaAccent,                     // #02C39A
    onSecondary = DrovaDeep,
    secondaryContainer = DrovaTurquoiseContainer,
    onSecondaryContainer = DrovaDeep,
    tertiary = DrovaDeep,                        // #0F172A
    onTertiary = Color.White,
    tertiaryContainer = DrovaCharcoalLight,
    onTertiaryContainer = Color.White,
    background = DrovaBackground,                // #F8FAFC
    onBackground = DrovaTextPrimary,             // #0F172A
    surface = DrovaSurface,                      // #FFFFFF
    onSurface = DrovaTextPrimary,                // #0F172A
    surfaceVariant = DrovaSurfaceVariant,        // #F1F5F9
    onSurfaceVariant = DrovaTextSecondary,       // #64748B
    surfaceTint = DrovaPrimary,
    outline = DrovaBorder,                       // #E2E8F0
    outlineVariant = DrovaBorder,
    error = DrovaError,                          // #EF4444
    onError = Color.White,
    errorContainer = DrovaErrorContainer,        // #FEE2E2
    onErrorContainer = DrovaErrorText,           // #B91C1C
    scrim = DrovaDeep
)

val DrovaDarkColorScheme = darkColorScheme(
    primary = DrovaPrimary,
    onPrimary = DrovaDeep,
    primaryContainer = DrovaDeepTeal,
    onPrimaryContainer = DrovaTurquoiseLight,
    secondary = DrovaAccent,
    onSecondary = DrovaDeep,
    secondaryContainer = DrovaCharcoalLight,
    onSecondaryContainer = DrovaAccent,
    tertiary = DrovaDeep,
    onTertiary = Color.White,
    tertiaryContainer = DrovaCharcoalMuted,
    onTertiaryContainer = Color.White,
    background = DrovaDeep,
    onBackground = DrovaSurface,
    surface = DrovaCharcoalLight,
    onSurface = DrovaSurface,
    surfaceVariant = DrovaCharcoalMuted,
    onSurfaceVariant = DrovaTextSecondary,
    surfaceTint = DrovaPrimary,
    outline = DrovaCharcoalMuted,
    outlineVariant = DrovaCharcoalLight,
    error = DrovaError,
    onError = Color.White,
    errorContainer = DrovaErrorContainer,
    onErrorContainer = DrovaErrorText,
    scrim = Color.Black
)

// ============================================================================
// Theme Composable Provider
// ============================================================================

@Composable
fun DrovaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false to preserve DROVA brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DrovaDarkColorScheme
        else -> DrovaLightColorScheme
    }

    val customColors = if (darkTheme) DrovaDarkColors else DrovaLightColors

    CompositionLocalProvider(
        LocalDrovaColors provides customColors,
        LocalDrovaSpecializedTypography provides DrovaCustomTextTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = DrovaTypography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    DrovaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
