package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de listado: muestra en un ListView las vacunas guardadas en SQLite. */
class VacunasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: VacunaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vacunas)
        findViewById<View>(R.id.vacunasRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        adapter = VacunaAdapter(this, mutableListOf())

        val lvVacunas = findViewById<ListView>(R.id.lvVacunas)
        lvVacunas.adapter = adapter
        lvVacunas.emptyView = findViewById<TextView>(R.id.tvVacunasVacio)

        // Seleccionar una vacuna abre su detalle.
        lvVacunas.setOnItemClickListener { _, _, posicion, _ ->
            val vacuna = adapter.getItem(posicion) ?: return@setOnItemClickListener
            val intent = Intent(this, DetalleVacunaActivity::class.java)
            intent.putExtra(Vacuna.EXTRA_ID, vacuna.id)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAgregarVacunaLista).setOnClickListener {
            startActivity(Intent(this, AgregarVacunaActivity::class.java))
        }
    }

    // Se recarga cada vez que la pantalla vuelve a primer plano (tras agregar, editar o eliminar).
    override fun onResume() {
        super.onResume()
        cargarVacunas()
    }

    private fun cargarVacunas() {
        adapter.clear()
        adapter.addAll(dbHelper.obtenerVacunas())
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
