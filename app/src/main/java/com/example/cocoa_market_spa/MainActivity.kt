
package com.example.cocoa_market_spa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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

        // Mantiene la navegacion actual de Cocoa Market
        setContent {
            CocoamarketSPATheme {
                AppNavigation()
            }
        }

        // Android 13 o superior necesita permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // Solicita permiso una vez, cuando sea necesario
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}
