*Dificultad:* intermedia.

### Objetivos

- Gestionar objetos almacenados en sesiones.
- Controlar estados mediante atributos de objetos.
- Validar parámetros numéricos.
- Implementar operaciones de reserva y cancelación.
- Controlar las condiciones de acceso a los servlets.

### Enunciado

Se desea desarrollar una aplicación web para gestionar reservas de entradas de cine.

Cada usuario podrá crear una sesión de reserva y seleccionar películas y asientos.

La aplicación deberá disponer de los siguientes servlets:

*1. CrearReserva (/crear)*

Recibe:

- pelicula: nombre de la película.
- sesion: hora de la sesión.
- plazas: número total de plazas disponibles.

Se deberán validar los parámetros y comprobar que el número de plazas sea un entero positivo.

Se creará un objeto Reserva y se almacenará en la sesión.

*2. ReservarEntrada (/reservar)*

Recibe:

- asiento: número del asiento que se desea reservar.

Condiciones:

- Debe existir una sesión con una reserva configurada.
- El número de asiento debe ser un entero válido.
- El asiento debe estar dentro del rango disponible.
- No se podrá reservar un asiento que ya esté ocupado.

Si la reserva se realiza correctamente, el asiento quedará ocupado.

*3. CancelarEntrada (/cancelar)*

Recibe:

- asiento: número del asiento que se desea liberar.

Condiciones:

- Debe existir una reserva configurada.
- El asiento debe ser válido y estar reservado.
- Si se cumplen las condiciones, se liberará el asiento.

*4. ConsultarReserva (/consultar)*

Devuelve:

- Nombre de la película.
- Hora de la sesión.
- Número total de plazas.
- Número de plazas ocupadas.
- Número de plazas disponibles.

### Requisitos adicionales

- Cada usuario tendrá su propia reserva.
- No se podrá reservar ni cancelar antes de crear una reserva.
- No se podrán realizar operaciones sobre asientos inexistentes.
- Se deberán controlar los errores de conversión numérica.
- Las modificaciones deberán mantenerse entre peticiones.