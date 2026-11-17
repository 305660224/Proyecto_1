package com.example.proyecto_1_pataaldia

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Formulario para modificar una cita existente (precarga sus datos actuales). */
class EditarCitaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tilMotivo: TextInputLayout
    private lateinit var tilVeterinaria: TextInputLayout
    private lateinit var tilFecha: TextInputLayout
    private lateinit var tilHora: TextInputLayout
    private lateinit var etMotivo: TextInputEditText
    private lateinit var etVeterinaria: TextInputEditText
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHora: TextInputEditText

    private var citaId: Int = -1

    /** Fecha y hora con que se abrió la cita, para saber si el usuario la cambió. */
    private var fechaHoraOriginal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_cita)
        findViewById<View>(R.id.editarCitaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        citaId = intent.getIntExtra(Cita.EXTRA_ID, -1)

        tilMotivo = findViewById(R.id.tilMotivoCita)
        tilVeterinaria = findViewById(R.id.tilVeterinariaCita)
        tilFecha = findViewById(R.id.tilFechaCita)
        tilHora = findViewById(R.id.tilHoraCita)
        etMotivo = findViewById(R.id.etMotivoCita)
        etVeterinaria = findViewById(R.id.etVeterinariaCita)
        etFecha = findViewById(R.id.etFechaCita)
        etHora = findViewById(R.id.etHoraCita)

        val cita = dbHelper.obtenerCitaPorId(citaId)
        if (cita == null) {
            Toast.makeText(this, "No se encontró la cita seleccionada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Precarga de los datos actuales en el formulario.
        fechaHoraOriginal = cita.fechaHora
        etMotivo.setText(cita.motivo)
        etVeterinaria.setText(cita.veterinaria)
        etFecha.setText(fechaCitaParaMostrar(cita.fechaHora))
        etHora.setText(horaCitaParaMostrar(cita.fechaHora))

        etFecha.configurarSelectorFechaCita()
        etHora.configurarSelectorHoraCita()
        tilMotivo.limpiarErrorAlEscribir()
        tilVeterinaria.limpiarErrorAlEscribir()
        tilFecha.limpiarErrorAlEscribir()
        tilHora.limpiarErrorAlEscribir()

        findViewById<Button>(R.id.btnGuardarCambiosCita).setOnClickListener { guardarCambios() }
        findViewById<Button>(R.id.btnCancelarEditarCita).setOnClickListener { finish() }
    }

    /** Valida y hace UPDATE de la cita (mismo id, no se duplica el registro). */
    private fun guardarCambios() {
        if (!validarCamposCita(tilMotivo, tilVeterinaria, tilFecha, tilHora)) return

        val fechaHora = unirFechaHoraCita(
            etFecha.text.toString().trim(),
            etHora.text.toString().trim()
        )
        if (fechaHora == null) {
            tilFecha.error = "La fecha o la hora no son válidas"
            return
        }

        // Solo se valida la fecha y hora si el usuario la cambió
        // (así se puede corregir el motivo o la veterinaria de una cita que ya pasó).
        if (fechaHora != fechaHoraOriginal) {
            if (citaYaPaso(fechaHora)) {
                tilHora.error = "La cita debe ser en una fecha y hora futura"
                return
            }
            // Regla de negocio: no puede haber dos citas en la misma fecha y hora.
            if (dbHelper.existeCitaEnFechaHora(fechaHora, excluirId = citaId)) {
                mostrarHorarioOcupado(
                    dbHelper.sugerirHorarioLibre(fechaHora, excluirId = citaId),
                    etFecha, etHora, tilHora
                )
                return
            }
        }

        val cita = Cita(
            id = citaId,
            motivo = etMotivo.text.toString().trim(),
            fechaHora = fechaHora,
            veterinaria = etVeterinaria.text.toString().trim()
        )

        if (dbHelper.actualizarCita(cita) > 0) {
            Toast.makeText(this, "Cita actualizada correctamente", Toast.LENGTH_SHORT).show()
            finish() // Vuelve al detalle, que se recarga en onResume().
        } else {
            Toast.makeText(this, "No se pudo actualizar la cita", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
