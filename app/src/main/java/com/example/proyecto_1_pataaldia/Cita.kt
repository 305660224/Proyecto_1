package com.example.proyecto_1_pataaldia

/**
 * Modelo de una cita veterinaria.
 * Se corresponde con la tabla [DatabaseHelper.TABLE_CITAS].
 *
 * @property id identificador único (0 mientras la cita aún no se ha guardado en SQLite).
 * @property motivo motivo de la cita, por ejemplo "Control anual" (columna motivo).
 * @property fechaHora fecha y hora en formato yyyy-MM-dd HH:mm (columna fecha_hora).
 *   Se guarda así porque, como texto, se ordena igual que en el calendario;
 *   en pantalla se muestra como dd/MM/yyyy y hh:mm AM/PM (ver CitaFormUtils.kt).
 * @property veterinaria nombre de la veterinaria o clínica (columna veterinaria).
 */
data class Cita(
    val id: Int = 0,
    val motivo: String,
    val fechaHora: String,
    val veterinaria: String
) {
    companion object {
        /** Clave del extra de Intent con el id de la cita seleccionada. */
        const val EXTRA_ID = "extra_cita_id"
    }
}
