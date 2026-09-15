# Tarea 1.1: Análisis y diseño de una Interfaz de Comandos

## Apartado 1. Descripción de la aplicación

* **Nombre de la aplicación:** `SysAdmin Shell` (Consola de Administración de Sistemas basada en PowerShell / Bash).
* **Objetivo principal:** Permitir la interacción directa con el sistema operativo para gestionar archivos, procesos y servicios mediante la ejecución rápida de comandos de texto.
* **Tipo de usuario:** Administradores de sistemas, desarrolladores de software y técnicos informáticos.
* **Funciones principales:**
    * Navegación y manipulación del sistema de archivos (crear, copiar, mover y borrar carpetas o ficheros).
    * Inspección y control de procesos del sistema en ejecución.
    * Gestión de permisos de archivos y configuración de red.
    * Ejecución y automatización de scripts de tareas.

---

## Apartado 2. Diseño de la interfaz

```text
================================================================================
  SysAdmin Shell v2.4 (x64)                          [Host: LOCALHOST | OK]
================================================================================
 [SISTEMA] Usuario: admin@dev-machine | Directorio: /Users/admin/Projects
 [SISTEMA] Escribe 'help' para ver la lista de comandos disponibles.
--------------------------------------------------------------------------------
 ZONA DE RESULTADOS / MENSAJES AL USUARIO:

  $ ls -la
  drwxr-xr-x   4 admin  staff   128B Sep 15 10:00 .
  drwxr-xr-x  10 admin  staff   320B Sep 15 09:30 ..
  -rw-r--r--   1 admin  staff   2.4K Sep 15 09:55 app.js
  -rw-r--r--   1 admin  staff   512B Sep 15 10:01 config.json

--------------------------------------------------------------------------------
 ZONA DE INTRODUCCIÓN DE COMANDOS:
 admin@dev-machine:/Projects$ mkdir backups█
================================================================================

```
### Componentes de la interfaz
* **Información contextual:** Cabecera superior que muestra la versión de la consola, estado del host, usuario activo y ruta del directorio de trabajo actual.
* **Zona de resultados y mensajes:** Área central dedicada a imprimir las respuestas a los comandos, listados de archivos, confirmaciones o errores.
* **Zona de introducción de comandos:** El *prompt* o línea de entrada (`admin@dev-machine:/Projects$`) junto al cursor parpadeante (`█`) donde el usuario redacta las instrucciones.

---

## Apartado 3. Flujo de uso

### Ejemplo 1: Consultar los archivos del directorio
* **Acción del usuario:** Quiere listar los archivos del directorio actual con sus detalles de permisos y tamaño. Escribe:
  ```bash
  ls -la
  ```
    * **Respuesta del sistema:** Procesa la orden y genera el listado estructurado en la zona de resultados.
* **Resultado:** El usuario visualiza la lista completa de ficheros y directorios.

### Ejemplo 2: Crear un nuevo directorio
* **Acción del usuario:** Necesita crear una carpeta llamada `backups`. Escribe:
  ```bash
  mkdir backups
  ```
    * **Respuesta del sistema:** Ejecuta la creación del directorio en segundo plano y devuelve el control al prompt sin mostrar errores.
* **Resultado:** La carpeta `backups` queda creada físicamente en el sistema de archivos.

### Ejemplo 3: Intentar acceder a un directorio inexistente (Error)
* **Acción del usuario:** Comete un fallo tipográfico al intentar cambiar de directorio:
  ```bash
  cd carpetas
  ```
    * **Respuesta del sistema:** Imprime un mensaje explícito de error en la zona de resultados:
  ```text
  [ERROR] cd: no such file or directory: carpetas
  ```
    * **Resultado:** El usuario identifica el problema de inmediato y corrige el nombre de la ruta.

---

## Apartado 4. Reflexión

### Ventajas de una interfaz de comandos (CLI)
Una consola o terminal ofrece una velocidad de ejecución excepcional y consume una cantidad mínima de recursos de memoria y CPU. Además, facilita la automatización masiva de tareas complejas mediante el uso de scripts.

### Inconvenientes frente a una interfaz gráfica (GUI)
Presenta una curva de aprendizaje elevada al requerir la memorización de comandos y sintaxis exactas. Resulta poco intuitiva para usuarios no técnicos y es más propensa a errores por fallos de escritura.

### Situaciones de uso
* **Interfaz de comandos (CLI):** Recomendada en administración remota de servidores (SSH), automatización de tareas, gestión de proyectos de desarrollo y sistemas con recursos reducidos.
* **Interfaz gráfica (GUI):** Ideal para usuarios finales, entorno de oficina cotidiano, consumo multimedia y diseño gráfico donde la interacción visual sea prioritaria.