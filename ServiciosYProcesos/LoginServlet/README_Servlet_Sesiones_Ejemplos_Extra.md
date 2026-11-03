# Más ejemplos fáciles — Servlets + Sesión (2º DAM)

Examen corto. El profe dijo que es **fácil** y que con sesiones se saca rápido.  
Aquí van **muchos enunciados simples**, casi todos se resuelven con el mismo esquema.

---

## El esquema que se repite siempre (memoriza esto)

```text
1. getParameter("...")
2. getSession()
3. leer atributo (si null → valor inicial)
4. cambiar el valor
5. setAttribute(...)
6. forward a la página
```

Código base:

```java
HttpSession session = request.getSession();

// leer param
String p = request.getParameter("x");

// leer sesión
Integer n = (Integer) session.getAttribute("n");
if (n == null) n = 0;

// lógica
// ...

session.setAttribute("n", n);
request.getRequestDispatcher("/pagina.html").forward(request, response);
```

Si controlas esto, casi todos los ejemplos de abajo son el mismo con otro nombre.

---

## Grupo A — Números en sesión (los más fáciles)

### A1. Contador +1
Cada visita a `/a1` suma 1.

### A2. Contador +1 / −1
- `/a2?op=mas` → +1  
- `/a2?op=menos` → −1  

### A3. Acumulador
`/a3?n=4` suma ese número al total de sesión.

### A4. Multiplicador
Empieza en 1. `/a4?n=2` multiplica. Varias veces: 1→2→6→24…

### A5. Máximo guardado
`/a5?n=10` guarda el número si es mayor que el actual en sesión.

### A6. Mínimo guardado
Igual que A5 pero guardando el menor.

### A7. Media sencilla
Guarda `suma` y `cantidad`. Cada `/a7?n=5` actualiza y muestra media = suma/cantidad.

### A8. Contador con tope
Suma 1 hasta 10; si llega a 10 muestra “fin” y no suma más.

### A9. Dos contadores
`visitas` y `clics` (por ejemplo un enlace `?click=1`).

### A10. Reset genérico
Cualquier ejercicio anterior con `?reset=1` → `removeAttribute` o poner a 0.

---

## Grupo B — Textos y listas (muy típico)

### B1. Última palabra
`/b1?msg=hola` guarda el último mensaje y lo muestra.

### B2. Historial de mensajes
Lista en sesión. Cada `?msg=...` se añade. Pintar con `th:each`.

### B3. Historial con máximo 5
Si hay 5, borra el más antiguo (o no dejes añadir).

### B4. Lista sin duplicados
Solo añade si no está ya en la lista.

### B5. Contar palabras distintas
Añade mensajes y muestra `lista.size()`.

### B6. Mayúsculas
Guarda el texto en mayúsculas: `msg.toUpperCase()`.

### B7. Nombre de usuario en sesión
`/b7?nombre=Ana` → “Hola Ana” en todas las páginas hasta que cambie.

### B8. Concatenar frase
Cada `?palabra=sol` añade al final de una frase en sesión.

### B9. Borrar un ítem por índice
`/b9?del=2` elimina la posición 2 de la lista.

### B10. Vaciar lista
`/b10?clear=1` → `list.clear()`.

---

## Grupo C — Login y permisos (versión fácil)

### C1. Login fijo
Usuario `dam` clave `1234`. Si OK → sesión con `usuario`.

### C2. Página solo si hay sesión
Si no hay `usuario` → redirect a login.

### C3. Logout
`invalidate()` y volver al inicio.

### C4. Mensaje de error
Login fallido → `request.setAttribute("error", "...")` y forward al form.

### C5. Contar logins fallidos
En sesión `fallos`. Si llega a 3, mostrar “cuenta bloqueada” (solo en esa sesión).

### C6. Recordar el último usuario que intentó entrar
Aunque falle, guardar `ultimoIntento` en sesión.

### C7. Zona admin / zona user
Si `usuario` es `admin` muestra enlace extra; si no, página normal.

---

## Grupo D — Elecciones y preferencias

### D1. Color favorito
`/d1?color=azul` y la página usa ese color (texto o clase CSS).

### D2. Idioma
`?lang=es` o `en`. Mostrar “Hola” / “Hello” según sesión.

### D3. Tamaño de fuente
`?size=grande|normal|peque` guardado en sesión.

### D4. Modo claro/oscuro
`?tema=dark` alterna o fija el tema.

### D5. Ciudad
`?ciudad=Madrid` y mostrar “Tu ciudad: Madrid”.

---

## Grupo E — Juegos simples (estilo 3 en raya)

### E1. Tres en raya (como en clase)
Tablero en sesión + `?fila=&col=`.

### E2. Tres en raya con reset
`?reset=1` limpia tablero y turno.

### E3. Solo marcar casillas (sin ganar)
Versión aún más fácil: al hacer click se pone “X” siempre. Solo para practicar sesión + URL.

### E4. Ahorcado fácil
Palabra fija `CASA`. `?letra=A`. 6 vidas.

### E5. Ahorcado con lista de letras usadas
Además de vidas, lista `usadas` en sesión.

### E6. Número secreto 1–10
`?n=7` → mayor / menor / correcto.

### E7. Número secreto con límite de intentos
Máximo 4 intentos; si no, “perdiste”.

### E8. Piedra papel tijera
`?jugada=piedra`. CPU random. Marcador en sesión.

### E9. Moneda (cara/cruz)
`?eleccion=cara`. Acumular aciertos.

### E10. Dado
Cada visita tira un dado 1–6 y guarda el **histórico** de tiradas en una lista.

### E11. Dos dados
Suma de dos dados; guardar la última tirada y el total de partidas.

### E12. Encuentra la pareja (muy simple)
2x2 de cartas; `?i=0` revela. Estado array en sesión. Sin animaciones.

### E13. Semáforo
Cada click cambia ROJO→AMBAR→VERDE→ROJO.

### E14. Semáforo automático de pasos
Contador 0..2 mapeado a colores.

### E15. “Topa el número”
Hay un número secreto de casilla 0–8. `?casilla=3` acierta o no. Un solo acierto gana.

---

## Grupo F — Formularios mínimos (si cae POST)

### F1. Form nombre → sesión
POST `nombre` → guardar → mostrar saludo.

### F2. Form suma
Dos campos `a` y `b` → resultado (puede ir también a sesión como historial).

### F3. Form + lista
Campo texto → añadir a lista en sesión → mostrar tabla.

### F4. Login por POST
Igual que C1 pero con form `method="post"`.

### F5. Encuesta sí/no
`voto=si|no` y contadores `votosSi` / `votosNo` en sesión (por usuario, no global del servidor).

---

## Grupo G — Pequeñas lógicas de “negocio”

### G1. Carrito de 3 productos fijos
Enlaces: añadir Pan, Leche, Huevos. Lista en sesión.

### G2. Carrito con cantidad
Si añades Pan y ya estaba, `cantidad++` (Map o clase simple).

### G3. Precio total
Cada producto tiene precio fijo; mostrar total.

### G4. Cupón
`?cupon=DAM10` aplica “descuento” booleano en sesión.

### G5. Stock inventado
Quedan 5 unidades; al comprar resta 1 en sesión.

### G6. Agenda del día
Añadir horas libres (`?hora=10:00`) a una lista.

### G7. Asistencia
`?nombre=Luis` añade a lista de asistentes.

### G8. Turno de cola
Cada `?nuevo=Ana` añade a cola; `?atender=1` quita el primero.

### G9. Like counter
Un solo botón “me gusta” que suma en sesión.

### G10. Página + “tiempo” ficticio
Cada acción suma 5 minutos a un reloj de sesión (solo un int).

---

## Grupo H — Combinados (siguen siendo fáciles)

### H1. Login + contador privado
Solo si hay sesión, el contador suma.

### H2. Login + carrito
Sin login puedes ver; al añadir exige usuario en sesión.

### H3. Juego + puntuación máxima
Guardar `record` en sesión si superas la puntuación.

### H4. Idioma + saludo personalizado
`nombre` + `lang` en sesión.

### H5. Tres en raya + “jugador actual” en título
Mostrar “Turno de X” desde sesión.

---

## Grupo I — Enunciados “tal cual” podrían aparecer

Copia-pega de estilo examen:

1. **“Crea un servlet que cuente cuántas veces el usuario ha entrado y lo muestre.”**  
2. **“Guarda en sesión una lista de productos añadidos por parámetro add.”**  
3. **“Implementa login con usuario fijo y una página que solo se vea si hay sesión.”**  
4. **“Adivina un número del 1 al 20 guardado en sesión.”**  
5. **“Haz un contador con botones + y − usando parámetros.”**  
6. **“Guarda el color elegido por el usuario y muéstralo en la página.”**  
7. **“Simula un carrito: añadir, listar y vaciar.”**  
8. **“Tres en raya: recibe fila y columna por URL y pinta el tablero.”**  
9. **“Ahorcado con palabra fija e intentos en sesión.”**  
10. **“Logout que invalide la sesión y vuelva al inicio.”**  
11. **“Lista de mensajes; no admitir vacíos.”**  
12. **“Contador que se reinicia con ?reset=true.”**  
13. **“Piedra papel tijera con marcador.”**  
14. **“Formulario POST que guarde el nombre y muestre Hola, nombre.”**  
15. **“Cola: añadir personas y atender (quitar la primera).”**  

---

## Grupo J — Variaciones de una sola línea (para ganar velocidad)

Partiendo de un contador, cambia solo la lógica:

| Si el profe pide… | Cambias esto |
|-------------------|--------------|
| Que reste | `n--` |
| Que sume el param | `n += Integer.parseInt(...)` |
| Que no baje de 0 | `if (n < 0) n = 0` |
| Que al llegar a 5 muestre fin | `if (n >= 5) mensaje = "fin"` |
| Que guarde un texto | `String` en vez de `Integer` |
| Que guarde varios | `List` + `add` |
| Que borre todo | `invalidate()` o `removeAttribute` |

---

## Snippets de copia rápida

### Crear lista si no existe

```java
@SuppressWarnings("unchecked")
List<String> lista = (List<String>) session.getAttribute("lista");
if (lista == null) {
    lista = new ArrayList<>();
}
```

### Parseo seguro

```java
String p = request.getParameter("n");
if (p != null && !p.isBlank()) {
    int n = Integer.parseInt(p);
}
```

### Toggle booleano

```java
Boolean on = (Boolean) session.getAttribute("on");
if (on == null) on = false;
on = !on;
session.setAttribute("on", on);
```

### Alternar turno X/O

```java
String turno = (String) session.getAttribute("turno");
if (turno == null) turno = "X";
turno = turno.equals("X") ? "O" : "X";
session.setAttribute("turno", turno);
```

### Redirect con context path

```java
response.sendRedirect(request.getContextPath() + "/index.html");
```

---

## Qué estudiar esta noche (prioridad)

| Prioridad | Qué |
|-----------|-----|
| 1 | Contador + reset |
| 2 | Lista (add + clear) |
| 3 | Login + logout |
| 4 | Un juego por URL (3 en raya o número secreto) |
| 5 | Un form POST simple |

Con **prioridad 1–4** cubres casi cualquier examen “fácil” de 1 hora.

---

## Recordatorio

No hace falta memorizar 50 ejercicios. Hace falta reconocer el patrón:

> **“Me piden recordar algo entre peticiones” → sesión**  
> **“Me lo mandan por el navegador” → getParameter**  
> **“Lo tengo que ver en pantalla” → forward + Thymeleaf/HTML**

Ánimo: a nivel 2º DAM y con tiempo corto, van a premiar que el flujo de sesión esté claro, no que el juego sea perfecto.
