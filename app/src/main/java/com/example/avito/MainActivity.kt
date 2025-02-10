package com.example.avito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.avito.start.StartScreen
import com.example.avito.ui.theme.AvitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTheme {
                val navController = rememberNavController()
                StartScreen(navController = navController)
            }
        }
    }
}