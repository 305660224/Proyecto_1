package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Formulario para agendar una nueva cita veterinaria. */
class AgregarCitaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tilMotivo: TextInputLayout
    private lateinit var tilVeterinaria: TextInputLayout
    private lateinit var tilFecha: TextInputLayout
    private lateinit var tilHora: TextInputLayout
    private lateinit var etMotivo: TextInputEditText
    private lateinit var etVeterinaria: TextInputEditText
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHora: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_cita)
        findViewById<View>(R.id.agregarCitaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)

        tilMotivo = findViewById(R.id.tilMotivoCita)
        tilVeterinaria = findViewById(R.id.tilVeterinariaCita)
        tilFecha = findViewById(R.id.tilFechaCita)
        tilHora = findViewById(R.id.tilHoraCita)
        etMotivo = findViewById(R.id.etMotivoCita)
        etVeterinaria = findViewById(R.id.etVeterinariaCita)
        etFecha = findViewById(R.id.etFechaCita)
        etHora = findViewById(R.id.etHoraCita)

        etFecha.configurarSelectorFechaCita()
        etHora.configurarSelectorHoraCita()
        tilMotivo.limpiarErrorAlEscribir()
        tilVeterinaria.limpiarErrorAlEscribir()
        tilFecha.limpiarErrorAlEscribir()
        tilHora.limpiarErrorAlEscribir()

        findViewById<Button>(R.id.btnGuardarCita).setOnClickListener { guardarCita() }
        findViewById<Button>(R.id.btnCancelarAgregarCita).setOnClickListener { finish() }
    }

    private fun guardarCita() {
        if (!validarCamposCita(tilMotivo, tilVeterinaria, tilFecha, tilHora)) return

        val fechaHora = unirFechaHoraCita(
            etFecha.text.toString().trim(),
            etHora.text.toString().trim()
        )
        if (fechaHora == null) {
            tilFecha.error = "La fecha o la hora no son válidas"
            return
        }

        // Una cita nueva debe quedar en el futuro.
        if (citaYaPaso(fechaHora)) {
            tilHora.error = "La cita debe ser en una fecha y hora futura"
            return
        }

        // Regla de negocio: no puede haber dos citas en la misma fecha y hora.
        if (dbHelper.existeCitaEnFechaHora(fechaHora)) {
            mostrarHorarioOcupado(dbHelper.sugerirHorarioLibre(fechaHora), etFecha, etHora, tilHora)
            return
        }

        val cita = Cita(
            motivo = etMotivo.text.toString().trim(),
            fechaHora = fechaHora,
            veterinaria = etVeterinaria.text.toString().trim()
        )

        if (dbHelper.insertarCita(cita) == -1L) {
            Toast.makeText(this, "No se pudo guardar la cita. Intenta de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Cita agendada correctamente", Toast.LENGTH_SHORT).show()
        volverAlListado()
    }

    /** Regresa al listado (reutiliza la instancia existente si ya está en la pila y la recarga en onResume). */
    private fun volverAlListado() {
        val intent = Intent(this, CitasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
