package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla principal del Módulo de Mascotas.
 * Consulta y muestra todos los registros de mascotas almacenados en SQLite dentro de un ListView,
 * actualizándose automáticamente en onResume() y permitiendo navegar al detalle o al formulario de registro.
 */
class MascotasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var lvMascotas: ListView
    private lateinit var tvSinMascotas: TextView
    private lateinit var btnIrAgregarMascota: Button

    // Lista en memoria sincronizada con las posiciones del ListView
    private var listaMascotas: List<Mascota> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascotas)

        // Inicialización del helper de SQLite y referencias UI
        dbHelper = DatabaseHelper(this)
        lvMascotas = findViewById(R.id.lvMascotas)
        tvSinMascotas = findViewById(R.id.tvSinMascotas)
        btnIrAgregarMascota = findViewById(R.id.btnIrAgregarMascota)
        NavUtils.setup(this, R.id.nav_mascotas)

        // Botón para abrir el formulario de registro de nueva mascota
        btnIrAgregarMascota.setOnClickListener {
            val intent = Intent(this, AgregarMascotaActivity::class.java)
            startActivity(intent)
        }

        // Evento al hacer clic en un elemento del ListView: abre DetalleMascotaActivity enviando el ID
        lvMascotas.setOnItemClickListener { _, _, position, _ ->
            val mascotaSeleccionada = listaMascotas[position]
            val intent = Intent(this, DetalleMascotaActivity::class.java).apply {
                putExtra("MASCOTA_ID", mascotaSeleccionada.id)
            }
            startActivity(intent)
        }

        // Carga inicial de datos
        cargarListaMascotas()
    }

    /**
     * Se ejecuta cada vez que la pantalla vuelve a primer plano (por ejemplo,
     * después de agregar, editar o eliminar una mascota), refrescando el ListView.
     */
    override fun onResume() {
        super.onResume()
        cargarListaMascotas()
    }

    /**
     * Consulta las mascotas en SQLite mediante DatabaseHelper y actualiza el adaptador del ListView.
     */
    private fun cargarListaMascotas() {
        listaMascotas = dbHelper.obtenerMascotas()

        if (listaMascotas.isEmpty()) {
            tvSinMascotas.visibility = View.VISIBLE
            lvMascotas.visibility = View.GONE
        } else {
            tvSinMascotas.visibility = View.GONE
            lvMascotas.visibility = View.VISIBLE

            // Formateo claro de cada registro para su visualización en el ListView
            val datosParaMostrar = listaMascotas.map { mascota ->
                "🐾 ${mascota.nombre} (${mascota.especie})\nEdad: ${mascota.edad} años | Peso: ${mascota.peso} kg"
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                datosParaMostrar
            )
            lvMascotas.adapter = adapter
        }
    }
}
