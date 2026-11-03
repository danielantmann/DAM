# Guía rápida — Servlets + Sesiones + Thymeleaf (examen)

Documento de repaso para un examen corto (≈1 h) con **Tomcat**, **Java (Jakarta Servlet)** y **Thymeleaf**.  
Nivel orientado a lo visto en clase: parámetros por URL, estado en sesión y pintar en pantalla.

---

## 1. Guía genérica (lo que tienes que dominar)

### 1.1 ¿Qué es una sesión?

HTTP es **sin estado**: cada petición llega “sola”.  
La **sesión** permite recordar datos del mismo usuario entre peticiones (tablero, login, carrito…).

- Se identifica con una cookie `JSESSIONID` (Tomcat lo gestiona).
- En el servidor: objeto `HttpSession`.

```java
HttpSession session = request.getSession();      // crea si no existe
HttpSession session = request.getSession(false); // null si no hay sesión
```

### 1.2 Operaciones básicas de sesión

```java
// Guardar
session.setAttribute("clave", valor);

// Leer (puede ser null)
Object o = session.getAttribute("clave");
String nombre = (String) session.getAttribute("usuario");

// Borrar un dato
session.removeAttribute("clave");

// Cerrar sesión entera (logout)
session.invalidate();
```

**Importante:** después de `invalidate()` no uses la sesión otra vez en la misma petición.

### 1.3 Leer datos de la petición

```java
// URL: /juego?fila=1&col=2
String fila = request.getParameter("fila");
String col  = request.getParameter("col");

// Formulario POST (name="usuario")
String usuario = request.getParameter("usuario");
```

Los parámetros siempre llegan como **String**. Convierte si hace falta:

```java
int n = Integer.parseInt(request.getParameter("n"));
```

### 1.4 Responder: forward vs redirect

| Método | Qué hace | ¿Se conservan atributos de `request`? | Uso típico |
|--------|----------|----------------------------------------|------------|
| `forward` | Misma petición → va a una vista | Sí | Mostrar página tras procesar |
| `redirect` | El navegador hace **otra** petición GET | No (se pierden) | Evitar reenvío de formulario; usar **sesión** para datos |

```java
// Forward (muy habitual en examen)
request.getRequestDispatcher("/vista.html").forward(request, response);

// Redirect
response.sendRedirect(request.getContextPath() + "/inicio");
```

### 1.5 Estructura típica de un Servlet de examen

```java
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

        // 1) leer parámetros
        // 2) leer/actualizar sesión
        // 3) setAttribute
        // 4) forward a Thymeleaf/HTML

        request.getRequestDispatcher("/resultado.html").forward(request, response);
    }
}
```

### 1.6 Thymeleaf — lo mínimo

Si el profe configura Thymeleaf para leer la sesión:

```html
<p th:text="${session.usuario}">usuario</p>
<p th:text="${session.contador}">0</p>

<ul>
  <li th:each="item : ${session.carrito}" th:text="${item}">item</li>
</ul>
```

Si el servlet pone datos en el **request**:

```java
request.setAttribute("mensaje", "Hola");
```

```html
<p th:text="${mensaje}">...</p>
```

Condiciones y enlaces:

```html
<p th:if="${session.usuario != null}" th:text="${session.usuario}">...</p>
<a th:href="@{/logout}">Salir</a>
<a th:href="@{/jugar(fila=1,col=2)}">Casilla 1,2</a>
```

### 1.7 Checklist mental en el examen (2 minutos)

1. ¿De dónde salen los datos? → `getParameter`
2. ¿Qué debo recordar entre peticiones? → `session`
3. ¿Cómo se muestra? → `forward` + plantilla
4. ¿Hay reset/logout? → `removeAttribute` o `invalidate`
5. ¿Null-safe? → comprobar `getAttribute(...) != null`

---

## 2. Plantilla reutilizable de “estado en sesión”

Casi todos los ejercicios son variantes de esto:

```java
HttpSession session = request.getSession();

MiEstado estado = (MiEstado) session.getAttribute("estado");
if (estado == null) {
    estado = new MiEstado(); // valor inicial
}

// modificar estado según parámetros
estado.actualizar(request.getParameter("x"));

session.setAttribute("estado", estado);
request.getRequestDispatcher("/vista.html").forward(request, response);
```

Puedes usar:
- un `int` / `String`
- una `List<String>`
- un `boolean[][]` (tablero)
- una clase propia (`Partida`, `Carrito`, `Ahorcado`)

---

## 3. Ejemplos tipo examen

Cada ejemplo incluye: idea, URL de prueba, código mínimo y qué pintar.

---

### Ejemplo 1 — Contador de visitas / clics

**Enunciado posible:** cada vez que se entra en `/contador`, suma 1 y muestra el total.

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

```html
<p>Has entrado <span th:text="${session.contador}">0</span> veces.</p>
<a th:href="@{/contador}">Sumar otra</a>
```

**Reset:** `/contador?reset=1` → `session.removeAttribute("contador")`.

---

### Ejemplo 2 — Suma por URL

**URL:** `/suma?n=5` acumula en sesión.

```java
String param = request.getParameter("n");
int suma = session.getAttribute("suma") == null ? 0 : (Integer) session.getAttribute("suma");
if (param != null) {
    suma += Integer.parseInt(param);
}
session.setAttribute("suma", suma);
```

Pruebas: `/suma?n=2` → `/suma?n=3` → total 5.

---

### Ejemplo 3 — Login mínimo con sesión

**Formulario** (`login.html`):

```html
<form method="post" action="/login">
  <input name="usuario" />
  <input name="clave" type="password" />
  <button type="submit">Entrar</button>
</form>
```

**Servlet:**

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String user = req.getParameter("usuario");
        String pass = req.getParameter("clave");

        if ("admin".equals(user) && "1234".equals(pass)) {
            req.getSession().setAttribute("usuario", user);
            resp.sendRedirect(req.getContextPath() + "/privada");
        } else {
            req.setAttribute("error", "Credenciales incorrectas");
            req.getRequestDispatcher("/login.html").forward(req, resp);
        }
    }
}
```

**Zona privada:**

```java
@WebServlet("/privada")
public class PrivadaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }
        req.getRequestDispatcher("/privada.html").forward(req, resp);
    }
}
```

**Logout:**

```java
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login.html");
    }
}
```

---

### Ejemplo 4 — Carrito de la compra (lista en sesión)

**Añadir:** `/carrito?add=Pan`

```java
@WebServlet("/carrito")
public class CarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        @SuppressWarnings("unchecked")
        List<String> carrito = (List<String>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        String add = req.getParameter("add");
        if (add != null && !add.isBlank()) {
            carrito.add(add);
        }
        if ("1".equals(req.getParameter("vaciar"))) {
            carrito.clear();
        }

        session.setAttribute("carrito", carrito);
        req.getRequestDispatcher("/carrito.html").forward(req, resp);
    }
}
```

```html
<ul>
  <li th:each="p : ${session.carrito}" th:text="${p}">producto</li>
</ul>
<a th:href="@{/carrito(add='Leche')}">Añadir Leche</a>
<a th:href="@{/carrito(vaciar=1)}">Vaciar</a>
```

---

### Ejemplo 5 — Lista de nombres / “guestbook”

Igual que el carrito pero con mensajes:

- `/libro?msg=Hola`
- lista `List<String>` en sesión
- `th:each` en la vista

Extra típico: límite de 10 mensajes o no añadir vacíos.

---

### Ejemplo 6 — Tres en raya (como en clase, jugadas por URL)

**Idea:** tablero `String[3][3]` o `char[3][3]` en sesión.  
**URL:** `/tres?fila=0&col=1`

```java
HttpSession session = request.getSession();
String[][] tablero = (String[][]) session.getAttribute("tablero");
if (tablero == null) {
    tablero = new String[3][3];
    for (int i = 0; i < 3; i++)
        Arrays.fill(tablero[i], "");
}

String turno = (String) session.getAttribute("turno");
if (turno == null) turno = "X";

String f = request.getParameter("fila");
String c = request.getParameter("col");
if (f != null && c != null) {
    int fila = Integer.parseInt(f);
    int col  = Integer.parseInt(c);
    if (tablero[fila][col].isEmpty()) {
        tablero[fila][col] = turno;
        turno = turno.equals("X") ? "O" : "X";
    }
}

session.setAttribute("tablero", tablero);
session.setAttribute("turno", turno);
request.getRequestDispatcher("/tres.html").forward(request, response);
```

En HTML: tabla 3×3 con enlaces:

```html
<a th:href="@{/tres(fila=0,col=0)}">-</a>
```

**Reset:** `session.removeAttribute("tablero"); session.removeAttribute("turno");`

---

### Ejemplo 7 — Ahorcado

**Estado en sesión:**
- `palabraSecreta` (String)
- `aciertos` (por ejemplo `char[]` o `String` con guiones)
- `intentos` (int)
- `letrasUsadas` (`List<Character>` o `String`)

**URL:** `/ahorcado?letra=a`

Lógica resumida:

```java
String letra = request.getParameter("letra");
if (letra != null && letra.length() == 1) {
    char ch = Character.toLowerCase(letra.charAt(0));
    // si la letra está en la palabra → revelar posiciones
    // si no → intentos--
    // guardar todo en sesión
}
```

Vista:
- palabra oculta: `_ a _ a`
- intentos restantes
- letras usadas
- enlaces A–Z o un formulario con una letra

**Ganar/perder:** comprobar si no quedan `_` o `intentos == 0`.

---

### Ejemplo 8 — Número secreto (adivina)

Al crear sesión:

```java
int secreto = new Random().nextInt(100) + 1;
session.setAttribute("secreto", secreto);
session.setAttribute("intentos", 0);
```

**URL:** `/adivina?n=42`

```java
int secreto = (Integer) session.getAttribute("secreto");
int n = Integer.parseInt(request.getParameter("n"));
int intentos = (Integer) session.getAttribute("intentos") + 1;
session.setAttribute("intentos", intentos);

String msg;
if (n < secreto) msg = "Mayor";
else if (n > secreto) msg = "Menor";
else msg = "¡Correcto en " + intentos + " intentos!";
request.setAttribute("mensaje", msg);
```

---

### Ejemplo 9 — Quiz / test de preguntas

- `indice` de pregunta en sesión
- `puntos` en sesión
- cada `/quiz?resp=B` comprueba, suma punto, avanza índice
- al final muestra puntuación

Estructura de datos posible:

```java
List<String> preguntas = List.of("¿2+2?", "¿Capital de Francia?");
List<String> correctas = List.of("4", "Paris");
```

---

### Ejemplo 10 — Semáforo / máquina de estados

Estados: `ROJO → AMBAR → VERDE → ROJO`  
Cada visita a `/semaforo` cambia al siguiente y lo guarda en sesión.

Útil para ver que la sesión guarda un **enum** o un `String` de estado.

---

### Ejemplo 11 — Preferencia de usuario (tema / idioma)

```java
String idioma = request.getParameter("lang");
if (idioma != null) {
    session.setAttribute("idioma", idioma);
}
```

La plantilla elige textos según `${session.idioma}`.

---

### Ejemplo 12 — “Flash message” (mensaje de un solo uso)

```java
// Tras una acción
session.setAttribute("flash", "Guardado OK");
response.sendRedirect(request.getContextPath() + "/home");

// En /home
String flash = (String) session.getAttribute("flash");
if (flash != null) {
    request.setAttribute("flash", flash);
    session.removeAttribute("flash");
}
```

---

### Ejemplo 13 — Multiplicar acumulado

Como el contador, pero:

- `/calc?op=mul&n=3` multiplica el valor de sesión
- `/calc?op=reset` vuelve a 1

Entrena `getParameter` + varios comandos en un solo servlet.

---

### Ejemplo 14 — Piedra, papel o tijera

- Usuario: `/ppt?jugada=piedra`
- Servidor elige aleatorio
- Guarda en sesión marcador `usuario` / `cpu`
- Muestra última jugada + marcador

---

### Ejemplo 15 — Tablero de notas con borrar

- Lista en sesión
- `/notas?add=Comprar pan`
- `/notas?del=0` borra por índice

```java
String del = request.getParameter("del");
if (del != null) {
    int i = Integer.parseInt(del);
    if (i >= 0 && i < lista.size()) lista.remove(i);
}
```

---

## 4. Errores frecuentes en examen

| Error | Qué pasa | Solución |
|-------|----------|----------|
| No usar sesión | Cada request parte de cero | `getSession` + `setAttribute` |
| `ClassCastException` | Casteaste mal el attribute | Mismo tipo al guardar y al leer |
| `NumberFormatException` | `parseInt` de null o texto | Comprobar param != null |
| Redirect y pierdes datos | Los pusiste solo en `request` | Guardar en sesión o usar forward |
| NPE tras `invalidate` | Sigues usando la session | Redirect inmediato tras logout |
| Olvidar `forward` | Servlet hace la lógica y pantalla en blanco | Siempre ir a una vista |
| Context path mal | 404 en enlaces | `request.getContextPath()` |

---

## 5. Esquema de respuesta “de oro” (escribir en el examen)

```text
1. Leer parámetros de la URL/formulario
2. Obtener sesión
3. Recuperar estado (o crear uno nuevo si es null)
4. Aplicar la lógica (jugada, alta, login…)
5. Guardar estado en sesión
6. Forward a la plantilla Thymeleaf
```

Si te piden logout/reset:

```text
7. removeAttribute o invalidate
8. Redirect al inicio
```

---

## 6. Mini enunciados para practicar (15–20 min cada uno)

1. Contador con reset por URL  
2. Carrito: add + vaciar + listar  
3. Login fijo (`admin/1234`) + página privada + logout  
4. Adivina número 1–50 con mensajes mayor/menor  
5. Tres en raya 3×3 solo con enlaces (como en clase)  
6. Ahorcado de una palabra fija (`JAVA`) con 6 intentos  
7. Lista de tareas: añadir y borrar por índice  

Si te salen el **1, 2, 3 y 5**, vas sobrado para un examen de una hora centrado en sesiones.

---

## 7. Recordatorio final

No necesitas una arquitectura grande. En este tipo de examen gana quien hace bien:

**`getParameter` → sesión → actualizar → `setAttribute` → `forward` → Thymeleaf**

Eso es el 3 en raya de clase generalizado a carrito, login, ahorcado, contador, etc.

¡Ánimo con el examen!
