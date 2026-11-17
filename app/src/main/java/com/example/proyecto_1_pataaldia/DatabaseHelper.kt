package com.example.proyecto_1_pataaldia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "PataAlDia.db"
        private const val DATABASE_VERSION = 1

        // Tabla Mascotas
        const val TABLE_MASCOTAS = "Mascotas"
        const val COL_MASCOTA_ID = "id"
        const val COL_MASCOTA_NOMBRE = "nombre"
        const val COL_MASCOTA_ESPECIE = "especie"
        const val COL_MASCOTA_EDAD = "edad"
        const val COL_MASCOTA_PESO = "peso"
        const val COL_MASCOTA_NOTAS = "notas"

        // Tabla Vacunas
        const val TABLE_VACUNAS = "Vacunas"
        const val COL_VACUNA_ID = "id"
        const val COL_VACUNA_NOMBRE = "nombre_vacuna"
        const val COL_VACUNA_FECHA = "fecha_aplicacion"
        const val COL_VACUNA_NOTAS = "notas"

        // Tabla Citas
        const val TABLE_CITAS = "Citas"
        const val COL_CITA_ID = "id"
        const val COL_CITA_MOTIVO = "motivo"
        const val COL_CITA_FECHA_HORA = "fecha_hora"
        const val COL_CITA_VETERINARIA = "veterinaria"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableMascotas = """
            CREATE TABLE $TABLE_MASCOTAS (
                $COL_MASCOTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MASCOTA_NOMBRE TEXT NOT NULL,
                $COL_MASCOTA_ESPECIE TEXT NOT NULL,
                $COL_MASCOTA_EDAD INTEGER,
                $COL_MASCOTA_PESO REAL,
                $COL_MASCOTA_NOTAS TEXT
            )
        """.trimIndent()

        val createTableVacunas = """
            CREATE TABLE $TABLE_VACUNAS (
                $COL_VACUNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_VACUNA_NOMBRE TEXT NOT NULL,
                $COL_VACUNA_FECHA TEXT NOT NULL,
                $COL_VACUNA_NOTAS TEXT
            )
        """.trimIndent()

        val createTableCitas = """
            CREATE TABLE $TABLE_CITAS (
                $COL_CITA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CITA_MOTIVO TEXT NOT NULL,
                $COL_CITA_FECHA_HORA TEXT NOT NULL,
                $COL_CITA_VETERINARIA TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableMascotas)
        db.execSQL(createTableVacunas)
        db.execSQL(createTableCitas)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MASCOTAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VACUNAS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITAS")
        onCreate(db)
    }

    // CRUD Mascotas

    // CRUD Vacunas

    private val tagVacunas = "DatabaseHelper.Vacunas"

    /** CREATE: inserta una vacuna. Devuelve el id generado, o -1 si ocurrió un error. */
    fun insertarVacuna(vacuna: Vacuna): Long {
        return try {
            val valores = ContentValues().apply {
                put(COL_VACUNA_NOMBRE, vacuna.nombre)
                put(COL_VACUNA_FECHA, vacuna.fechaAplicacion)
                put(COL_VACUNA_NOTAS, vacuna.notas)
            }
            writableDatabase.insertOrThrow(TABLE_VACUNAS, null, valores)
        } catch (e: SQLException) {
            Log.e(tagVacunas, "Error al insertar la vacuna", e)
            -1L
        }
    }

    /** READ (lista): devuelve todas las vacunas, las más recientes primero. */
    fun obtenerVacunas(): List<Vacuna> {
        val vacunas = mutableListOf<Vacuna>()
        try {
            readableDatabase.query(
                TABLE_VACUNAS, null, null, null, null, null, "$COL_VACUNA_ID DESC"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    vacunas.add(cursor.aVacuna())
                }
            }
        } catch (e: SQLException) {
            Log.e(tagVacunas, "Error al consultar las vacunas", e)
        }
        return vacunas
    }

    /** READ (detalle): devuelve la vacuna con ese id, o null si no existe o hubo un error. */
    fun obtenerVacunaPorId(id: Int): Vacuna? {
        return try {
            readableDatabase.query(
                TABLE_VACUNAS, null, "$COL_VACUNA_ID = ?", arrayOf(id.toString()),
                null, null, null
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.aVacuna() else null
            }
        } catch (e: SQLException) {
            Log.e(tagVacunas, "Error al consultar la vacuna $id", e)
            null
        }
    }

    /** UPDATE: modifica la vacuna con el mismo id. Devuelve filas afectadas (0 si no existe, -1 si hubo error). */
    fun actualizarVacuna(vacuna: Vacuna): Int {
        return try {
            val valores = ContentValues().apply {
                put(COL_VACUNA_NOMBRE, vacuna.nombre)
                put(COL_VACUNA_FECHA, vacuna.fechaAplicacion)
                put(COL_VACUNA_NOTAS, vacuna.notas)
            }
            writableDatabase.update(
                TABLE_VACUNAS, valores, "$COL_VACUNA_ID = ?", arrayOf(vacuna.id.toString())
            )
        } catch (e: SQLException) {
            Log.e(tagVacunas, "Error al actualizar la vacuna ${vacuna.id}", e)
            -1
        }
    }

    /** DELETE: elimina la vacuna con ese id. Devuelve filas afectadas (0 si no existe, -1 si hubo error). */
    fun eliminarVacuna(id: Int): Int {
        return try {
            writableDatabase.delete(TABLE_VACUNAS, "$COL_VACUNA_ID = ?", arrayOf(id.toString()))
        } catch (e: SQLException) {
            Log.e(tagVacunas, "Error al eliminar la vacuna $id", e)
            -1
        }
    }

    /** Convierte la fila actual del cursor en un objeto Vacuna. */
    private fun Cursor.aVacuna(): Vacuna = Vacuna(
        id = getInt(getColumnIndexOrThrow(COL_VACUNA_ID)),
        nombre = getString(getColumnIndexOrThrow(COL_VACUNA_NOMBRE)),
        fechaAplicacion = getString(getColumnIndexOrThrow(COL_VACUNA_FECHA)),
        notas = getString(getColumnIndexOrThrow(COL_VACUNA_NOTAS)) ?: ""
    )

    // CRUD Citas

    private val tagCitas = "DatabaseHelper.Citas"

    /** CREATE: inserta una cita. Devuelve el id generado, o -1 si ocurrió un error. */
    fun insertarCita(cita: Cita): Long {
        return try {
            val valores = ContentValues().apply {
                put(COL_CITA_MOTIVO, cita.motivo)
                put(COL_CITA_FECHA_HORA, cita.fechaHora)
                put(COL_CITA_VETERINARIA, cita.veterinaria)
            }
            writableDatabase.insertOrThrow(TABLE_CITAS, null, valores)
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al insertar la cita", e)
            -1L
        }
    }

    /** READ (lista): devuelve todas las citas ordenadas por fecha y hora (de la más antigua a la más lejana). */
    fun obtenerCitas(): List<Cita> {
        val citas = mutableListOf<Cita>()
        try {
            readableDatabase.query(
                TABLE_CITAS, null, null, null, null, null, "$COL_CITA_FECHA_HORA ASC"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    citas.add(cursor.aCita())
                }
            }
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al consultar las citas", e)
        }
        return citas
    }

    /** READ (detalle): devuelve la cita con ese id, o null si no existe o hubo un error. */
    fun obtenerCitaPorId(id: Int): Cita? {
        return try {
            readableDatabase.query(
                TABLE_CITAS, null, "$COL_CITA_ID = ?", arrayOf(id.toString()),
                null, null, null
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.aCita() else null
            }
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al consultar la cita $id", e)
            null
        }
    }

    /** UPDATE: modifica la cita con el mismo id. Devuelve filas afectadas (0 si no existe, -1 si hubo error). */
    fun actualizarCita(cita: Cita): Int {
        return try {
            val valores = ContentValues().apply {
                put(COL_CITA_MOTIVO, cita.motivo)
                put(COL_CITA_FECHA_HORA, cita.fechaHora)
                put(COL_CITA_VETERINARIA, cita.veterinaria)
            }
            writableDatabase.update(
                TABLE_CITAS, valores, "$COL_CITA_ID = ?", arrayOf(cita.id.toString())
            )
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al actualizar la cita ${cita.id}", e)
            -1
        }
    }

    /** DELETE: elimina (cancela) la cita con ese id. Devuelve filas afectadas (0 si no existe, -1 si hubo error). */
    fun eliminarCita(id: Int): Int {
        return try {
            writableDatabase.delete(TABLE_CITAS, "$COL_CITA_ID = ?", arrayOf(id.toString()))
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al eliminar la cita $id", e)
            -1
        }
    }

    /**
     * Regla de negocio: no puede haber dos citas en la misma fecha y hora.
     * Devuelve true si ya existe otra cita en [fechaHora] (formato yyyy-MM-dd HH:mm).
     *
     * @param excluirId id de la cita que se está editando, para que no choque consigo misma (0 al agregar).
     */
    fun existeCitaEnFechaHora(fechaHora: String, excluirId: Int = 0): Boolean {
        return try {
            readableDatabase.query(
                TABLE_CITAS, arrayOf(COL_CITA_ID),
                "$COL_CITA_FECHA_HORA = ? AND $COL_CITA_ID != ?",
                arrayOf(fechaHora, excluirId.toString()),
                null, null, null, "1"
            ).use { cursor -> cursor.moveToFirst() }
        } catch (e: SQLException) {
            Log.e(tagCitas, "Error al verificar el horario $fechaHora", e)
            false
        }
    }

    /** Convierte la fila actual del cursor en un objeto Cita. */
    private fun Cursor.aCita(): Cita = Cita(
        id = getInt(getColumnIndexOrThrow(COL_CITA_ID)),
        motivo = getString(getColumnIndexOrThrow(COL_CITA_MOTIVO)),
        fechaHora = getString(getColumnIndexOrThrow(COL_CITA_FECHA_HORA)),
        veterinaria = getString(getColumnIndexOrThrow(COL_CITA_VETERINARIA))
    )
}
