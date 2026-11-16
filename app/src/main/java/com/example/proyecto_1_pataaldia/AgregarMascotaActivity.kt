package com.example.proyecto_1_pataaldia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad con el formulario para registrar una nueva mascota en SQLite.
 * Incluye validaciones estrictas de campos obligatorios (Nombre, Especie y Edad).
 */
class AgregarMascotaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNombreMascota: EditText
    private lateinit var etEspecieMascota: EditText
    private lateinit var etEdadMascota: EditText
    private lateinit var etPesoMascota: EditText
    private lateinit var etNotasMascota: EditText
    private lateinit var btnGuardarMascota: Button
    private lateinit var btnCancelarAgregarMascota: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_mascota)

        // Inicialización del helper de base de datos y componentes del formulario
        dbHelper = DatabaseHelper(this)
        etNombreMascota = findViewById(R.id.etNombreMascota)
        etEspecieMascota = findViewById(R.id.etEspecieMascota)
        etEdadMascota = findViewById(R.id.etEdadMascota)
        etPesoMascota = findViewById(R.id.etPesoMascota)
        etNotasMascota = findViewById(R.id.etNotasMascota)
        btnGuardarMascota = findViewById(R.id.btnGuardarMascota)
        btnCancelarAgregarMascota = findViewById(R.id.btnCancelarAgregarMascota)

        // Acción del botón Guardar
        btnGuardarMascota.setOnClickListener {
            guardarMascota()
        }

        // Acción del botón Cancelar para regresar sin guardar
        btnCancelarAgregarMascota.setOnClickListener {
            finish()
        }
    }

    /**
     * Captura los datos ingresados, valida los campos obligatorios e inserta el registro en SQLite.
     */
    private fun guardarMascota() {
        val nombre = etNombreMascota.text.toString().trim()
        val especie = etEspecieMascota.text.toString().trim()
        val edadStr = etEdadMascota.text.toString().trim()
        val pesoStr = etPesoMascota.text.toString().trim()
        val notas = etNotasMascota.text.toString().trim()

        // Validación estricta de campos obligatorios: Nombre, Especie y Edad
        if (nombre.isEmpty() || especie.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(
                this,
                "Advertencia: Los campos Nombre, Especie y Edad son obligatorios.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validación numérica de Edad
        val edad = edadStr.toIntOrNull()
        if (edad == null || edad < 0) {
            Toast.makeText(
                this,
                "Advertencia: Ingrese una edad válida en años.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validación de Peso (opcional; si se deja vacío se asigna 0.0, si se ingresa debe ser numérico)
        val peso = if (pesoStr.isEmpty()) {
            0.0
        } else {
            val pesoConvertido = pesoStr.toDoubleOrNull()
            if (pesoConvertido == null || pesoConvertido < 0.0) {
                Toast.makeText(
                    this,
                    "Advertencia: Ingrese un peso válido en kilogramos.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            pesoConvertido
        }

        // Creación del objeto Mascota
        val nuevaMascota = Mascota(
            nombre = nombre,
            especie = especie,
            edad = edad,
            peso = peso,
            notas = notas
        )

        // Inserción en SQLite a través de DatabaseHelper
        val exito = dbHelper.insertarMascota(nuevaMascota)
        if (exito) {
            Toast.makeText(
                this,
                "Mascota registrada exitosamente.",
                Toast.LENGTH_SHORT
            ).show()
            finish() // Cierra la actividad y regresa al listado
        } else {
            Toast.makeText(
                this,
                "Error al guardar la mascota en la base de datos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
