package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.core.designsystem.DrovaRtlProvider
import com.example.core.di.ServiceLocator
import com.example.presentation.navigation.DrovaNavHost
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            DrovaTheme {
                DrovaRtlProvider {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = DrovaBackground
                    ) {
                        DrovaNavHost()
                    }
                }
            }
        }
    }
}
