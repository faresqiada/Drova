package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp

/**
 * DROVA Brand Typography System
 *
 * Engineered specifically for high-contrast bilingual (Arabic / English) commerce & logistics:
 * 1. Rejects generic Material 3 sizing (which produces timid headlines and cramped Arabic lines).
 * 2. High Line-Height Ratio (1.35x - 1.5x) to accommodate Arabic diacritics, tall ascenders (أ, ل, ط)
 *    and deep descenders (ي, ع, ر) without vertical truncation.
 * 3. Zero/Neutral Letter Spacing (0.sp) across Arabic heading & body tokens to maintain continuous
 *    cursive ligature flow without unnatural glyph decoupling.
 * 4. Architectural Weight Steps (Black, ExtraBold, Bold, SemiBold, Medium) providing authoritative
 *    visual hierarchy for real-time delivery operations, order statuses, prices, and metrics.
 */

// ============================================================================
// Specialized Typographic Tokens for DROVA Commerce & Logistics
// ============================================================================

@Immutable
data class DrovaSpecializedTypography(
    val heroDisplay: TextStyle,
    val sectionHeader: TextStyle,
    val kpiMetric: TextStyle,
    val kpiLabel: TextStyle,
    val priceLarge: TextStyle,
    val priceMedium: TextStyle,
    val priceCurrency: TextStyle,
    val statusBadge: TextStyle,
    val stepIndex: TextStyle,
    val inputLabel: TextStyle,
    val buttonBold: TextStyle
)

val DrovaCustomTextTokens = DrovaSpecializedTypography(
    heroDisplay = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp,
        color = DrovaDeep
    ),
    sectionHeader = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = DrovaDeep
    ),
    kpiMetric = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp,
        color = DrovaDeep
    ),
    kpiLabel = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.2.sp,
        color = DrovaTextSecondary
    ),
    priceLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.5).sp,
        color = DrovaPrimary
    ),
    priceMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = DrovaPrimary
    ),
    priceCurrency = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        color = DrovaTextSecondary
    ),
    statusBadge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.2.sp
    ),
    stepIndex = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = DrovaPrimary
    ),
    inputLabel = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        color = DrovaTextSecondary
    ),
    buttonBold = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.3.sp
    )
)

val LocalDrovaSpecializedTypography = staticCompositionLocalOf { DrovaCustomTextTokens }

// ============================================================================
// Core Material 3 Typography Scale
// ============================================================================

val DrovaTypography = Typography(
    // Large prominent hero banners & splash screen headers
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Screen title headlines & primary welcome headers
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Modal & sheet display headers
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),

    // Prominent section titles (e.g., Active Orders, Live Tracking)
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Module titles & restaurant store headings
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Card group headers & sub-section banners
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),

    // Card titles, product names & menu item titles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Top bar titles & list item titles
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Sub-titles & metadata headers
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),

    // Primary reading text & long descriptions
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = DrovaTextPrimary
    ),
    // Standard secondary text, helper texts & order item details
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
        color = DrovaTextSecondary
    ),
    // Captions, timestamps & disclaimer footers
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
        color = DrovaTextMuted
    ),

    // Primary CTA buttons & action labels
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    // Status pills, role tags & filter chips
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    // Fine-print metadata badges & micro tags
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)

val Typography = DrovaTypography
