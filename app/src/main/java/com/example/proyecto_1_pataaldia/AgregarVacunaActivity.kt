package com.example.proyecto_1_pataaldia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Formulario para registrar una nueva vacuna. */
class AgregarVacunaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilFecha: TextInputLayout
    private lateinit var etNombre: TextInputEditText
    private lateinit var etFecha: TextInputEditText
    private lateinit var etNotas: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_vacuna)
        findViewById<View>(R.id.agregarVacunaRoot).aplicarInsetsSistema()

        dbHelper = DatabaseHelper(this)

        tilNombre = findViewById(R.id.tilNombreVacuna)
        tilFecha = findViewById(R.id.tilFechaVacuna)
        etNombre = findViewById(R.id.etNombreVacuna)
        etFecha = findViewById(R.id.etFechaVacuna)
        etNotas = findViewById(R.id.etNotasVacuna)

        etFecha.configurarSelectorFecha()
        tilNombre.limpiarErrorAlEscribir()
        tilFecha.limpiarErrorAlEscribir()

        findViewById<Button>(R.id.btnGuardarVacuna).setOnClickListener { guardarVacuna() }
        findViewById<Button>(R.id.btnCancelarAgregar).setOnClickListener { finish() }
    }

    /** Valida el formulario, inserta la vacuna en SQLite, muestra un Toast y regresa al listado. */
    private fun guardarVacuna() {
        if (!validarCamposVacuna(tilNombre, tilFecha)) return

        val vacuna = Vacuna(
            nombre = etNombre.text.toString().trim(),
            fechaAplicacion = etFecha.text.toString().trim(),
            notas = etNotas.text.toString().trim()
        )

        if (dbHelper.insertarVacuna(vacuna) == -1L) {
            Toast.makeText(this, "No se pudo guardar la vacuna. Intenta de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Vacuna guardada correctamente", Toast.LENGTH_SHORT).show()
        volverAlListado()
    }

    /** Regresa al listado (reutiliza la instancia existente si ya está en la pila y la recarga en onResume). */
    private fun volverAlListado() {
        val intent = Intent(this, VacunasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
