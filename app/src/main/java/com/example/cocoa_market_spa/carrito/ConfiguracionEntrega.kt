
package com.example.cocoa_market_spa.carrito

// Configuracion temporal de los despachos del minimarket.
object ConfiguracionEntrega {

    // Cantidad de despachos realizados durante la semana.
    const val DESPACHOS_POR_SEMANA = 2

    // null significa que la tarifa aun no esta confirmada.
    // Cuando se defina, reemplazar null por el valor en pesos.
    // Ejemplo: const val COSTO_DESPACHO = 2500
    val COSTO_DESPACHO: Int? = null
}
