package com.example.cocoa_market_spa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cocoa_market_spa.ui.navigation.AppNavigation
import com.example.cocoa_market_spa.ui.theme.CocoamarketSPATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CocoamarketSPATheme {
                AppNavigation()
            }
        }
    }
}