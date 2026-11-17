package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla de Detalle de una Mascota.
 * Recibe el ID de la mascota por Intent, consulta sus datos en SQLite y los presenta en TextViews.
 * Permite navegar a EditarMascotaActivity, eliminar el registro con AlertDialog de confirmación,
 * o regresar al listado principal.
 */
class DetalleMascotaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvDetalleId: TextView
    private lateinit var tvDetalleNombre: TextView
    private lateinit var tvDetalleEspecie: TextView
    private lateinit var tvDetalleEdad: TextView
    private lateinit var tvDetallePeso: TextView
    private lateinit var tvDetalleNotas: TextView
    private lateinit var btnEditarMascota: Button
    private lateinit var btnEliminarMascota: Button
    private lateinit var btnRegresarListado: Button

    private var mascotaId: Int = -1
    private var mascotaActual: Mascota? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_mascota)

        // Inicialización de base de datos y vistas
        dbHelper = DatabaseHelper(this)
        tvDetalleId = findViewById(R.id.tvDetalleId)
        tvDetalleNombre = findViewById(R.id.tvDetalleNombre)
        tvDetalleEspecie = findViewById(R.id.tvDetalleEspecie)
        tvDetalleEdad = findViewById(R.id.tvDetalleEdad)
        tvDetallePeso = findViewById(R.id.tvDetallePeso)
        tvDetalleNotas = findViewById(R.id.tvDetalleNotas)
        btnEditarMascota = findViewById(R.id.btnEditarMascota)
        btnEliminarMascota = findViewById(R.id.btnEliminarMascota)
        btnRegresarListado = findViewById(R.id.btnRegresarListado)

        // Recepción del ID enviado mediante el Intent
        mascotaId = intent.getIntExtra("MASCOTA_ID", -1)
        if (mascotaId == -1) {
            Toast.makeText(this, "Error: No se recibió un ID de mascota válido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Botón Editar: abre EditarMascotaActivity pasando el ID actual
        btnEditarMascota.setOnClickListener {
            val intentEditar = Intent(this, EditarMascotaActivity::class.java).apply {
                putExtra("MASCOTA_ID", mascotaId)
            }
            startActivity(intentEditar)
        }

        // Botón Eliminar: muestra un AlertDialog obligatorio antes de borrar físicamente en SQLite
        btnEliminarMascota.setOnClickListener {
            mostrarDialogoConfirmacionEliminar()
        }

        // Botón explícito para regresar al listado
        btnRegresarListado.setOnClickListener {
            finish()
        }

        // Carga inicial de datos
        cargarDetalleMascota()
    }

    /**
     * Al regresar desde EditarMascotaActivity se recargan los datos actualizados desde SQLite.
     */
    override fun onResume() {
        super.onResume()
        if (mascotaId != -1) {
            cargarDetalleMascota()
        }
    }

    /**
     * Consulta la mascota por su ID en SQLite y rellena los TextView correspondientes.
     */
    private fun cargarDetalleMascota() {
        mascotaActual = dbHelper.obtenerMascotaPorId(mascotaId)
        val mascota = mascotaActual

        if (mascota == null) {
            Toast.makeText(this, "El registro de la mascota ya no existe.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvDetalleId.text = "#${mascota.id}"
        tvDetalleNombre.text = mascota.nombre
        tvDetalleEspecie.text = mascota.especie
        tvDetalleEdad.text = "${mascota.edad} años"
        tvDetallePeso.text = "${mascota.peso} kg"
        tvDetalleNotas.text = if (mascota.notas.isNotBlank()) mascota.notas else "Sin notas registradas."
    }

    /**
     * Lanza un AlertDialog de confirmación para prevenir eliminaciones accidentales.
     * Si el usuario acepta, elimina el registro en SQLite, muestra un Toast e inmoviliza/cierra la vista.
     */
    private fun mostrarDialogoConfirmacionEliminar() {
        val nombreMostrar = mascotaActual?.nombre ?: "esta mascota"

        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar permanentemente el expediente de \"$nombreMostrar\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, Eliminar") { _, _ ->
                // Inmovilizar botones durante el cierre para evitar dobles clics
                btnEliminarMascota.isEnabled = false
                btnEditarMascota.isEnabled = false

                val filasEliminadas = dbHelper.eliminarMascota(mascotaId)
                if (filasEliminadas > 0) {
                    Toast.makeText(
                        this,
                        "Mascota eliminada correctamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Cierra la vista y regresa al listado actualizado
                } else {
                    btnEliminarMascota.isEnabled = true
                    btnEditarMascota.isEnabled = true
                    Toast.makeText(
                        this,
                        "Error: No se pudo eliminar el registro.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}
