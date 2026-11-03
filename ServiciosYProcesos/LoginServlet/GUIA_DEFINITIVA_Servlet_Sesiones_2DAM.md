# Guía definitiva — Servlets, Sesiones y Thymeleaf (2º DAM)

**Objetivo:** suspender cero en un examen corto (≈1 h) centrado en **sesiones**.  
**Stack:** Tomcat + Java (Jakarta Servlet) + Thymeleaf (o HTML servido tras el servlet).

Si el profe dice que *“con sesiones lo sacas en 15 minutos”*, el examen será una variante de:

> leer parámetro → guardar/actualizar sesión → mostrar página

Esta guía concentra **teoría, plantillas de código, trucos, errores y decenas de enunciados posibles**.

---

# PARTE 1 — Lo que tienes que entender

## 1.1 HTTP no recuerda nada

Cada click, cada URL, cada formulario es una **petición nueva**.  
El servidor, por defecto, no sabe si eres el mismo usuario que hace 5 segundos.

Para **recordar** algo entre peticiones usas:

| Herramienta | ¿Para qué? | ¿Vive dónde? |
|-------------|------------|--------------|
| Parámetros URL / form | Datos de **esta** petición | Solo ahora |
| `request.setAttribute` | Datos para la **misma** petición (forward) | Solo ahora |
| **`HttpSession`** | Datos del **usuario** entre peticiones | Hasta timeout o `invalidate` |
| Application / static | Compartido por **todos** los usuarios | Casi nunca en examen |

En el examen de 2º DAM la estrella es la **sesión**.

## 1.2 ¿Qué es la sesión?

Objeto en el **servidor** asociado a un cliente (normalmente con cookie `JSESSIONID`).

```java
HttpSession session = request.getSession();      // obtiene o crea
HttpSession session = request.getSession(true);  // igual: crea si no hay
HttpSession session = request.getSession(false); // null si no existe
```

### Operaciones que debes saber de memoria

```java
session.setAttribute("clave", valor);   // guardar / sobrescribir
Object o = session.getAttribute("clave"); // leer (puede ser null)
session.removeAttribute("clave");       // borrar una clave
session.invalidate();                   // destruir sesión entera (logout)
```

### Tipos habituales en sesión

```java
session.setAttribute("contador", 3);                 // Integer (autobox)
session.setAttribute("nombre", "Ana");               // String
session.setAttribute("lista", new ArrayList<String>()); // List
session.setAttribute("tablero", new String[3][3]);   // array
session.setAttribute("logueado", true);              // Boolean
```

Al leer, **cast** al tipo correcto:

```java
Integer c = (Integer) session.getAttribute("contador");
String nombre = (String) session.getAttribute("nombre");
@SuppressWarnings("unchecked")
List<String> lista = (List<String>) session.getAttribute("lista");
```

## 1.3 Parámetros de la petición

```java
String v = request.getParameter("nombre"); // ?nombre=Ana  o input name="nombre"
String[] varios = request.getParameterValues("item"); // checkboxes
```

Siempre String. Conversiones:

```java
int n = Integer.parseInt(request.getParameter("n"));
```

**Truco examen:** comprueba null antes de parsear.

```java
String p = request.getParameter("n");
if (p != null && !p.isBlank()) {
    int n = Integer.parseInt(p);
}
```

## 1.4 Forward vs Redirect (pregunta típica)

| | `forward` | `redirect` |
|---|-----------|------------|
| Qué es | El servidor “sigue” a otra vista **en la misma petición** | El navegador hace una **petición nueva** |
| Atributos de `request` | Se conservan | **Se pierden** |
| URL en el navegador | Puede no cambiar | Cambia |
| Uso | Mostrar resultado tras procesar | Tras POST (evitar reenvío), ir a otra ruta “limpia” |
| Datos que quieres conservar | Pueden ir en request **o** sesión | **Obligatorio sesión** (o volver a enviar params) |

```java
// Forward
request.getRequestDispatcher("/vista.html").forward(request, response);

// Redirect (¡context path!)
response.sendRedirect(request.getContextPath() + "/inicio");
```

**Regla de oro:** si haces `redirect` y quieres recordar algo → **sesión**.

## 1.5 Ciclo mental del ejercicio perfecto

```text
┌─────────────┐
│  Navegador  │  URL ?x=1  o  form POST
└──────┬──────┘
       ▼
┌─────────────┐
│   Servlet   │  getParameter
│             │  getSession
│             │  leer / crear estado
│             │  lógica
│             │  setAttribute (sesión)
└──────┬──────┘
       ▼
┌─────────────┐
│  Thymeleaf  │  muestra ${session.xxx} o ${request.xxx}
│  / HTML     │
└─────────────┘
```

## 1.6 Thymeleaf mínimo para examen

```html
<!-- valor de sesión -->
<p th:text="${session.contador}">0</p>
<p th:text="${session.usuario}">invitado</p>

<!-- lista -->
<ul>
  <li th:each="item : ${session.carrito}" th:text="${item}">...</li>
</ul>

<!-- condicional -->
<p th:if="${session.usuario != null}">Logueado</p>
<p th:unless="${session.usuario != null}">No logueado</p>

<!-- enlace con params -->
<a th:href="@{/juego(fila=1,col=2)}">Jugar 1,2</a>
<a th:href="@{/logout}">Salir</a>
```

Si el profe pasa variables por request:

```java
request.setAttribute("mensaje", "OK");
```

```html
<p th:text="${mensaje}">...</p>
```

---

# PARTE 2 — Plantilla de Servlet para copiar en el examen

```java
package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/accion")
public class AccionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // 1) Parámetros
        String param = request.getParameter("x");

        // 2) Estado desde sesión (ejemplo contador)
        Integer contador = (Integer) session.getAttribute("contador");
        if (contador == null) {
            contador = 0;
        }

        // 3) Lógica (cambiar según enunciado)
        contador++;

        // 4) Guardar
        session.setAttribute("contador", contador);

        // 5) Ir a la vista
        request.getRequestDispatcher("/resultado.html").forward(request, response);
    }
}
```

**Cambia solo el bloque 3** según el enunciado. El resto es siempre igual.

---

# PARTE 3 — Snippets de oro (copia y adapta)

## 3.1 Contador

```java
Integer n = (Integer) session.getAttribute("n");
if (n == null) n = 0;
n++;
session.setAttribute("n", n);
```

## 3.2 Contador con + y −

```java
String op = request.getParameter("op");
Integer n = (Integer) session.getAttribute("n");
if (n == null) n = 0;
if ("mas".equals(op)) n++;
if ("menos".equals(op)) n--;
session.setAttribute("n", n);
```

## 3.3 Acumular número de la URL

```java
Integer total = (Integer) session.getAttribute("total");
if (total == null) total = 0;
String p = request.getParameter("n");
if (p != null && !p.isBlank()) {
    total += Integer.parseInt(p);
}
session.setAttribute("total", total);
```

## 3.4 Lista: crear / añadir / vaciar

```java
@SuppressWarnings("unchecked")
List<String> lista = (List<String>) session.getAttribute("lista");
if (lista == null) lista = new ArrayList<>();

String add = request.getParameter("add");
if (add != null && !add.isBlank()) {
    lista.add(add);
}
if ("1".equals(request.getParameter("clear"))) {
    lista.clear();
}
session.setAttribute("lista", lista);
```

## 3.5 Borrar por índice

```java
String del = request.getParameter("del");
if (del != null) {
    int i = Integer.parseInt(del);
    if (i >= 0 && i < lista.size()) {
        lista.remove(i);
    }
}
```

## 3.6 Login fijo

```java
String u = request.getParameter("usuario");
String c = request.getParameter("clave");
if ("dam".equals(u) && "1234".equals(c)) {
    session.setAttribute("usuario", u);
    response.sendRedirect(request.getContextPath() + "/privada");
    return;
}
request.setAttribute("error", "Login incorrecto");
request.getRequestDispatcher("/login.html").forward(request, response);
```

## 3.7 Comprobar login

```java
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("usuario") == null) {
    response.sendRedirect(request.getContextPath() + "/login.html");
    return;
}
```

## 3.8 Logout

```java
HttpSession session = request.getSession(false);
if (session != null) {
    session.invalidate();
}
response.sendRedirect(request.getContextPath() + "/login.html");
```

## 3.9 Toggle boolean

```java
Boolean on = (Boolean) session.getAttribute("on");
if (on == null) on = false;
on = !on;
session.setAttribute("on", on);
```

## 3.10 Alternar turno X / O

```java
String turno = (String) session.getAttribute("turno");
if (turno == null) turno = "X";
turno = "X".equals(turno) ? "O" : "X";
session.setAttribute("turno", turno);
```

## 3.11 Tablero 3x3 vacío

```java
String[][] tablero = (String[][]) session.getAttribute("tablero");
if (tablero == null) {
    tablero = new String[3][3];
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            tablero[i][j] = "";
        }
    }
}
```

## 3.12 Jugada en tablero

```java
String fs = request.getParameter("fila");
String cs = request.getParameter("col");
if (fs != null && cs != null) {
    int f = Integer.parseInt(fs);
    int c = Integer.parseInt(cs);
    if (f >= 0 && f < 3 && c >= 0 && c < 3 && tablero[f][c].isEmpty()) {
        tablero[f][c] = turno; // turno de sesión
        // alternar turno...
    }
}
```

## 3.13 Número aleatorio secreto

```java
Integer secreto = (Integer) session.getAttribute("secreto");
if (secreto == null) {
    secreto = new java.util.Random().nextInt(100) + 1; // 1..100
    session.setAttribute("secreto", secreto);
    session.setAttribute("intentos", 0);
}
```

## 3.14 Reset selectivo

```java
if ("1".equals(request.getParameter("reset"))) {
    session.removeAttribute("tablero");
    session.removeAttribute("turno");
    // o session.invalidate();
}
```

## 3.15 Mensaje flash (un solo uso)

```java
// al procesar
session.setAttribute("flash", "Guardado");
response.sendRedirect(request.getContextPath() + "/home");

// en /home
String flash = (String) session.getAttribute("flash");
if (flash != null) {
    request.setAttribute("flash", flash);
    session.removeAttribute("flash");
}
```

---

# PARTE 4 — Trucos de examen

1. **Si “no se guarda”** → olvidaste `setAttribute` después de modificar el objeto/lista.  
2. **Si la lista “vuelve vacía”** → creaste `new ArrayList<>()` siempre; solo créala si `getAttribute` es null.  
3. **Si tras redirect no hay datos** → estaban en `request`, no en `session`.  
4. **Si 404 en enlaces** → falta `request.getContextPath()`.  
5. **Si NumberFormatException** → parámetro null o vacío; valida antes de `parseInt`.  
6. **Si ClassCastException** → guardaste un tipo y leíste otro (ej. int vs Integer no es el problema; sí List vs String).  
7. **Si NPE en logout** → usaste la sesión después de `invalidate()`.  
8. **Un solo servlet, muchas acciones** → un parámetro `op` o varios `if` sobre params distintos.  
9. **No hace falta base de datos** en este tipo de examen.  
10. **No hace falta JS** para lo básico: enlaces `?param=valor` bastan (como el 3 en raya).

### Orden de escritura en el folio / IDE (15 min)

```text
1. @WebServlet + clase
2. doGet/doPost → procesar()
3. getSession
4. getParameter
5. getAttribute + if null
6. lógica de 5–15 líneas
7. setAttribute
8. forward
9. HTML/Thymeleaf mínimo que muestre el atributo
```

---

# PARTE 5 — Banco de enunciados posibles

## 5.A Números (muy probables si es “fácil”)

| # | Enunciado corto |
|---|-----------------|
| 1 | Contador de visitas en sesión |
| 2 | Contador con ?op=mas y ?op=menos |
| 3 | Sumar el valor de ?n al total de sesión |
| 4 | Multiplicar el valor de sesión por ?n (inicio 1) |
| 5 | Guardar el máximo número enviado |
| 6 | Guardar el mínimo número enviado |
| 7 | Media: guardar suma y cantidad |
| 8 | Contador que no pase de 10 |
| 9 | Contador con reset ?reset=1 |
| 10 | Dos contadores independientes (a y b) |

## 5.B Listas y textos

| # | Enunciado corto |
|---|-----------------|
| 11 | Guardar el último mensaje (?msg=) |
| 12 | Historial de mensajes en List |
| 13 | No añadir mensajes vacíos |
| 14 | Máximo 5 mensajes |
| 15 | No duplicados |
| 16 | Borrar por índice ?del=0 |
| 17 | Vaciar lista ?clear=1 |
| 18 | Concatenar palabras en una frase |
| 19 | Guardar nombre y mostrar “Hola, X” |
| 20 | Lista de tareas add/del |

## 5.C Login / sesión de usuario

| # | Enunciado corto |
|---|-----------------|
| 21 | Login usuario/clave fijos |
| 22 | Página privada solo con sesión |
| 23 | Logout con invalidate |
| 24 | Mensaje error login fallido |
| 25 | Contar fallos de login en sesión |
| 26 | Mostrar nombre del usuario logueado |
| 27 | Redirect a login si no hay sesión |

## 5.D Preferencias

| # | Enunciado corto |
|---|-----------------|
| 28 | Color favorito en sesión |
| 29 | Idioma es/en |
| 30 | Tema claro/oscuro |
| 31 | Tamaño de letra |
| 32 | Ciudad guardada |

## 5.E Juegos (estilo clase)

| # | Enunciado corto |
|---|-----------------|
| 33 | Tres en raya por URL fila/col |
| 34 | Tres en raya + reset |
| 35 | Tres en raya + mostrar turno |
| 36 | Marcar casillas solo con X (sin ganar) |
| 37 | Número secreto 1–50 mayor/menor |
| 38 | Número secreto con máx. intentos |
| 39 | Ahorcado palabra fija |
| 40 | Ahorcado + letras usadas |
| 41 | Piedra papel tijera + marcador |
| 42 | Cara o cruz + aciertos |
| 43 | Dado + historial de tiradas |
| 44 | Semáforo ROJO/AMBAR/VERDE |
| 45 | Encuentra la casilla secreta 0–8 |

## 5.F Carrito y colas

| # | Enunciado corto |
|---|-----------------|
| 46 | Carrito: add producto por URL |
| 47 | Carrito: listar y vaciar |
| 48 | Carrito: 3 productos fijos con enlaces |
| 49 | Cola: añadir persona |
| 50 | Cola: atender (quitar el primero) |
| 51 | Votos sí/no acumulados en sesión |

## 5.G Formularios POST

| # | Enunciado corto |
|---|-----------------|
| 52 | Form nombre → sesión → saludo |
| 53 | Form login POST |
| 54 | Form texto → añadir a lista |
| 55 | Form dos números → mostrar suma (y opcional historial) |

## 5.H Combinados

| # | Enunciado corto |
|---|-----------------|
| 56 | Login + contador solo si hay sesión |
| 57 | Login + carrito |
| 58 | Juego + record de puntuación en sesión |
| 59 | Idioma + nombre |
| 60 | Flash message tras redirect |

---

# PARTE 6 — 8 ejercicios resueltos (modelo de respuesta)

## Ejercicio 1 — Contador

**Enunciado:** Cada GET a `/contador` incrementa y muestra el valor.

```java
@WebServlet("/contador")
public class ContadorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer n = (Integer) session.getAttribute("contador");
        if (n == null) n = 0;
        n++;
        session.setAttribute("contador", n);
        req.getRequestDispatcher("/contador.html").forward(req, resp);
    }
}
```

## Ejercicio 2 — Carrito mínimo

```java
@WebServlet("/carrito")
public class CarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        @SuppressWarnings("unchecked")
        List<String> carrito = (List<String>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        String add = req.getParameter("add");
        if (add != null && !add.isBlank()) carrito.add(add);
        if ("1".equals(req.getParameter("vaciar"))) carrito.clear();

        session.setAttribute("carrito", carrito);
        req.getRequestDispatcher("/carrito.html").forward(req, resp);
    }
}
```

## Ejercicio 3 — Login + privada + logout

Ya cubierto en snippets 3.6–3.8. Tres servlets o rutas: `/login`, `/privada`, `/logout`.

## Ejercicio 4 — Adivina el número

```java
Integer secreto = (Integer) session.getAttribute("secreto");
if (secreto == null) {
    secreto = new java.util.Random().nextInt(10) + 1;
    session.setAttribute("secreto", secreto);
}
String ps = request.getParameter("n");
String mensaje = "Introduce un número";
if (ps != null) {
    int n = Integer.parseInt(ps);
    if (n < secreto) mensaje = "Mayor";
    else if (n > secreto) mensaje = "Menor";
    else mensaje = "¡Correcto!";
}
request.setAttribute("mensaje", mensaje);
```

## Ejercicio 5 — Tres en raya (esqueleto)

1. Tablero en sesión (snippet 3.11)  
2. Turno en sesión (snippet 3.10)  
3. Leer fila/col (snippet 3.12)  
4. setAttribute tablero + turno  
5. forward  

Comprobar ganador es **extra**; muchos exámenes solo piden colocar fichas.

## Ejercicio 6 — Ahorcado (esqueleto)

```java
String palabra = "JAVA"; // fija
StringBuilder vista = (StringBuilder) session.getAttribute("vista");
Integer vidas = (Integer) session.getAttribute("vidas");
if (vista == null) {
    vista = new StringBuilder("_".repeat(palabra.length()));
    vidas = 6;
}
String letra = request.getParameter("letra");
if (letra != null && letra.length() == 1) {
    char ch = Character.toUpperCase(letra.charAt(0));
    boolean ok = false;
    for (int i = 0; i < palabra.length(); i++) {
        if (palabra.charAt(i) == ch) {
            vista.setCharAt(i, ch);
            ok = true;
        }
    }
    if (!ok) vidas--;
}
session.setAttribute("vista", vista);
session.setAttribute("vidas", vidas);
```

## Ejercicio 7 — Preferencia de color

```java
String color = request.getParameter("color");
if (color != null) {
    session.setAttribute("color", color);
}
// en HTML: th:style="'color:' + ${session.color}"
```

## Ejercicio 8 — Cola de atención

```java
@SuppressWarnings("unchecked")
List<String> cola = (List<String>) session.getAttribute("cola");
if (cola == null) cola = new ArrayList<>();
String nuevo = request.getParameter("nuevo");
if (nuevo != null && !nuevo.isBlank()) cola.add(nuevo);
if ("1".equals(request.getParameter("atender")) && !cola.isEmpty()) {
    cola.remove(0);
}
session.setAttribute("cola", cola);
```

---

# PARTE 7 — Qué priorizar esta noche

| Orden | Tema | Tiempo sugerido |
|------:|------|-----------------|
| 1 | Contador + reset | 10 min |
| 2 | Lista add / clear / del | 15 min |
| 3 | Login + logout + página privada | 15 min |
| 4 | Un juego URL (3 en raya **o** número secreto) | 20 min |
| 5 | Repasar forward vs redirect + null checks | 10 min |

Con 1–4 estás alineado con: *“si entiendes sesiones, 15 minutos”*.

---

# PARTE 8 — Chuleta de última hora (una página)

```text
session = request.getSession()
param   = request.getParameter("x")

obj = session.getAttribute("k")
if (obj == null) → inicializar

// ... lógica ...

session.setAttribute("k", obj)
request.getRequestDispatcher("/vista.html").forward(request, response)

LOGIN OK  → setAttribute("usuario", u) + redirect
LOGOUT    → invalidate() + redirect
PRIVADO   → si no hay usuario → redirect login
LISTA     → if null new ArrayList; add; setAttribute
RESET     → removeAttribute o invalidate
REDIRECT  → response.sendRedirect(request.getContextPath() + "/ruta")
PARSE     → null check antes de Integer.parseInt
```

---

# PARTE 9 — Mentalidad de examen

- No inventes arquitectura.  
- No uses BD salvo que lo pidan.  
- Un servlet bien hecho > cinco clases vacías.  
- Si no te sale el ganador del 3 en raya, **coloca fichas y turno**: ya demuestras sesión.  
- Comenta en una línea qué guardas en sesión: el corrector lo agradece.

---

**Frase final:**  
El examen fácil de sesiones no evalúa si eres ingeniero de software; evalúa si sabes que **el estado va en `HttpSession`**, se actualiza con parámetros, y se muestra con un **forward** a la vista.

¡Ánimo mañana!
