
package com.example.cocoa_market_spa.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.example.cocoa_market_spa.MainActivity
import java.util.concurrent.atomic.AtomicInteger

object Notificaciones {

    // Identificador del canal de notificaciones
    private const val CANAL_ID = "avisos_cocoa_market"

    // Evita que una notificacion reemplace a otra
    private val contador = AtomicInteger(1000)

    // Muestra una notificacion local del telefono
    @SuppressLint("MissingPermission")
    fun mostrar(
        context: Context,
        titulo: String,
        mensaje: String
    ): Boolean {

        try {

            // Verifica el permiso en Android 13 o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (
                    context.checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

            val administrador = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            // Android 8 o superior requiere un canal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val canal = NotificationChannel(
                    CANAL_ID,
                    "Avisos Cocoa Market",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                canal.description =
                    "Pedidos y alertas de inventario del minimarket"

                // El sonido de confirmacion ya lo maneja la app
                canal.setSound(null, null)

                administrador.createNotificationChannel(canal)
            }

            // Permite regresar a la aplicacion al tocar el aviso
            val intent = Intent(
                context,
                MainActivity::class.java
            )

            val abrirAplicacion = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            // Crea la notificacion segun la version de Android
            val notificacion = if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ) {

                Notification.Builder(context, CANAL_ID)

            } else {

                Notification.Builder(context)
            }

            notificacion
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setContentIntent(abrirAplicacion)
                .setAutoCancel(true)

            // Publica la notificacion
            administrador.notify(
                contador.incrementAndGet(),
                notificacion.build()
            )

            return true

        } catch (e: Exception) {

            // Un fallo en las notificaciones no afecta la compra
            return false
        }
    }
}
