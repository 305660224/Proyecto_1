# Documento Técnico – PataAlDía (Proyecto 1, Móvil 1)

## 1. Integrantes
1. Integrante 1 – Módulo Mascotas: _nombre_
2. Integrante 2 – Módulo Salud (Vacunas): _nombre_
3. Integrante 3 – Módulo Agenda (Citas): _nombre_
4. Integrante 4 – Ensamble, estética, UX y entregables: _nombre_

## 2. Objetivo
Desarrollar una app Android nativa (Kotlin, Android Studio) que permita llevar el control de mascotas, su historial de vacunación y sus citas veterinarias, con persistencia local en SQLite y CRUD completo en cada módulo.

## 3. Descripción de la aplicación
PataAlDía abre con una pantalla Splash (logo + nombre + eslogan) y pasa a una pantalla de Inicio con descripción y accesos a los tres módulos: Mascotas, Vacunas y Citas. Cada módulo ofrece listado (ListView que se refresca en `onResume()`), formulario de agregar con validación, detalle con botón "Regresar", eliminación con AlertDialog de confirmación, y edición con datos precargados. Se usa una paleta verde (identidad) con acentos azul (Vacunas) y naranja (Citas) definida en `colors.xml` / `themes.xml`.

## 4. Diseño de la base de datos
Archivo `PataAlDia.db`, gestionado por `DatabaseHelper : SQLiteOpenHelper`.

| Tabla | Columnas |
|---|---|
| Mascotas | id (PK autoinc), nombre (NOT NULL), especie (NOT NULL), edad (INT), peso (REAL), notas |
| Vacunas | ver `DatabaseHelper.kt` (`TABLE_VACUNAS`) |
| Citas | ver `DatabaseHelper.kt` (`TABLE_CITAS`) |

_Insertar captura de DB Browser for SQLite (estructura y registros)._

## 5. Descripción del CRUD
Cada entidad tiene en `DatabaseHelper`: `insertar*`, `obtener*` (lista), `obtener*PorId`, `actualizar*`, `eliminar*`. Create = formulario Agregar; Read = Listado + Detalle; Update = Editar (UPDATE por id, sin duplicar); Delete = botón Eliminar + AlertDialog + Toast.

## 6. Capturas
Splash, Inicio, 3 listas, 3 detalles, 3 formularios de agregar, una edición, una alerta de eliminación, y DB Browser. _(pegar aquí)_

## 7. Dificultades
- Integración de ramas de tres integrantes en un solo proyecto (conflictos en `DatabaseHelper.kt` e `InicioActivity.kt`).
- Mantener un estilo visual coherente entre módulos desarrollados por separado (se centralizó en `colors.xml`/`themes.xml`).
- Validación de formularios y refresco del ListView tras agregar/editar/eliminar (`onResume()`).
- _Agregar las propias del equipo._
