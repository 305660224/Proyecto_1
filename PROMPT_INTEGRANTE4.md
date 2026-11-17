# Prompt para Claude (Integrante 4 – pruebas, capturas y BD)

Tienes control total de mi computadora. Proyecto Android Studio "PataAlDía" (Kotlin, SQLite) en `C:\movil1\Proyecto_1`, ya con los 3 módulos integrados (Mascotas, Vacunas, Citas). Compila con `./gradlew assembleDebug` (JAVA_HOME = `C:\Program Files\Android\Android Studio\jbr`, SDK en `%LOCALAPPDATA%\Android\Sdk`). Haz TODO esto tú mismo, sin pedirme que haga nada manual:

1. Arranca un emulador (AVD ya instalado, usa `emulator -list-avds`) o dispositivo con `adb devices`. Instala el APK (`adb install -r app/build/outputs/apk/debug/app-debug.apk`) y ábrela.
2. Prueba cada botón de la app e ingresa datos reales:
   - Mascota: nombre "Toby", especie Perro, edad 3, peso 12.5, notas "Labrador, muy activo".
   - Vacuna: "Rabia" para Toby, con fecha y notas.
   - Cita: "Control anual" para Toby, con fecha y hora.
   Verifica también: validación de campos vacíos, Toast al guardar, lista que se refresca sola, botón "Regresar al listado", editar sin duplicar, eliminar con AlertDialog de confirmación. Si algo falla, corrígelo en el código, recompila y repite.
3. Capturas con `adb exec-out screencap -p > archivo.png`, guardadas en `C:\movil1\Proyecto_1\capturas\` con nombres numerados: Splash, Inicio, 3 Listas, 3 Detalles, 3 Formularios de Agregar, una Edición, una alerta de Eliminación.
4. Base de datos: extrae `PataAlDia.db` con `adb exec-out run-as com.example.proyecto_1_pataaldia cat databases/PataAlDia.db > capturas/PataAlDia.db` (equivale al Device File Explorer). Ábrela en "DB Browser for SQLite" (o, si no está instalado, con `sqlite3`/Python) y captura la estructura de las 3 tablas y los registros insertados (Toby, Rabia, Control anual).
5. Completa `DOCUMENTO_TECNICO.md`: inserta las capturas (rutas relativas), el diseño real de las tablas Vacunas y Citas leyendo `DatabaseHelper.kt`, y una sección de dificultades. Luego genera `DOCUMENTO_TECNICO.docx` (con las imágenes) usando el skill docx.
6. Genera el zip final para Moodle `C:\movil1\Proyecto_1_entrega.zip` con solo el código fuente (`git ls-files` + documento + capturas; sin `build/`, `.gradle/`, `.git/`).
7. Haz commit en una rama `entregables` y repórtame qué quedó hecho y qué no pudiste verificar.

No borres archivos fuera del proyecto.
