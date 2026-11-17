package com.example.proyecto_1_pataaldia

/**
 * Clase de datos (Data Class) que modela el registro de una Mascota
 * dentro del sistema PataAlDía.
 *
 * @property id Identificador único autogenerado en SQLite (por defecto 0 antes de insertar).
 * @property nombre Nombre de la mascota (obligatorio).
 * @property especie Especie de la mascota, ej. Perro, Gato, Ave (obligatorio).
 * @property edad Edad de la mascota en años (obligatorio).
 * @property peso Peso de la mascota en kilogramos.
 * @property notas Observaciones o notas adicionales sobre la mascota.
 */
data class Mascota(
    val id: Int = 0,
    val nombre: String,
    val especie: String,
    val edad: Int,
    val peso: Double,
    val notas: String
)
