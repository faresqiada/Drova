package com.example.core.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.OrderStatus
import com.example.domain.model.UserRole
import com.example.ui.theme.*

/**
 * Solid, confident primary button with architectural 8.dp radius
 * and sharp high-contrast typography.
 */
@Composable
fun DrovaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DrovaTurquoise,
            contentColor = Color.White,
            disabledContainerColor = DrovaBorder,
            disabledContentColor = DrovaTextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.4.sp
                    )
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

/**
 * Deep slate / charcoal secondary button for bold complementary actions.
 */
@Composable
fun DrovaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DrovaCharcoal,
            contentColor = Color.White,
            disabledContainerColor = DrovaBorder,
            disabledContentColor = DrovaTextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.4.sp
                )
            )
        }
    }
}

/**
 * Crisp 1px bordered button for tertiary actions.
 */
@Composable
fun DrovaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "outlined_button"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, DrovaBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DrovaTextPrimary,
            disabledContentColor = DrovaTextMuted
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = DrovaTurquoise
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

/**
 * Architectural, high-precision text input field.
 */
@Composable
fun DrovaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    testTag: String = "text_field"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isError) DrovaError else DrovaTextSecondary,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp, start = 1.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DrovaTextMuted,
                            fontSize = 13.sp
                        )
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = DrovaTextPrimary,
                fontSize = 14.sp
            ),
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isError) DrovaError else DrovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else null,
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.testTag("${testTag}_toggle_pwd")
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                            tint = DrovaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            isError = isError,
            singleLine = singleLine,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DrovaTurquoise,
                unfocusedBorderColor = DrovaBorder,
                errorBorderColor = DrovaError,
                focusedContainerColor = DrovaSurface,
                unfocusedContainerColor = DrovaSurface,
                errorContainerColor = DrovaSurface,
                cursorColor = DrovaTurquoise
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )

        AnimatedVisibility(visible = isError && errorMessage != null) {
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DrovaError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )
        }
    }
}

/**
 * Clean architectural flat card with 1px border. No muddy drop shadows.
 */
@Composable
fun DrovaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = DrovaBorder,
    containerColor: Color = DrovaSurface,
    contentPadding: Dp = 16.dp,
    testTag: String = "drova_card",
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Compact, high-contrast operational status tag.
 */
@Composable
fun DrovaStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status) {
        OrderStatus.CREATED -> Triple(Color(0xFFF1F5F9), DrovaCharcoal, DrovaBorder)
        OrderStatus.RESTAURANT_CONFIRMED -> Triple(DrovaTurquoiseLight, DrovaTurquoiseHover, DrovaTurquoise)
        OrderStatus.PREPARING -> Triple(Color(0xFFFEF3C7), DrovaWarningText, DrovaWarning)
        OrderStatus.READY_FOR_PICKUP -> Triple(DrovaTurquoiseContainer, DrovaDeepTeal, DrovaTurquoise)
        OrderStatus.CAPTAIN_ASSIGNED, OrderStatus.PICKED_UP -> Triple(Color(0xFFE0F2FE), DrovaInfoText, DrovaInfo)
        OrderStatus.ON_THE_WAY -> Triple(DrovaTurquoiseLight, DrovaTurquoiseHover, DrovaTurquoise)
        OrderStatus.DELIVERED, OrderStatus.COMPLETED -> Triple(Color(0xFFDCFCE7), DrovaSuccessText, DrovaSuccess)
        OrderStatus.CANCELLED, OrderStatus.REJECTED -> Triple(Color(0xFFFEE2E2), DrovaErrorText, DrovaError)
    }

    Surface(
        modifier = modifier.testTag("status_badge_${status.name}"),
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.4f))
    ) {
        Text(
            text = status.titleAr,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 11.sp,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * Sharp role indicator badge with high visual authority.
 */
@Composable
fun DrovaRoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, border) = when (role) {
        UserRole.CUSTOMER -> Triple(DrovaTurquoiseLight, DrovaTurquoiseHover, DrovaTurquoise.copy(alpha = 0.4f))
        UserRole.RESTAURANT -> Triple(DrovaCharcoal, Color.White, DrovaCharcoal)
        UserRole.CAPTAIN -> Triple(Color(0xFFFEF3C7), DrovaWarningText, DrovaWarning.copy(alpha = 0.5f))
    }

    Surface(
        modifier = modifier.testTag("role_badge_${role.name}"),
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = role.titleAr,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 11.sp,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * Top navigation bar with crisp bottom border and logo mark.
 */
@Composable
fun DrovaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    showLanguageToggle: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("drova_top_bar"),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = DrovaTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                DrovaMark(size = 24.dp)
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary,
                        fontSize = 15.sp
                    ),
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trailingContent != null) {
                    trailingContent()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (showLanguageToggle) {
                    DrovaLanguageToggle()
                }
            }
        }
    }
}

/**
 * Minimal language switcher button.
 */
@Composable
fun DrovaLanguageToggle(modifier: Modifier = Modifier) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { DrovaLanguageManager.toggleLanguage() }
            .testTag("language_toggle_button"),
        color = DrovaSurfaceVariant,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = DrovaTurquoise
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isAr) "EN" else "عربي",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaTextPrimary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
