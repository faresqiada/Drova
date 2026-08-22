package com.example.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Approved DROVA Mark:
 * Precision reproduction of the aerodynamic "D" symbol with top-left wing spur
 * and inner dynamic flow contour.
 */
@Composable
fun DrovaMark(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = DrovaPrimary,
    innerTint: Color = DrovaAccent
) {
    Canvas(
        modifier = modifier
            .size(size)
            .testTag("drova_logo_mark")
    ) {
        val w = this.size.width
        val h = this.size.height

        // Outer aerodynamic D ribbon with top-left beak
        val outerPath = Path().apply {
            moveTo(w * 0.24f, h * 0.28f)
            lineTo(w * 0.58f, h * 0.28f)
            cubicTo(
                w * 0.74f, h * 0.28f,
                w * 0.84f, h * 0.38f,
                w * 0.84f, h * 0.54f
            )
            cubicTo(
                w * 0.84f, h * 0.70f,
                w * 0.74f, h * 0.80f,
                w * 0.58f, h * 0.80f
            )
            lineTo(w * 0.32f, h * 0.80f)
            cubicTo(
                w * 0.26f, h * 0.80f,
                w * 0.22f, h * 0.74f,
                w * 0.25f, h * 0.68f
            )
            cubicTo(
                w * 0.27f, h * 0.64f,
                w * 0.32f, h * 0.64f,
                w * 0.36f, h * 0.64f
            )
            lineTo(w * 0.56f, h * 0.64f)
            cubicTo(
                w * 0.65f, h * 0.64f,
                w * 0.72f, h * 0.59f,
                w * 0.72f, h * 0.54f
            )
            cubicTo(
                w * 0.72f, h * 0.49f,
                w * 0.65f, h * 0.44f,
                w * 0.56f, h * 0.44f
            )
            lineTo(w * 0.42f, h * 0.44f)
            close()
        }

        drawPath(
            path = outerPath,
            color = tint,
            style = Fill
        )

        // Inner dynamic flow blade
        val innerPath = Path().apply {
            moveTo(w * 0.25f, h * 0.70f)
            cubicTo(
                w * 0.27f, h * 0.52f,
                w * 0.42f, h * 0.42f,
                w * 0.62f, h * 0.42f
            )
            cubicTo(
                w * 0.56f, h * 0.50f,
                w * 0.46f, h * 0.56f,
                w * 0.38f, h * 0.68f
            )
            cubicTo(
                w * 0.34f, h * 0.75f,
                w * 0.27f, h * 0.76f,
                w * 0.25f, h * 0.70f
            )
            close()
        }

        drawPath(
            path = innerPath,
            color = innerTint,
            style = Fill
        )
    }
}

@Composable
fun DrovaBrandLogo(
    modifier: Modifier = Modifier,
    markSize: Dp = 56.dp,
    showTagline: Boolean = true,
    isDarkBackground: Boolean = false,
    taglineArabic: String = "منظومة التوصيل الذكية",
    taglineEnglish: String = "Smart Delivery Ecosystem"
) {
    val primaryTextColor = if (isDarkBackground) Color.White else DrovaTextPrimary
    val subTextColor = if (isDarkBackground) Color(0xFF94A3B8) else DrovaTextMuted
    val markTint = if (isDarkBackground) Color.White else DrovaDeep
    val markInner = if (isDarkBackground) DrovaAccent else DrovaPrimary

    Column(
        modifier = modifier.testTag("drova_brand_logo"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DrovaMark(
            size = markSize,
            tint = markTint,
            innerTint = markInner
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "DROVA",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                fontSize = if (markSize > 64.dp) 34.sp else 26.sp,
                color = primaryTextColor
            )
        )

        if (showTagline) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = taglineArabic,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DrovaPrimary
                )
            )
            Text(
                text = taglineEnglish.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.5.sp,
                    fontSize = 10.sp,
                    color = subTextColor
                )
            )
        }
    }
}
