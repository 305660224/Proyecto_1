package com.example.proyecto_1_pataaldia

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Formato en que se guarda la fecha y hora de la cita en SQLite (se ordena bien como texto). */
private const val FORMATO_FECHA_HORA_BD = "yyyy-MM-dd HH:mm"

/** Formatos en que se muestran la fecha y la hora en pantalla. */
private const val FORMATO_FECHA_CITA = "dd/MM/yyyy"
private const val FORMATO_HORA_CITA = "hh:mm a" // Ej. 02:30 PM

/** Minutos que se pueden elegir en el selector de hora (de 5 en 5). */
private const val PASO_MINUTOS = 5

/** Minutos entre un horario sugerido y el siguiente cuando la hora elegida ya está ocupada. */
private const val INTERVALO_SUGERENCIA_MINUTOS = 30

/** Cantidad máxima de horarios que se revisan al buscar uno libre (48 × 30 min = 24 horas). */
private const val MAX_INTENTOS_SUGERENCIA = 48

private fun formato(patron: String) = SimpleDateFormat(patron, Locale.US).apply { isLenient = false }

// ---------------------------------------------------------------------------------------------
// Conversión entre el formato de la base de datos y el de la pantalla
// ---------------------------------------------------------------------------------------------

/**
 * Une la fecha (dd/MM/yyyy) y la hora (hh:mm AM/PM) del formulario en el formato de la base de datos.
 * Devuelve null si alguna de las dos no es válida.
 */
fun unirFechaHoraCita(fecha: String, hora: String): String? {
    val fechaHora = runCatching {
        formato("$FORMATO_FECHA_CITA $FORMATO_HORA_CITA").parse("$fecha $hora")
    }.getOrNull() ?: return null
    return formato(FORMATO_FECHA_HORA_BD).format(fechaHora)
}

/** Convierte un Date al formato de la base de datos (yyyy-MM-dd HH:mm). */
fun fechaHoraCitaParaBd(fecha: Date): String = formato(FORMATO_FECHA_HORA_BD).format(fecha)

/** Convierte la fecha y hora guardada (yyyy-MM-dd HH:mm) en Date, o null si no es válida. */
fun fechaHoraCitaADate(fechaHora: String): Date? =
    runCatching { formato(FORMATO_FECHA_HORA_BD).parse(fechaHora) }.getOrNull()

/** Devuelve solo la fecha de la cita en formato dd/MM/yyyy para mostrarla. */
fun fechaCitaParaMostrar(fechaHora: String): String =
    fechaHoraCitaADate(fechaHora)?.let { formato(FORMATO_FECHA_CITA).format(it) } ?: fechaHora

/** Devuelve solo la hora de la cita en formato hh:mm AM/PM para mostrarla. */
fun horaCitaParaMostrar(fechaHora: String): String =
    fechaHoraCitaADate(fechaHora)?.let { formato(FORMATO_HORA_CITA).format(it) } ?: ""

// ---------------------------------------------------------------------------------------------
// Estado de la cita (Hoy, Próxima o Pasada)
// ---------------------------------------------------------------------------------------------

/** Estado de una cita según su fecha y hora respecto al momento actual. */
enum class EstadoCita(val etiqueta: String, val color: Int) {
    HOY("Hoy", 0xFFBF360C.toInt()),
    PROXIMA("Próxima", 0xFF2E7D32.toInt()),
    PASADA("Pasada", 0xFF616161.toInt())
}

/** Calcula si la cita es hoy, más adelante o si ya pasó. */
fun estadoDeCita(fechaHora: String): EstadoCita {
    val fechaCita = fechaHoraCitaADate(fechaHora) ?: return EstadoCita.PROXIMA
    val ahora = Calendar.getInstance()
    if (fechaCita.before(ahora.time)) return EstadoCita.PASADA

    val calendarioCita = Calendar.getInstance().apply { time = fechaCita }
    val esHoy = calendarioCita.get(Calendar.YEAR) == ahora.get(Calendar.YEAR) &&
        calendarioCita.get(Calendar.DAY_OF_YEAR) == ahora.get(Calendar.DAY_OF_YEAR)
    return if (esHoy) EstadoCita.HOY else EstadoCita.PROXIMA
}

/** Indica si la fecha y hora (formato de la base de datos) ya pasó. */
fun citaYaPaso(fechaHora: String): Boolean =
    fechaHoraCitaADate(fechaHora)?.before(Date()) ?: false

// ---------------------------------------------------------------------------------------------
// Selectores de fecha y hora
// ---------------------------------------------------------------------------------------------

/**
 * Al tocar el campo se abre un selector de fecha y el resultado se escribe como dd/MM/yyyy.
 * Las citas se agendan a futuro, por eso no se permiten días anteriores a hoy.
 */
fun EditText.configurarSelectorFechaCita() {
    setOnClickListener {
        val formatoFecha = formato(FORMATO_FECHA_CITA)
        val calendario = Calendar.getInstance()
        runCatching { formatoFecha.parse(text.toString().trim()) }
            .getOrNull()
            ?.let { calendario.time = it }

        val dialogo = DatePickerDialog(
            context,
            { _, anio, mes, dia ->
                val elegida = Calendar.getInstance().apply { set(anio, mes, dia) }
                setText(formatoFecha.format(elegida.time))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        val inicioDeHoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dialogo.datePicker.minDate = inicioDeHoy.timeInMillis
        dialogo.show()
    }
}

/**
 * Al tocar el campo se abre un selector de hora con ruedas (hora, minutos y AM/PM)
 * y el resultado se escribe como hh:mm AM/PM, por ejemplo "02:30 PM".
 * Los minutos van de 5 en 5, que es lo normal para una cita.
 */
fun EditText.configurarSelectorHoraCita() {
    setOnClickListener {
        // Hora inicial: la que ya tiene el campo o, si está vacío, la hora actual.
        val calendario = Calendar.getInstance()
        runCatching { formato(FORMATO_HORA_CITA).parse(text.toString().trim()) }
            .getOrNull()
            ?.let { horaGuardada ->
                val aux = Calendar.getInstance().apply { time = horaGuardada }
                calendario.set(Calendar.HOUR_OF_DAY, aux.get(Calendar.HOUR_OF_DAY))
                calendario.set(Calendar.MINUTE, aux.get(Calendar.MINUTE))
            }

        val vista = LayoutInflater.from(context)
            .inflate(R.layout.dialog_hora_cita, null as ViewGroup?)
        val npHora = vista.findViewById<NumberPicker>(R.id.npHoraCita)
        val npMinuto = vista.findViewById<NumberPicker>(R.id.npMinutoCita)
        val npPeriodo = vista.findViewById<NumberPicker>(R.id.npPeriodoCita)

        // Rueda de horas: 1 a 12.
        npHora.minValue = 1
        npHora.maxValue = 12

        // Rueda de minutos: 00, 05, 10, ... 55 (el valor interno es la posición 0..11).
        val minutos = (0 until 60 step PASO_MINUTOS).map { String.format(Locale.US, "%02d", it) }
        npMinuto.minValue = 0
        npMinuto.maxValue = minutos.size - 1
        npMinuto.displayedValues = minutos.toTypedArray()

        // Rueda AM / PM (no da la vuelta).
        npPeriodo.minValue = 0
        npPeriodo.maxValue = 1
        npPeriodo.displayedValues = arrayOf("AM", "PM")
        npPeriodo.wrapSelectorWheel = false

        // Evita que al tocar un número se abra el teclado: solo se gira la rueda.
        listOf(npHora, npMinuto, npPeriodo).forEach {
            it.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }

        // Valores iniciales (de 24 h a 12 h).
        val hora24 = calendario.get(Calendar.HOUR_OF_DAY)
        npHora.value = if (hora24 % 12 == 0) 12 else hora24 % 12
        npMinuto.value = calendario.get(Calendar.MINUTE) / PASO_MINUTOS
        npPeriodo.value = if (hora24 < 12) 0 else 1

        AlertDialog.Builder(context)
            .setTitle("Elige la hora")
            .setView(vista)
            .setPositiveButton("Aceptar") { _, _ ->
                val hora = npHora.value
                val minuto = npMinuto.value * PASO_MINUTOS
                val periodo = if (npPeriodo.value == 0) "AM" else "PM"
                setText(String.format(Locale.US, "%02d:%02d %s", hora, minuto, periodo))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

// ---------------------------------------------------------------------------------------------
// Validaciones del formulario
// ---------------------------------------------------------------------------------------------

/**
 * Valida los campos obligatorios del formulario de citas (motivo, veterinaria, fecha y hora).
 * Marca con error los que estén vacíos y devuelve true solo si todos están completos.
 */
fun validarCamposCita(
    tilMotivo: TextInputLayout,
    tilVeterinaria: TextInputLayout,
    tilFecha: TextInputLayout,
    tilHora: TextInputLayout
): Boolean {
    val campos = listOf(
        tilMotivo to "El motivo de la cita es obligatorio",
        tilVeterinaria to "La veterinaria es obligatoria",
        tilFecha to "La fecha es obligatoria",
        tilHora to "La hora es obligatoria"
    )

    var valido = true
    for ((campo, mensaje) in campos) {
        if (campo.editText?.text.isNullOrBlank()) {
            campo.error = mensaje
            valido = false
        } else {
            campo.error = null
        }
    }
    return valido
}

// ---------------------------------------------------------------------------------------------
// Regla de negocio: no puede haber dos citas en la misma fecha y hora (RF-08)
// ---------------------------------------------------------------------------------------------

/**
 * Busca el siguiente horario libre a partir de [fechaHora], en saltos de 30 minutos,
 * revisando como máximo las siguientes 24 horas.
 *
 * @param excluirId id de la cita que se está editando (para no chocar consigo misma); 0 al agregar.
 * @return el horario libre en formato de la base de datos, o null si no se encontró ninguno.
 */
fun DatabaseHelper.sugerirHorarioLibre(fechaHora: String, excluirId: Int = 0): String? {
    val inicio = fechaHoraCitaADate(fechaHora) ?: return null
    val calendario = Calendar.getInstance().apply { time = inicio }

    repeat(MAX_INTENTOS_SUGERENCIA) {
        calendario.add(Calendar.MINUTE, INTERVALO_SUGERENCIA_MINUTOS)
        val candidato = fechaHoraCitaParaBd(calendario.time)
        if (!citaYaPaso(candidato) && !existeCitaEnFechaHora(candidato, excluirId)) {
            return candidato
        }
    }
    return null
}

/**
 * Avisa que el horario elegido ya está ocupado y, si hay un horario libre, ofrece usarlo.
 * Al aceptar la sugerencia se escriben la nueva fecha y hora en el formulario.
 */
fun Context.mostrarHorarioOcupado(
    sugerencia: String?,
    etFecha: EditText,
    etHora: EditText,
    tilHora: TextInputLayout
) {
    tilHora.error = "Ya hay una cita en esa fecha y hora"

    val dialogo = AlertDialog.Builder(this).setTitle("Horario ocupado")
    if (sugerencia == null) {
        dialogo.setMessage("Ya hay una cita agendada en esa fecha y hora. Elige otro horario.")
            .setPositiveButton("Entendido", null)
    } else {
        val fecha = fechaCitaParaMostrar(sugerencia)
        val hora = horaCitaParaMostrar(sugerencia)
        dialogo.setMessage(
            "Ya hay una cita agendada en esa fecha y hora.\n\n" +
                "El siguiente horario libre es el $fecha a las $hora. ¿Deseas usarlo?"
        )
            .setPositiveButton("Usar $hora") { _, _ ->
                etFecha.setText(fecha)
                etHora.setText(hora)
            }
            .setNegativeButton("Elegir otro", null)
    }
    dialogo.show()
}
