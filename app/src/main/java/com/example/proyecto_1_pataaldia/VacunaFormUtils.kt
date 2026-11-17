package com.example.proyecto_1_pataaldia

import android.app.DatePickerDialog
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Formato en que se guarda y se muestra la fecha de aplicación. */
private const val FORMATO_FECHA = "dd/MM/yyyy"

/** Ajusta el padding de la vista para que no quede debajo de las barras del sistema ni del teclado. */
fun View.aplicarInsetsSistema() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
        insets
    }
}

/** Al tocar el campo se abre un selector de fecha y el resultado se escribe como dd/MM/yyyy. */
fun EditText.configurarSelectorFecha() {
    setOnClickListener {
        val formato = SimpleDateFormat(FORMATO_FECHA, Locale.US).apply { isLenient = false }
        val calendario = Calendar.getInstance()
        runCatching { formato.parse(text.toString().trim()) }
            .getOrNull()
            ?.let { calendario.time = it }

        val dialogo = DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                val elegida = Calendar.getInstance().apply { set(anio, mes, dia) }
                setText(formato.format(elegida.time))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        // Una vacuna ya aplicada no puede tener fecha futura.
        dialogo.datePicker.maxDate = System.currentTimeMillis()
        dialogo.show()
    }
}

/** Quita el mensaje de error del TextInputLayout en cuanto el usuario modifica el texto. */
fun TextInputLayout.limpiarErrorAlEscribir() {
    editText?.doOnTextChanged { _, _, _, _ -> error = null }
}

/**
 * Valida los campos obligatorios del formulario de vacunas (nombre y fecha).
 * Marca con error los que estén vacíos y devuelve true solo si ambos están completos.
 */
fun validarCamposVacuna(tilNombre: TextInputLayout, tilFecha: TextInputLayout): Boolean {
    var valido = true

    if (tilNombre.editText?.text.isNullOrBlank()) {
        tilNombre.error = "El nombre de la vacuna es obligatorio"
        valido = false
    } else {
        tilNombre.error = null
    }

    if (tilFecha.editText?.text.isNullOrBlank()) {
        tilFecha.error = "La fecha de aplicación es obligatoria"
        valido = false
    } else {
        tilFecha.error = null
    }

    return valido
}
