package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de agenda: muestra en un ListView las citas veterinarias guardadas en SQLite. */
class CitasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: CitaAdapter
    private lateinit var tvResumen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citas)
        NavUtils.setup(this, R.id.nav_citas)
        findViewById<View>(R.id.citasRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        adapter = CitaAdapter(this, mutableListOf())
        tvResumen = findViewById(R.id.tvResumenCitas)

        val lvCitas = findViewById<ListView>(R.id.lvCitas)
        lvCitas.adapter = adapter
        lvCitas.emptyView = findViewById<TextView>(R.id.tvCitasVacio)

        // Seleccionar una cita abre su detalle.
        lvCitas.setOnItemClickListener { _, _, posicion, _ ->
            val cita = adapter.getItem(posicion) ?: return@setOnItemClickListener
            val intent = Intent(this, DetalleCitaActivity::class.java)
            intent.putExtra(Cita.EXTRA_ID, cita.id)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAgregarCitaLista).setOnClickListener {
            startActivity(Intent(this, AgregarCitaActivity::class.java))
        }
    }

    // Se recarga cada vez que la pantalla vuelve a primer plano (tras agregar, editar o cancelar).
    override fun onResume() {
        super.onResume()
        cargarCitas()
    }

    /**
     * Muestra primero las citas próximas (de la más cercana a la más lejana)
     * y al final las que ya pasaron (la más reciente primero).
     */
    private fun cargarCitas() {
        val (pasadas, proximas) = dbHelper.obtenerCitas().partition { citaYaPaso(it.fechaHora) }

        adapter.clear()
        adapter.addAll(proximas + pasadas.reversed())
        adapter.notifyDataSetChanged()

        tvResumen.text = when (proximas.size) {
            0 -> "No tienes citas próximas."
            1 -> "Tienes 1 cita próxima."
            else -> "Tienes ${proximas.size} citas próximas."
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
