package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Menú principal: descripción de la app y accesos directos (ver/agregar) a los módulos Mascotas, Vacunas y Citas.
 * Incluye la barra de navegación inferior (ver [NavUtils]).
 */
class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        NavUtils.setup(this, R.id.nav_inicio)

        val btnVerMascotas = findViewById<Button>(R.id.btnVerMascotas)
        val btnAgregarMascota = findViewById<Button>(R.id.btnAgregarMascota)
        val btnVerVacunas = findViewById<Button>(R.id.btnVerVacunas)
        val btnAgregarVacuna = findViewById<Button>(R.id.btnAgregarVacuna)
        val btnVerCitas = findViewById<Button>(R.id.btnVerCitas)
        val btnAgregarCita = findViewById<Button>(R.id.btnAgregarCita)

        // Módulo Mascotas
        btnVerMascotas.setOnClickListener {
            val intent = Intent(this, MascotasActivity::class.java)
            startActivity(intent)
        }

        btnAgregarMascota.setOnClickListener {
            val intent = Intent(this, AgregarMascotaActivity::class.java)
            startActivity(intent)
        }

        // Módulo Vacunas
        btnVerVacunas.setOnClickListener {
            val intent = Intent(this, VacunasActivity::class.java)
            startActivity(intent)
        }

        btnAgregarVacuna.setOnClickListener {
            val intent = Intent(this, AgregarVacunaActivity::class.java)
            startActivity(intent)
        }

        // Módulo Citas (Agenda)
        btnVerCitas.setOnClickListener {
            val intent = Intent(this, CitasActivity::class.java)
            startActivity(intent)
        }

        btnAgregarCita.setOnClickListener {
            val intent = Intent(this, AgregarCitaActivity::class.java)
            startActivity(intent)
        }
    }
}
