## MODELO 3 – Gestor de tareas personales

*Dificultad:* intermedia-alta.

### Objetivos

- Diseñar clases de modelo con varios atributos.
- Gestionar colecciones de objetos dentro de sesiones.
- Implementar operaciones de alta, consulta y modificación.
- Validar parámetros y controlar estados.
- Gestionar errores HTTP de forma adecuada.

### Enunciado

Se desea desarrollar una aplicación web que permita a cada usuario gestionar sus propias tareas pendientes.

Cada usuario dispondrá de una lista de tareas independiente asociada a su sesión.

Cada tarea tendrá:

- id: identificador numérico.
- titulo: título de la tarea.
- descripcion: descripción.
- prioridad: número del 1 al 3.
- completada: indica si la tarea está finalizada.

La aplicación deberá disponer de los siguientes servlets:

*1. CrearLista (/crear)*

Recibe:

- maxTareas: número máximo de tareas que podrá almacenar el usuario.

Se deberá validar que el parámetro sea un entero positivo.

Se creará una lista de tareas y se almacenará en la sesión.

*2. AnadirTarea (/anadir)*

Recibe:

- titulo
- descripcion
- prioridad

Condiciones:

- Debe existir una sesión y una lista configurada.
- No se podrán superar las tareas máximas permitidas.
- El título y la descripción no pueden estar vacíos.
- La prioridad debe ser un número entero entre 1 y 3.
- El identificador se asignará automáticamente.

*3. CompletarTarea (/completar)*

Recibe:

- id: identificador de la tarea.

Busca la tarea correspondiente y modifica su estado para marcarla como completada.

Si el identificador no es válido o no existe ninguna tarea con ese ID, deberá responder con un error HTTP adecuado.

*4. ConsultarTarea (/consultar)*

Recibe:

- id: identificador de la tarea.

Devuelve toda la información de la tarea solicitada.

*5. ConsultarLista (/lista)*

Devuelve un resumen de todas las tareas del usuario, incluyendo:

- Número total de tareas.
- Número de tareas completadas.
- Número de tareas pendientes.

### Requisitos adicionales

- Cada usuario tendrá su propia lista de tareas.
- Los identificadores no se podrán repetir.
- No se podrán consultar ni modificar tareas antes de configurar la lista.
- Se deberán validar todos los parámetros.
- Se deberán gestionar los errores de conversión y de búsqueda.
- Los cambios realizados en las tareas deberán mantenerse entre peticiones.