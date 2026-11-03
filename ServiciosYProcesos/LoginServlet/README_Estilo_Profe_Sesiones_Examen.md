# README — Estilo del profe (Sesiones + Servlet + Thymeleaf)

Basado en el código real de clase:

- `Session.java` → guardar un valor la primera vez y reutilizarlo
- `TresEnRaya.java` → **base del examen**: parámetros URL + array/estado en sesión + Thymeleaf

El 3 en raya es el modelo. El resto de ejercicios son **el mismo esquema** con otra lógica.

---

## 1. El patrón del profe (memoriza esto)

```text
1. Leer parámetros con request.getParameter(...)
2. Si faltan los necesarios → sendError(400) o mensaje
3. Sacar el estado de sesión: getAttribute("Nombre")
4. Si es null → crear estado inicial y setAttribute
5. Aplicar la jugada / acción sobre el estado
6. Volver a setAttribute (si el objeto es mutable a veces basta con modificarlo)
7. Preparar variables de vista (Strings, números…)
8. Thymeleaf: WebContext + TemplateEngine del ServletContext + process(...)
```

### Esqueleto vacío (plantilla de examen)

```java
@WebServlet("/ejercicio")
public class EjercicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1) Parámetros
        String p = req.getParameter("x");
        // if (p == null) { resp.sendError(400, "falta x"); return; }

        // 2) Sesión + estado
        HttpSession session = req.getSession();
        // TipoEstado estado = (TipoEstado) session.getAttribute("Estado");
        // if (estado == null) { estado = ...; session.setAttribute("Estado", estado); }

        // 3) Lógica
        // ...

        // 4) Vista Thymeleaf (como el profe)
        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("msg", "hola");
        resp.setContentType("text/html;charset=UTF-8");
        ((TemplateEngine) getServletContext()
                .getAttribute("templateEngine")) // o ThymeleafConstants.TEMPLATE_ENGINE_ATTR
                .process("plantilla", ctx, resp.getWriter());
    }
}
```

> En clase usan constantes (`ThymeleafConstants`, `UrlConstants`). En el examen usa **los nombres del proyecto que te den**.

### Variante aún más simple (como `Session.java`)

Sin Thymeleaf, solo el writer:

```java
resp.getWriter().println("<h1>" + nombre + "</h1>");
```

Si el profe lo acepta, es válido. Si piden plantilla, usa el bloque Thymeleaf del 3 en raya.

---

## 2. Código del profe (referencia)

### 2.1 Session — “guardar nombre una vez”

```java
@WebServlet("/session")
public class Session extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nombre;
        if (req.getSession().getAttribute("id") == null) {
            nombre = req.getParameter("nombre");
            req.getSession().setAttribute("id", nombre);
        } else {
            nombre = (String) req.getSession().getAttribute("id");
        }
        resp.getWriter().println("<h1>" + nombre + "</h1>");
    }
}
```

**Prueba:**  
`/session?nombre=Ana` → Ana  
`/session` (sin param) → sigue saliendo Ana

### 2.2 Tres en raya — la base

```text
Parámetros: marca, coordenada (1..9)
Sesión: "Tablero" = String[9] con "-"
Jugada: Tablero[coordenada - 1] = marca
Vista: fila1, fila2, fila3 como texto "X O -"
```

Idea fuerte: **un array de 9**, no matriz 3x3. Coordenada humana 1–9 → índice 0–8.

---

## 3. Cómo salir del 3 en raya hacia otros ejercicios

| 3 en raya | Generalización |
|-----------|----------------|
| `marca` | la acción / el valor a escribir |
| `coordenada` | la posición / el índice / el id |
| `String[9]` tablero | lista, contador, palabra, mapa de barcos… |
| `fila1/2/3` | variables que pasas al HTML |

Todo ejercicio de sesión del examen = **3 en raya con otro estado**.

---

## 4. Ejercicios resueltos al estilo profe

En todos: `doGet`, parámetros por URL, estado en sesión.  
Thymeleaf al final (abreviado como `render(req, resp, ctx)` mentalmente).

---

### 4.1 Contador de visitas

**URL:** `/visitas` cada vez suma 1.

```java
@WebServlet("/visitas")
public class VisitasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Integer n = (Integer) session.getAttribute("visitas");
        if (n == null) {
            n = 0;
        }
        n++;
        session.setAttribute("visitas", n);

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("visitas", n);
        resp.setContentType("text/html;charset=UTF-8");
        ((TemplateEngine) getServletContext().getAttribute("templateEngine"))
                .process("visitas", ctx, resp.getWriter());
    }
}
```

```html
<p>Visitas de esta sesión: <span th:text="${visitas}">0</span></p>
```

**Reset opcional:** `?reset=1` → `session.removeAttribute("visitas")`.

---

### 4.2 Contador + / − (como jugadas por URL)

**URL:** `/contador?op=mas` / `?op=menos`

```java
Integer n = (Integer) session.getAttribute("contador");
if (n == null) n = 0;

String op = req.getParameter("op");
if ("mas".equals(op)) n++;
if ("menos".equals(op)) n--;

session.setAttribute("contador", n);
ctx.setVariable("contador", n);
```

Misma idea que el 3 en raya: el **parámetro decide el cambio**, la sesión guarda el resultado.

---

### 4.3 Login mínimo (mezcla Session.java + control)

**URL login:** `/login?usuario=dam&clave=1234`  
**URL zona:** `/zona`  
**URL logout:** `/logout`

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String usuario = req.getParameter("usuario");
        String clave = req.getParameter("clave");

        if ("dam".equals(usuario) && "1234".equals(clave)) {
            req.getSession().setAttribute("usuario", usuario);
            resp.sendRedirect(req.getContextPath() + "/zona");
            return;
        }
        resp.getWriter().println("<h1>Login incorrecto</h1>");
    }
}
```

```java
@WebServlet("/zona")
public class ZonaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String usuario = (String) session.getAttribute("usuario");
        resp.getWriter().println("<h1>Hola " + usuario + "</h1>");
        resp.getWriter().println("<a href='" + req.getContextPath() + "/logout'>Salir</a>");
    }
}
```

```java
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
```

**Paralelo con el profe:** como `Session.java`, el atributo `"usuario"` se guarda una vez y se reutiliza.

---

### 4.4 Carrito de compra

**URLs:**
- `/carrito?add=Pan`
- `/carrito?add=Leche`
- `/carrito?vaciar=1`

```java
@WebServlet("/carrito")
public class CarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        @SuppressWarnings("unchecked")
        List<String> carrito = (List<String>) session.getAttribute("Carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("Carrito", carrito);
        }

        String add = req.getParameter("add");
        if (add != null && !add.isBlank()) {
            carrito.add(add);
        }
        if ("1".equals(req.getParameter("vaciar"))) {
            carrito.clear();
        }

        // Vista: una sola String tipo "Pan, Leche" (estilo filas del 3 en raya)
        String lista = String.join(" | ", carrito);

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("lista", lista);
        ctx.setVariable("total", carrito.size());
        resp.setContentType("text/html;charset=UTF-8");
        ((TemplateEngine) getServletContext().getAttribute("templateEngine"))
                .process("carrito", ctx, resp.getWriter());
    }
}
```

**Paralelo 3 en raya:**  
- `add` ≈ `marca`  
- la lista ≈ `Tablero`  
- `lista` en el HTML ≈ `fila1`

---

### 4.5 Ahorcado (misma idea: estado en sesión + param)

**URL:** `/ahorcado?letra=A`

Estado en sesión:
- `"palabra"` fija `"CASA"` (o solo en código)
- `"vista"` → `char[]` o `StringBuilder` con guiones
- `"vidas"` → int

```java
@WebServlet("/ahorcado")
public class AhorcadoServlet extends HttpServlet {

    private static final String PALABRA = "CASA";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();

        StringBuilder vista = (StringBuilder) session.getAttribute("Vista");
        Integer vidas = (Integer) session.getAttribute("Vidas");

        if (vista == null) {
            vista = new StringBuilder("____"); // length = PALABRA.length()
            vidas = 6;
            session.setAttribute("Vista", vista);
            session.setAttribute("Vidas", vidas);
        }

        String letraParam = req.getParameter("letra");
        if (letraParam != null && !letraParam.isBlank()) {
            char letra = Character.toUpperCase(letraParam.charAt(0));
            boolean acierto = false;
            for (int i = 0; i < PALABRA.length(); i++) {
                if (PALABRA.charAt(i) == letra) {
                    vista.setCharAt(i, letra);
                    acierto = true;
                }
            }
            if (!acierto) {
                vidas--;
                session.setAttribute("Vidas", vidas);
            }
            // Vista ya está en sesión (mutable); opcional setAttribute otra vez
        }

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("vista", vista.toString());
        ctx.setVariable("vidas", vidas);
        resp.setContentType("text/html;charset=UTF-8");
        ((TemplateEngine) getServletContext().getAttribute("templateEngine"))
                .process("ahorcado", ctx, resp.getWriter());
    }
}
```

```html
<h1 th:text="${vista}">____</h1>
<p>Vidas: <span th:text="${vidas}">6</span></p>
```

**Paralelo 3 en raya:**  
`letra` ≈ `marca` · posiciones de la palabra ≈ `coordenada` · `Vista` ≈ `Tablero`

---

### 4.6 Batalla naval (versión examen: muy simple)

No hace falta un juego completo. Nivel profe:

- Tablero 9 casillas (como el 3 en raya)
- En algunas hay barco `"B"`, resto `"~"`
- Parámetro `coordenada=1..9`
- Si hay barco → `"X"` (tocado), si no → `"O"` (agua)
- Guardar tablero del **jugador** (lo que ve) en sesión
- Barcos fijos en un array constante (no hace falta sesión para los barcos si no los modificas)

```java
@WebServlet("/naval")
public class NavalServlet extends HttpServlet {

    // Barcos fijos: hay barco en posiciones 2, 5, 8 (índice 1,4,7)
    private static final boolean[] BARCOS = {
            false, true, false,
            false, true, false,
            false, true, false
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String coord = req.getParameter("coordenada");
        if (coord == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta coordenada");
            return;
        }

        HttpSession session = req.getSession();
        String[] visible = (String[]) session.getAttribute("Naval");
        if (visible == null) {
            visible = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            session.setAttribute("Naval", visible);
        }

        int i = Integer.parseInt(coord) - 1;
        if (i >= 0 && i < 9 && visible[i].equals("-")) {
            visible[i] = BARCOS[i] ? "X" : "O"; // tocado o agua
        }

        String fila1 = visible[0] + " " + visible[1] + " " + visible[2];
        String fila2 = visible[3] + " " + visible[4] + " " + visible[5];
        String fila3 = visible[6] + " " + visible[7] + " " + visible[8];

        var webExchange = JakartaServletWebApplication
                .buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        resp.setContentType("text/html;charset=UTF-8");
        ((TemplateEngine) getServletContext().getAttribute("templateEngine"))
                .process("naval", ctx, resp.getWriter());
    }
}
```

**Es el 3 en raya del profe** cambiando solo la lógica de “qué se escribe en la casilla”.

---

### 4.7 Adivina el número

```java
Integer secreto = (Integer) session.getAttribute("Secreto");
if (secreto == null) {
    secreto = new Random().nextInt(10) + 1;
    session.setAttribute("Secreto", secreto);
}

String mensaje = "Prueba un numero (?n=)";
String ns = req.getParameter("n");
if (ns != null) {
    int n = Integer.parseInt(ns);
    if (n < secreto) mensaje = "Mayor";
    else if (n > secreto) mensaje = "Menor";
    else mensaje = "Correcto";
}
ctx.setVariable("mensaje", mensaje);
```

---

### 4.8 Lista de mensajes (guestbook)

```java
@SuppressWarnings("unchecked")
List<String> msgs = (List<String>) session.getAttribute("Msgs");
if (msgs == null) {
    msgs = new ArrayList<>();
    session.setAttribute("Msgs", msgs);
}
String msg = req.getParameter("msg");
if (msg != null && !msg.isBlank()) {
    msgs.add(msg);
}
ctx.setVariable("todo", String.join(" / ", msgs));
```

---

### 4.9 Piedra, papel o tijera + marcador

```java
Integer ptsUser = (Integer) session.getAttribute("PtsUser");
Integer ptsCpu  = (Integer) session.getAttribute("PtsCpu");
if (ptsUser == null) { ptsUser = 0; ptsCpu = 0; }

String jugada = req.getParameter("jugada"); // piedra|papel|tijera
String[] ops = {"piedra", "papel", "tijera"};
String cpu = ops[new Random().nextInt(3)];
String resultado = "Empate";

if (jugada != null) {
    if (jugada.equals(cpu)) resultado = "Empate";
    else if (
        (jugada.equals("piedra") && cpu.equals("tijera")) ||
        (jugada.equals("papel") && cpu.equals("piedra")) ||
        (jugada.equals("tijera") && cpu.equals("papel"))
    ) {
        resultado = "Ganas";
        ptsUser++;
    } else {
        resultado = "Pierdes";
        ptsCpu++;
    }
    session.setAttribute("PtsUser", ptsUser);
    session.setAttribute("PtsCpu", ptsCpu);
}
ctx.setVariable("resultado", resultado);
ctx.setVariable("marcador", ptsUser + " - " + ptsCpu);
```

---

### 4.10 Semáforo

```java
String color = (String) session.getAttribute("Semaforo");
if (color == null) color = "ROJO";

// cada visita avanza
if ("ROJO".equals(color)) color = "AMBAR";
else if ("AMBAR".equals(color)) color = "VERDE";
else color = "ROJO";

session.setAttribute("Semaforo", color);
ctx.setVariable("color", color);
```

---

### 4.11 Cola de espera

```java
@SuppressWarnings("unchecked")
List<String> cola = (List<String>) session.getAttribute("Cola");
if (cola == null) {
    cola = new ArrayList<>();
    session.setAttribute("Cola", cola);
}
String nuevo = req.getParameter("nuevo");
if (nuevo != null && !nuevo.isBlank()) cola.add(nuevo);
if ("1".equals(req.getParameter("atender")) && !cola.isEmpty()) {
    cola.remove(0);
}
ctx.setVariable("cola", String.join(", ", cola));
```

---

## 5. Tabla “si el 3 en raya era X, el ejercicio es Y”

| Ejercicio | Parámetro(s) | Atributo sesión | Qué modificas |
|-----------|--------------|-----------------|---------------|
| 3 en raya | `marca`, `coordenada` | `Tablero` String[9] | una casilla |
| Naval | `coordenada` | `Naval` String[9] | agua/tocado |
| Contador | `op` o ninguno | `visitas` Integer | +1 / ±1 |
| Carrito | `add`, `vaciar` | `Carrito` List | add/clear |
| Ahorcado | `letra` | `Vista`, `Vidas` | guiones y vidas |
| Login | `usuario`, `clave` | `usuario` String | set una vez |
| Adivina | `n` | `Secreto` Integer | solo comparar |
| Mensajes | `msg` | `Msgs` List | add |
| PPT | `jugada` | `PtsUser`, `PtsCpu` | marcador |
| Semáforo | — | `Semaforo` String | ciclo colores |
| Cola | `nuevo`, `atender` | `Cola` List | add/remove(0) |

---

## 6. Thymeleaf al estilo profe (HTML)

Como `tresEnRaya.html`:

```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Ejercicio</title>
</head>
<body>
    <h1 th:text="${fila1}">-</h1>
    <h1 th:text="${fila2}">-</h1>
    <h1 th:text="${fila3}">-</h1>
    <!-- o un solo valor -->
    <p th:text="${msg}">mensaje</p>
</body>
</html>
```

No hace falta CSS ni JS para aprobar.

---

## 7. Errores que el profe puede penalizar

| Error | Por qué |
|-------|---------|
| No crear el tablero/lista si es `null` | NPE a la primera |
| Usar coordenada sin `-1` | `ArrayIndexOutOfBounds` |
| No validar parámetros | NPE / 500 |
| Olvidar `setAttribute` tras crear | cada request empieza de cero |
| Redirect y poner datos solo en request | se pierden |
| Castear mal (`String[]` vs `List`) | ClassCastException |

---

## 8. Plan de estudio con el código del profe

1. Ejecutar y entender **`Session.java`** (5 min)  
2. Ejecutar y entender **`TresEnRaya.java`** línea a línea (15 min)  
3. Copiar `TresEnRaya` y convertirlo en **naval** o **contador** (20 min)  
4. Hacer **carrito** o **lista de mensajes** (15 min)  
5. **Login + logout** si te sobra tiempo (15 min)

---

## 9. Chuleta final (estilo profe)

```text
param = req.getParameter("...")
if (param == null) sendError(400)

session = req.getSession()
estado = session.getAttribute("X")
if (estado == null) {
    estado = valorInicial
    session.setAttribute("X", estado)
}

// modificar estado (como Tablero[i] = marca)

// preparar Strings para la vista (fila1, fila2, ...)

WebContext ctx = ...
ctx.setVariable("fila1", fila1)
templateEngine.process("plantilla", ctx, resp.getWriter())
```

---

**Conclusión:** el examen no es “inventa un juego”. Es **repite el patrón del 3 en raya** con otro nombre de parámetros y otro estado en sesión.

Si te dan batalla naval, ahorcado o carrito, piensa:

> “¿Cuál es mi `Tablero` y cuál es mi `coordenada`/`marca`?”

Eso es lo que el profe está evaluando.
