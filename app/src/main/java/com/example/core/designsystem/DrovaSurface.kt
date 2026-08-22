package com.example.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * Surface hierarchy style that prevents the generic "everything-is-a-card" look.
 * Replaces muddy Material 3 drop shadows with clean architectural borders,
 * intentional background canvas (#F8FAFC), and selective elevation for floating layers only.
 */
enum class DrovaSurfaceStyle {
    /**
     * Crisp flat surface (Pure #FFFFFF on #F8FAFC canvas) with a 1px border.
     * Ideal for list items, form panels, and content modules.
     */
    FLAT,

    /**
     * Seamless tonal container (#F1F5F9) without elevation or heavy borders.
     * Ideal for secondary sections, input containers, and background insets.
     */
    TONAL,

    /**
     * Pure background canvas (#F8FAFC) with no border or elevation.
     * Used for grouping items directly on the page without nesting cards.
     */
    CANVAS,

    /**
     * Ultra-subtle architectural border with transparent or translucent background.
     */
    OUTLINED,

    /**
     * Selective floating elevation (1.dp - 3.dp crisp shadow) reserved strictly
     * for floating action bars, sticky summary bars, contextual popovers, and bottom action sheets.
     */
    FLOATING,

    /**
     * High-authority dark charcoal (#0F172A) container for hero announcements,
     * status headers, and executive dashboards.
     */
    HERO_DEEP,

    /**
     * Branded turquoise light (#E6F7F5) container for active status milestones and highlight banners.
     */
    HERO_PRIMARY
}

/**
 * Reusable Drova Surface Component.
 *
 * Enforces the Drova visual design language:
 * - Clean off-white background canvas (#F8FAFC)
 * - 8.dp architectural geometric corners by default
 * - Razor-thin 1px border (#E2E8F0) in place of muddy shadows
 * - Intentional, selective elevation only for floating layers
 */
@Composable
fun DrovaSurface(
    modifier: Modifier = Modifier,
    style: DrovaSurfaceStyle = DrovaSurfaceStyle.FLAT,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    border: BorderStroke? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    testTag: String = "drova_surface",
    content: @Composable BoxScope.() -> Unit
) {
    val resolvedContainerColor = containerColor ?: when (style) {
        DrovaSurfaceStyle.FLAT -> DrovaSurface // #FFFFFF
        DrovaSurfaceStyle.TONAL -> DrovaSurfaceVariant // #F1F5F9
        DrovaSurfaceStyle.CANVAS -> DrovaBackground // #F8FAFC
        DrovaSurfaceStyle.OUTLINED -> Color.Transparent
        DrovaSurfaceStyle.FLOATING -> DrovaSurface // #FFFFFF
        DrovaSurfaceStyle.HERO_DEEP -> DrovaDeep // #0F172A
        DrovaSurfaceStyle.HERO_PRIMARY -> DrovaTurquoiseLight // #E6F7F5
    }

    val resolvedContentColor = contentColor ?: when (style) {
        DrovaSurfaceStyle.HERO_DEEP -> Color.White
        DrovaSurfaceStyle.HERO_PRIMARY -> DrovaTurquoiseHover
        else -> DrovaTextPrimary
    }

    val resolvedBorder = border ?: when (style) {
        DrovaSurfaceStyle.FLAT -> BorderStroke(1.dp, DrovaBorder)
        DrovaSurfaceStyle.OUTLINED -> BorderStroke(1.dp, DrovaBorder)
        DrovaSurfaceStyle.HERO_PRIMARY -> BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.35f))
        DrovaSurfaceStyle.FLOATING -> BorderStroke(1.dp, DrovaBorder.copy(alpha = 0.8f))
        DrovaSurfaceStyle.TONAL -> BorderStroke(1.dp, DrovaBorder.copy(alpha = 0.4f))
        DrovaSurfaceStyle.CANVAS,
        DrovaSurfaceStyle.HERO_DEEP -> null
    }

    val shadowModifier = if (style == DrovaSurfaceStyle.FLOATING) {
        Modifier.shadow(
            elevation = 3.dp,
            shape = shape,
            ambientColor = DrovaCharcoal.copy(alpha = 0.08f),
            spotColor = DrovaCharcoal.copy(alpha = 0.12f)
        )
    } else {
        Modifier
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = if (style == DrovaSurfaceStyle.HERO_DEEP) Color.White else DrovaTurquoise),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .testTag(testTag)
            .then(shadowModifier)
            .clip(shape)
            .then(clickableModifier),
        shape = shape,
        color = resolvedContainerColor,
        contentColor = resolvedContentColor,
        border = resolvedBorder,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Convenience Root Container for whole screens that guarantees the
 * #F8FAFC canvas background and proper status bar / edge-to-edge alignment.
 */
@Composable
fun DrovaScreenContainer(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    testTag: String = "drova_screen_container",
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DrovaBackground)
            .testTag(testTag)
    ) {
        if (topBar != null) {
            topBar()
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            content = content
        )

        if (bottomBar != null) {
            bottomBar()
        }
    }
}

/**
 * High-clarity Section Grouping Container.
 * Groups related items cleanly using architectural borders and subtle backgrounds
 * rather than high-elevation card stacks.
 */
@Composable
fun DrovaSectionContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    trailingAction: (@Composable () -> Unit)? = null,
    style: DrovaSurfaceStyle = DrovaSurfaceStyle.FLAT,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    testTag: String = "drova_section",
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null || trailingAction != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                }
                if (trailingAction != null) {
                    trailingAction()
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        DrovaSurface(
            modifier = Modifier.fillMaxWidth(),
            style = style,
            contentPadding = contentPadding,
            testTag = testTag
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}
