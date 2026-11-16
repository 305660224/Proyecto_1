package com.example.proyecto_1_pataaldia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad para editar un registro existente de Mascota en SQLite.
 * Recibe el ID mediante Intent, precarga la información actual en los campos EditText
 * y actualiza únicamente el registro correspondiente sin crear duplicados.
 */
class EditarMascotaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etEditarNombreMascota: EditText
    private lateinit var etEditarEspecieMascota: EditText
    private lateinit var etEditarEdadMascota: EditText
    private lateinit var etEditarPesoMascota: EditText
    private lateinit var etEditarNotasMascota: EditText
    private lateinit var btnGuardarCambiosMascota: Button
    private lateinit var btnCancelarEditarMascota: Button

    private var mascotaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_mascota)

        // Inicialización de base de datos y componentes del formulario
        dbHelper = DatabaseHelper(this)
        etEditarNombreMascota = findViewById(R.id.etEditarNombreMascota)
        etEditarEspecieMascota = findViewById(R.id.etEditarEspecieMascota)
        etEditarEdadMascota = findViewById(R.id.etEditarEdadMascota)
        etEditarPesoMascota = findViewById(R.id.etEditarPesoMascota)
        etEditarNotasMascota = findViewById(R.id.etEditarNotasMascota)
        btnGuardarCambiosMascota = findViewById(R.id.btnGuardarCambiosMascota)
        btnCancelarEditarMascota = findViewById(R.id.btnCancelarEditarMascota)

        // Recepción del ID de la mascota a editar
        mascotaId = intent.getIntExtra("MASCOTA_ID", -1)
        if (mascotaId == -1) {
            Toast.makeText(this, "Error: ID de mascota no válido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Precargar datos actuales desde SQLite
        precargarDatosMascota()

        // Acción para guardar los cambios realizados
        btnGuardarCambiosMascota.setOnClickListener {
            actualizarDatosMascota()
        }

        // Acción para cancelar y volver sin guardar cambios
        btnCancelarEditarMascota.setOnClickListener {
            finish()
        }
    }

    /**
     * Consulta la mascota actual en SQLite y rellena los campos del formulario.
     */
    private fun precargarDatosMascota() {
        val mascota = dbHelper.obtenerMascotaPorId(mascotaId)
        if (mascota == null) {
            Toast.makeText(this, "Error: No se encontró el registro de la mascota.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        etEditarNombreMascota.setText(mascota.nombre)
        etEditarEspecieMascota.setText(mascota.especie)
        etEditarEdadMascota.setText(mascota.edad.toString())
        etEditarPesoMascota.setText(mascota.peso.toString())
        etEditarNotasMascota.setText(mascota.notas)
    }

    /**
     * Valida los datos modificados y ejecuta el UPDATE en SQLite sobre el ID correspondiente.
     */
    private fun actualizarDatosMascota() {
        val nombre = etEditarNombreMascota.text.toString().trim()
        val especie = etEditarEspecieMascota.text.toString().trim()
        val edadStr = etEditarEdadMascota.text.toString().trim()
        val pesoStr = etEditarPesoMascota.text.toString().trim()
        val notas = etEditarNotasMascota.text.toString().trim()

        // Validación estricta de campos obligatorios
        if (nombre.isEmpty() || especie.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(
                this,
                "Advertencia: Los campos Nombre, Especie y Edad son obligatorios.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val edad = edadStr.toIntOrNull()
        if (edad == null || edad < 0) {
            Toast.makeText(
                this,
                "Advertencia: Ingrese una edad válida en años.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

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

        // Creación del objeto Mascota conservando el ID original para no duplicar registros
        val mascotaActualizada = Mascota(
            id = mascotaId,
            nombre = nombre,
            especie = especie,
            edad = edad,
            peso = peso,
            notas = notas
        )

        val filasAfectadas = dbHelper.actualizarMascota(mascotaActualizada)
        if (filasAfectadas > 0) {
            Toast.makeText(
                this,
                "Cambios guardados exitosamente.",
                Toast.LENGTH_SHORT
            ).show()
            finish() // Regresa a la pantalla de detalle/listado
        } else {
            Toast.makeText(
                this,
                "Error al actualizar los datos de la mascota.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
