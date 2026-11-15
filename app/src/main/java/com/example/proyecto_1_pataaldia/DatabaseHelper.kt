package com.example.proyecto_1_pataaldia

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    // CRUD Citas
}
