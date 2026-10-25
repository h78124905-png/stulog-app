package com.yourname.stulogplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yourname.stulogplus.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StulogTheme { AppNavigation() } }
    }
}

@Composable
private fun StulogTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme(
        primary = Color(0xFFC4B5FD), secondary = Color(0xFF60A5FA),
        background = Color(0xFF141319), surface = Color(0xFF232228)
    ) else lightColorScheme(
        primary = Color(0xFF8B5CF6), secondary = Color(0xFF3B82F6),
        background = Color(0xFFFBFAFF), surface = Color.White
    )
    MaterialTheme(colorScheme = colors, content = content)
}
