package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de detalle de una vacuna, con opciones para editar, eliminar y regresar al listado. */
class DetalleVacunaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvNombre: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvNotas: TextView

    private var vacunaId: Int = -1
    private var vacunaActual: Vacuna? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_vacuna)
        findViewById<View>(R.id.detalleVacunaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        vacunaId = intent.getIntExtra(Vacuna.EXTRA_ID, -1)

        tvNombre = findViewById(R.id.tvDetalleNombre)
        tvFecha = findViewById(R.id.tvDetalleFecha)
        tvNotas = findViewById(R.id.tvDetalleNotas)

        findViewById<Button>(R.id.btnEditarVacuna).setOnClickListener {
            val intent = Intent(this, EditarVacunaActivity::class.java)
            intent.putExtra(Vacuna.EXTRA_ID, vacunaId)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnEliminarVacuna).setOnClickListener { confirmarEliminacion() }
        findViewById<Button>(R.id.btnRegresarListado).setOnClickListener { finish() }
    }

    // Se vuelve a leer de SQLite para reflejar los cambios hechos en la pantalla de edición.
    override fun onResume() {
        super.onResume()
        cargarDetalle()
    }

    /** Consulta la vacuna por id y muestra sus datos; si ya no existe, cierra la pantalla. */
    private fun cargarDetalle() {
        val vacuna = dbHelper.obtenerVacunaPorId(vacunaId)
        if (vacuna == null) {
            Toast.makeText(this, "No se encontró la vacuna seleccionada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        vacunaActual = vacuna
        tvNombre.text = vacuna.nombre
        tvFecha.text = vacuna.fechaAplicacion
        tvNotas.text = vacuna.notas.ifBlank { "Sin notas" }
    }

    /** Muestra el AlertDialog de confirmación antes de borrar. */
    private fun confirmarEliminacion() {
        val vacuna = vacunaActual ?: return
        AlertDialog.Builder(this)
            .setTitle("Eliminar vacuna")
            .setMessage("¿Seguro que deseas eliminar la vacuna \"${vacuna.nombre}\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> eliminarVacuna(vacuna) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Borra la vacuna en SQLite, informa con un Toast y regresa al listado. */
    private fun eliminarVacuna(vacuna: Vacuna) {
        if (dbHelper.eliminarVacuna(vacuna.id) > 0) {
            Toast.makeText(this, "Vacuna eliminada correctamente", Toast.LENGTH_SHORT).show()
            finish() // Regresa al listado, que se recarga en onResume().
        } else {
            Toast.makeText(this, "No se pudo eliminar la vacuna", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
