package com.example.proyecto_1_pataaldia

/**
 * Modelo de una vacuna aplicada a una mascota.
 * Se corresponde con la tabla [DatabaseHelper.TABLE_VACUNAS].
 *
 * @property id identificador único (0 mientras la vacuna aún no se ha guardado en SQLite).
 * @property nombre nombre de la vacuna (columna nombre_vacuna).
 * @property fechaAplicacion fecha de aplicación en formato dd/MM/yyyy (columna fecha_aplicacion).
 * @property notas observaciones opcionales (columna notas).
 */
data class Vacuna(
    val id: Int = 0,
    val nombre: String,
    val fechaAplicacion: String,
    val notas: String = ""
) {
    companion object {
        /** Clave del extra de Intent con el id de la vacuna seleccionada. */
        const val EXTRA_ID = "extra_vacuna_id"
    }
}
