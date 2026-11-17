package com.example.proyecto_1_pataaldia

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Configura la barra de navegación inferior (Inicio / Mascotas / Vacunas / Citas).
 * Cada pantalla principal la incluye con `<include layout="@layout/include_bottom_nav"/>`
 * y llama a [setup] indicando cuál pestaña le corresponde.
 */
object NavUtils {

    /** Marca [selectedId] como pestaña activa y navega a la pantalla elegida al tocar otra. */
    fun setup(activity: Activity, selectedId: Int) {
        val nav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.selectedItemId = selectedId
        nav.setOnItemSelectedListener { item ->
            if (item.itemId == selectedId) return@setOnItemSelectedListener true
            val destino = when (item.itemId) {
                R.id.nav_mascotas -> MascotasActivity::class.java
                R.id.nav_vacunas -> VacunasActivity::class.java
                R.id.nav_citas -> CitasActivity::class.java
                else -> InicioActivity::class.java
            }
            val intent = Intent(activity, destino)
            if (item.itemId == R.id.nav_inicio) {
                // Volver a Inicio: cierra las pantallas intermedias y reutiliza la que ya existe
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            activity.startActivity(intent)
            // Entre pestañas que no son Inicio se reemplaza la pantalla, así "Atrás" regresa a Inicio
            if (selectedId != R.id.nav_inicio && item.itemId != R.id.nav_inicio) activity.finish()
            activity.overridePendingTransition(0, 0)
            true
        }
    }
}
