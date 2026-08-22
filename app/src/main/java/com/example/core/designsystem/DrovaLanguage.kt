package com.example.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val labelAr: String, val labelEn: String, val isRtl: Boolean) {
    ARABIC("ar", "العربية", "Arabic", true),
    ENGLISH("en", "English", "الإنجليزية", false)
}

object DrovaLanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.ARABIC)

    fun toggleLanguage() {
        currentLanguage = if (currentLanguage == AppLanguage.ARABIC) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.ARABIC
        }
    }

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }
}

@Composable
fun DrovaRtlProvider(
    language: AppLanguage = DrovaLanguageManager.currentLanguage,
    content: @Composable () -> Unit
) {
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        content()
    }
}
