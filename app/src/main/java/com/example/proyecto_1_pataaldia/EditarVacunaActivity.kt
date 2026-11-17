package com.example.proyecto_1_pataaldia

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Formulario para modificar una vacuna existente (precarga sus datos actuales). */
class EditarVacunaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilFecha: TextInputLayout
    private lateinit var etNombre: TextInputEditText
    private lateinit var etFecha: TextInputEditText
    private lateinit var etNotas: TextInputEditText

    private var vacunaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_vacuna)
        findViewById<View>(R.id.editarVacunaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)
        vacunaId = intent.getIntExtra(Vacuna.EXTRA_ID, -1)

        tilNombre = findViewById(R.id.tilNombreVacuna)
        tilFecha = findViewById(R.id.tilFechaVacuna)
        etNombre = findViewById(R.id.etNombreVacuna)
        etFecha = findViewById(R.id.etFechaVacuna)
        etNotas = findViewById(R.id.etNotasVacuna)

        val vacuna = dbHelper.obtenerVacunaPorId(vacunaId)
        if (vacuna == null) {
            Toast.makeText(this, "No se encontró la vacuna seleccionada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Precarga de los datos actuales en el formulario.
        etNombre.setText(vacuna.nombre)
        etFecha.setText(vacuna.fechaAplicacion)
        etNotas.setText(vacuna.notas)

        etFecha.configurarSelectorFecha()
        tilNombre.limpiarErrorAlEscribir()
        tilFecha.limpiarErrorAlEscribir()

        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener { guardarCambios() }
        findViewById<Button>(R.id.btnCancelarEditar).setOnClickListener { finish() }
    }

    /** Valida y hace UPDATE de la vacuna (mismo id, no se duplica el registro). */
    private fun guardarCambios() {
        if (!validarCamposVacuna(tilNombre, tilFecha)) return

        val vacuna = Vacuna(
            id = vacunaId,
            nombre = etNombre.text.toString().trim(),
            fechaAplicacion = etFecha.text.toString().trim(),
            notas = etNotas.text.toString().trim()
        )

        if (dbHelper.actualizarVacuna(vacuna) > 0) {
            Toast.makeText(this, "Vacuna actualizada correctamente", Toast.LENGTH_SHORT).show()
            finish() // Vuelve al detalle, que se recarga en onResume().
        } else {
            Toast.makeText(this, "No se pudo actualizar la vacuna", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
