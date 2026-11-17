package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de detalle de una cita, con opciones para editarla, cancelarla y regresar a la agenda. */
class DetalleCitaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvMotivo: TextView
    private lateinit var tvVeterinaria: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvHora: TextView
    private lateinit var tvEstado: TextView

    private var citaId: Int = -1
    private var citaActual: Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cita)
        findViewById<View>(R.id.detalleCitaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        citaId = intent.getIntExtra(Cita.EXTRA_ID, -1)

        tvMotivo = findViewById(R.id.tvDetalleCitaMotivo)
        tvVeterinaria = findViewById(R.id.tvDetalleCitaVeterinaria)
        tvFecha = findViewById(R.id.tvDetalleCitaFecha)
        tvHora = findViewById(R.id.tvDetalleCitaHora)
        tvEstado = findViewById(R.id.tvDetalleCitaEstado)

        findViewById<Button>(R.id.btnEditarCita).setOnClickListener {
            val intent = Intent(this, EditarCitaActivity::class.java)
            intent.putExtra(Cita.EXTRA_ID, citaId)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnCancelarCita).setOnClickListener { confirmarCancelacion() }
        findViewById<Button>(R.id.btnRegresarAgenda).setOnClickListener { finish() }
    }

    // Se vuelve a leer de SQLite para reflejar los cambios hechos en la pantalla de edición.
    override fun onResume() {
        super.onResume()
        cargarDetalle()
    }

    private fun cargarDetalle() {
        val cita = dbHelper.obtenerCitaPorId(citaId)
        if (cita == null) {
            Toast.makeText(this, "No se encontró la cita seleccionada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        citaActual = cita
        tvMotivo.text = cita.motivo
        tvVeterinaria.text = cita.veterinaria
        tvFecha.text = fechaCitaParaMostrar(cita.fechaHora)
        tvHora.text = horaCitaParaMostrar(cita.fechaHora)

        val estado = estadoDeCita(cita.fechaHora)
        tvEstado.text = estado.etiqueta
        tvEstado.setTextColor(estado.color)
    }

    private fun confirmarCancelacion() {
        val cita = citaActual ?: return
        AlertDialog.Builder(this)
            .setTitle("Cancelar cita")
            .setMessage(
                "¿Seguro que deseas cancelar la cita \"${cita.motivo}\" del " +
                    "${fechaCitaParaMostrar(cita.fechaHora)} a las ${horaCitaParaMostrar(cita.fechaHora)}? " +
                    "Se eliminará de la agenda."
            )
            .setPositiveButton("Sí, cancelar") { _, _ -> eliminarCita(cita) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarCita(cita: Cita) {
        if (dbHelper.eliminarCita(cita.id) > 0) {
            Toast.makeText(this, "Cita cancelada correctamente", Toast.LENGTH_SHORT).show()
            finish() // Regresa a la agenda, que se recarga en onResume().
        } else {
            Toast.makeText(this, "No se pudo cancelar la cita", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
