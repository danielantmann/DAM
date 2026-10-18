# Chuleta práctica de Hilos en Java — 2.º DAM (PSP)

Guía de consulta para **implementar ejercicios de examen**. Incluye plantillas de código y patrones de los apuntes y de los simulacros: `Thread`, `Runnable`, sincronización, locks, semáforos, colas, pools, `Future`, `CompletableFuture`, coordinación, errores y cierre.

> **Cómo usarla en el examen:** identifica primero qué problema tienes (contador compartido, tareas que devuelven resultados, recurso limitado, productor-consumidor o tareas asíncronas) y salta a la sección correspondiente. Copia la estructura, adapta los nombres y cantidades, compila y prueba.

---

## Índice rápido

1. [Elegir la herramienta adecuada](#1-elegir-la-herramienta-adecuada)
2. [Crear hilos: `Thread` y `Runnable`](#2-crear-hilos-thread-y-runnable)
3. [Esperar y detener hilos](#3-esperar-y-detener-hilos)
4. [Datos compartidos: `synchronized`, `volatile` y atómicos](#4-datos-compartidos-synchronized-volatile-y-atómicos)
5. [`ReentrantLock` y `tryLock`](#5-reentrantlock-y-trylock)
6. [Evitar deadlocks](#6-evitar-deadlocks)
7. [`Semaphore`: recursos limitados](#7-semaphore-recursos-limitados)
8. [`wait`, `notify` y `notifyAll`](#8-wait-notify-y-notifyall)
9. [`BlockingQueue`: productor-consumidor](#9-blockingqueue-productor-consumidor)
10. [`ConcurrentHashMap`](#10-concurrenthashmap)
11. [`ExecutorService`, `Callable` y `Future`](#11-executorservice-callable-y-future)
12. [Cierre correcto de ejecutores](#12-cierre-correcto-de-ejecutores)
13. [`CountDownLatch` y `CyclicBarrier`](#13-countdownlatch-y-cyclicbarrier)
14. [`CompletableFuture`: cadenas y combinación](#14-completablefuture-cadenas-y-combinación)
15. [Errores y timeouts en `CompletableFuture`](#15-errores-y-timeouts-en-completablefuture)
16. [`ScheduledExecutorService`](#16-scheduledexecutorservice)
17. [Herramientas adicionales del tutorial](#17-herramientas-adicionales-del-tutorial)
18. [Plantillas integradoras](#18-plantillas-integradoras)
19. [Fallos típicos que revisar](#19-fallos-típicos-que-revisar)
20. [Plan de examen de 120 minutos](#20-plan-de-examen-de-120-minutos)

---

## 1. Elegir la herramienta adecuada

| El enunciado dice… | Herramienta que encaja |
|---|---|
| Crear varios hilos que imprimen o ejecutan acciones | `Thread` + `Runnable` |
| Esperar a que un hilo termine | `join()` |
| Contador u otro dato simple compartido | `AtomicInteger` o `synchronized` |
| Proteger varias operaciones sobre el mismo recurso | `synchronized` o `ReentrantLock` |
| Limitar accesos simultáneos a 2, 3, 5… recursos | `Semaphore(n)` |
| Productor y consumidores; cola que se llena/vacía | `BlockingQueue` |
| Esperar si un buffer manual está lleno/vacío | `wait()` + `notifyAll()` dentro de `synchronized` |
| Ejecutar muchas tareas con un número limitado de hilos | `ExecutorService` |
| Una tarea devuelve un resultado y se recoge después | `Callable<T>` + `Future<T>` |
| Varias operaciones asíncronas con transformaciones | `CompletableFuture` |
| Combinar dos resultados independientes | `thenCombine()` |
| Esperar a un conjunto de tareas | `allOf()`, `CountDownLatch` o `join()` según el caso |
| Esperar hasta que N hilos lleguen a un punto común | `CyclicBarrier` |
| Ejecutar algo cada cierto intervalo | `ScheduledExecutorService` |
| Compartir un mapa entre varios hilos | `ConcurrentHashMap` |

**Regla práctica:** no uses una herramienta por costumbre; usa la más directa para el requisito que pide el enunciado.

---

## 2. Crear hilos: `Thread` y `Runnable`

### Patrón recomendado: `Runnable`

```java
Runnable tarea = () -> {
    System.out.println("Ejecutando en: " + Thread.currentThread().getName());
};

Thread hilo = new Thread(tarea, "Trabajador-1");
hilo.start();
hilo.join(); // El main espera a que termine
```

También puedes implementar una clase:

```java
class Tarea implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " -> " + i);
        }
    }
}
```

```java
Thread h1 = new Thread(new Tarea(), "Hilo-1");
Thread h2 = new Thread(new Tarea(), "Hilo-2");
h1.start();
h2.start();
h1.join();
h2.join();
```

### Diferencia esencial

- `start()` inicia un hilo nuevo y hace que se ejecute `run()` en ese hilo.
- Llamar directamente a `run()` es una llamada normal: no inicia un hilo nuevo.
- `Runnable` representa una tarea; `Thread` representa el hilo que la ejecuta.
- `Thread.currentThread().getName()` permite identificar qué hilo ejecuta el código.

---

## 3. Esperar y detener hilos

### `join()`

```java
Thread[] hilos = new Thread[3];

for (int i = 0; i < hilos.length; i++) {
    final int id = i + 1;
    hilos[i] = new Thread(() -> {
        System.out.println("Empieza tarea " + id);
    });
    hilos[i].start();
}

for (Thread hilo : hilos) {
    hilo.join();
}
System.out.println("Todos han terminado");
```

### `sleep()` e interrupción

`Thread.sleep(ms)` pausa el hilo actual. Puede lanzar `InterruptedException`.

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // Restaurar la señal de interrupción
    return;                             // Salir de la tarea si procede
}
```

Patrón de trabajador que puede detenerse:

```java
Thread trabajador = new Thread(() -> {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Trabajando...");
            Thread.sleep(500);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        System.out.println("Trabajador finalizado");
    }
});

trabajador.start();
Thread.sleep(2000);
trabajador.interrupt();
trabajador.join();
```

**Importante:** `interrupt()` solicita la interrupción; no es una orden mágica que mate el hilo. La tarea debe responder cooperativamente.

### Estados que conviene reconocer

- `NEW`: creado, aún no iniciado.
- `RUNNABLE`: ejecutable o en ejecución.
- `BLOCKED`: esperando entrar en una sección `synchronized` cuyo monitor está ocupado.
- `WAITING`: espera sin límite temporal, por ejemplo `join()` sin timeout o `wait()`.
- `TIMED_WAITING`: espera temporal, por ejemplo `sleep()` o `join(timeout)`.
- `TERMINATED`: ya terminó.

Un hilo `daemon` no mantiene por sí solo viva la JVM cuando todos los hilos no-daemon han terminado. Se configura antes de llamar a `start()`:

```java
Thread monitor = new Thread(() -> {
    while (true) {
        System.out.println("Monitor activo");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});
monitor.setDaemon(true);
monitor.start();
```

---

## 4. Datos compartidos: `synchronized`, `volatile` y atómicos

### El contador inseguro

```java
class Contador {
    private int valor = 0;

    public void incrementar() {
        valor++; // NO es una operación atómica
    }

    public int getValor() {
        return valor;
    }
}
```

Si 10 hilos incrementan 10.000 veces cada uno, el resultado esperado es 100.000, pero la versión sin protección puede perder incrementos por una condición de carrera.

### Solución A: `synchronized`

```java
class Contador {
    private int valor = 0;

    public synchronized void incrementar() {
        valor++;
    }

    public synchronized int getValor() {
        return valor;
    }
}
```

También se puede proteger solo la sección crítica:

```java
public void incrementar() {
    synchronized (this) {
        valor++;
    }
}
```

Los hilos que sincronizan sobre el mismo monitor no ejecutan simultáneamente esa sección crítica.

### Solución B: `AtomicInteger`

```java
import java.util.concurrent.atomic.AtomicInteger;

class Contador {
    private final AtomicInteger valor = new AtomicInteger(0);

    public void incrementar() {
        valor.incrementAndGet();
    }

    public int getValor() {
        return valor.get();
    }
}
```

Para un contador simple, `AtomicInteger` suele ser la opción más directa.

### ¿Qué significa `volatile`?

```java
private volatile boolean activo = true;
```

`volatile` ayuda a que las lecturas y escrituras de esa variable vean el valor actualizado entre hilos. **No convierte operaciones compuestas, como `contador++`, en atómicas.** No es una solución suficiente para un contador concurrente.

### Plantilla para probar un contador

```java
Contador contador = new Contador();
Thread[] hilos = new Thread[10];

for (int i = 0; i < hilos.length; i++) {
    hilos[i] = new Thread(() -> {
        for (int j = 0; j < 10_000; j++) {
            contador.incrementar();
        }
    });
    hilos[i].start();
}

for (Thread hilo : hilos) {
    hilo.join();
}
System.out.println("Contador final: " + contador.getValor());
```

### Encapsulación de atributos

Para un inventario, por ejemplo:

```java
private int stock = 100; // private se conserva; no usar final si debe cambiar
private final ReentrantLock candado = new ReentrantLock();
```

- `private`: controla desde dónde se accede directamente al atributo.
- `final`: impide reasignar la variable; un `int final` no puede disminuir.
- Se puede conservar `private` y cambiar el atributo mediante los métodos públicos de la clase.

---

## 5. `ReentrantLock` y `tryLock`

### Plantilla segura

```java
import java.util.concurrent.locks.ReentrantLock;

class Inventario {
    private int stock = 100;
    private final ReentrantLock candado = new ReentrantLock();

    public boolean vender() {
        candado.lock();
        try {
            if (stock > 0) {
                stock--;
                return true;
            }
            return false;
        } finally {
            candado.unlock();
        }
    }

    public int getStock() {
        candado.lock();
        try {
            return stock;
        } finally {
            candado.unlock();
        }
    }
}
```

**No olvides `finally`:** aunque se ejecute un `return` o se produzca una excepción, el `unlock()` se ejecuta.

### `tryLock()` con timeout

Se utiliza cuando no quieres esperar indefinidamente por el lock:

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock = new ReentrantLock();
boolean adquirido = false;

try {
    adquirido = lock.tryLock(1, TimeUnit.SECONDS);
    if (!adquirido) {
        System.out.println("No se pudo obtener el recurso a tiempo");
        return;
    }

    // Sección crítica protegida
    System.out.println("Recurso obtenido");
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    if (adquirido) {
        lock.unlock();
    }
}
```

Solo se llama a `unlock()` si ese hilo ha adquirido el lock.

### ¿Cuál usar?

- `synchronized`: opción sencilla para proteger una sección crítica.
- `ReentrantLock`: cuando necesitas capacidades explícitas como `tryLock()` o adquisición interrumpible.
- `AtomicInteger`: para una operación atómica sencilla sobre un número.

---

## 6. Evitar deadlocks

Un **deadlock** o interbloqueo puede producirse cuando dos hilos retienen recursos que el otro necesita.

Ejemplo conceptual del problema:

- Hilo A obtiene `recursoA` y espera `recursoB`.
- Hilo B obtiene `recursoB` y espera `recursoA`.

Formas de evitarlo:

1. Obtener siempre los locks en el mismo orden global.
2. Reducir el tiempo que se mantienen bloqueos.
3. Si el enunciado lo pide, utilizar `tryLock(timeout)` y liberar los recursos adquiridos antes de reintentar.

Patrón si se necesitan dos locks con `tryLock`:

```java
boolean primero = false;
boolean segundo = false;

try {
    primero = lockA.tryLock(500, TimeUnit.MILLISECONDS);
    if (!primero) return;

    segundo = lockB.tryLock(500, TimeUnit.MILLISECONDS);
    if (!segundo) return;

    // Sección crítica que requiere ambos locks
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    if (segundo) lockB.unlock();
    if (primero) lockA.unlock();
}
```

Esta estructura libera los locks en orden inverso. No reemplaza la necesidad de diseñar correctamente el orden de adquisición en el resto del programa.

---

## 7. `Semaphore`: recursos limitados

Un semáforo limita el número de tareas que pueden usar un recurso simultáneamente. Ejemplos: tres plazas de parking, dos impresoras, cinco conexiones.

### Patrón típico: parking

```java
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

Semaphore plazas = new Semaphore(3);

Runnable coche = () -> {
    boolean permiso = false;
    try {
        plazas.acquire();
        permiso = true;

        System.out.println(Thread.currentThread().getName()
                + " aparca. Plazas libres: " + plazas.availablePermits());
        Thread.sleep(1500);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        if (permiso) {
            plazas.release();
        }
    }
};
```

### Esperar un tiempo máximo

```java
boolean permiso = false;
try {
    permiso = plazas.tryAcquire(1, TimeUnit.SECONDS);
    if (!permiso) {
        System.out.println("No hay recurso disponible a tiempo");
        return;
    }
    // Usar el recurso
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    if (permiso) plazas.release();
}
```

**Truco:** utiliza una variable booleana (`permiso`) para saber si adquiriste el permiso. Libéralo en `finally`, y solo si lo adquiriste.

---

## 8. `wait`, `notify` y `notifyAll`

Estas operaciones se usan dentro de una sección `synchronized`. `wait()` libera el monitor mientras espera. Cuando un hilo cambia la condición, usa `notifyAll()` para despertar a los hilos que esperan en ese monitor.

### Buffer manual de una posición

```java
class Buffer {
    private Integer dato = null;

    public synchronized void producir(int valor) throws InterruptedException {
        while (dato != null) { // Buffer lleno
            wait();
        }
        dato = valor;
        notifyAll();
    }

    public synchronized int consumir() throws InterruptedException {
        while (dato == null) { // Buffer vacío
            wait();
        }
        int resultado = dato;
        dato = null;
        notifyAll();
        return resultado;
    }
}
```

Ejemplo de hilos:

```java
Buffer buffer = new Buffer();

Thread productor = new Thread(() -> {
    try {
        for (int i = 1; i <= 5; i++) {
            buffer.producir(i);
            System.out.println("Producido: " + i);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

Thread consumidor = new Thread(() -> {
    try {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Consumido: " + buffer.consumir());
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

productor.start();
consumidor.start();
productor.join();
consumidor.join();
```

**Reglas para el examen:**

- `wait()`, `notify()` y `notifyAll()` deben ejecutarse con el monitor del objeto adquirido, normalmente dentro de un método `synchronized` sobre ese objeto.
- Usa `while`, no `if`, para volver a comprobar la condición tras despertarse.
- Si el ejercicio solo pide productor-consumidor y permite `BlockingQueue`, esta última suele requerir menos código y gestionar mejor la espera.

---

## 9. `BlockingQueue`: productor-consumidor

`put()` espera si la cola está llena. `take()` espera si está vacía. Una `ArrayBlockingQueue` tiene capacidad limitada.

### Plantilla completa: un productor y dos consumidores

Este ejemplo produce diez enteros y utiliza `-1` como mensaje especial de fin. Los identificadores de los datos empiezan en 1, por lo que `-1` queda reservado.

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductorConsumidor {
    private static final int FIN = -1;

    public static void main(String[] args) throws Exception {
        BlockingQueue<Integer> cola = new ArrayBlockingQueue<>(3);
        AtomicInteger completados = new AtomicInteger(0);
        int numeroConsumidores = 2;
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<?> c1 = executor.submit(() -> consumir(cola, completados, "Consumidor-1"));
        Future<?> c2 = executor.submit(() -> consumir(cola, completados, "Consumidor-2"));

        Future<?> productor = executor.submit(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    cola.put(i);
                    System.out.println("Producido: " + i);
                }
                // Una señal de fin por cada consumidor
                for (int i = 0; i < numeroConsumidores; i++) {
                    cola.put(FIN);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        productor.get();
        c1.get();
        c2.get();
        executor.shutdown();

        System.out.println("Procesados: " + completados.get());
    }

    private static void consumir(BlockingQueue<Integer> cola,
                                 AtomicInteger completados,
                                 String nombre) {
        try {
            while (true) {
                int dato = cola.take();
                if (dato == FIN) break;

                System.out.println(nombre + " procesa " + dato);
                completados.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

Esta plantilla es para el flujo normal del ejercicio. Si el enunciado exige cancelación/interrupción del productor, hay que diseñar también cómo desbloquear y detener a los consumidores en ese escenario.

### Puntos que suelen dar problemas

- No dejar consumidores bloqueados al terminar el productor.
- Poner una señal de fin por consumidor si se usa este patrón.
- Contabilizar cada elemento una sola vez.
- No confundir una cola vacía en un instante concreto con que el productor haya terminado.

---

## 10. `ConcurrentHashMap`

Úsalo para un mapa compartido por varios hilos que leen y escriben.

```java
import java.util.concurrent.ConcurrentHashMap;

ConcurrentHashMap<String, Integer> productos = new ConcurrentHashMap<>();

productos.put("P-01", 20);
productos.put("P-02", 35);

Integer stock = productos.get("P-01");
System.out.println("Stock: " + stock);

productos.putIfAbsent("P-01", 99); // Solo inserta si la clave no existe
productos.compute("P-01", (clave, valor) -> valor == null ? 1 : valor - 1);
```

- `put()`: inserta o reemplaza una clave.
- `get()`: consulta una clave.
- `putIfAbsent()`: inserta si no existía.
- `compute()` / `computeIfPresent()`: permiten actualizar un valor asociado a una clave.

Una colección concurrente ayuda a proteger sus operaciones, pero no vuelve automáticamente atómica una secuencia de varias operaciones independientes. Si hay que comprobar una condición y luego modificar varios datos coherentemente, diseña la operación de forma atómica o utiliza sincronización adecuada.

---

## 11. `ExecutorService`, `Callable` y `Future`

### Crear un pool y enviar tareas sin resultado

```java
ExecutorService executor = Executors.newFixedThreadPool(4);

for (int i = 1; i <= 20; i++) {
    final int id = i;
    executor.submit(() -> {
        System.out.println("Tarea " + id + " en " + Thread.currentThread().getName());
    });
}

executor.shutdown();
```

### `Runnable` frente a `Callable<T>`

- `Runnable`: no devuelve un resultado mediante `run()`.
- `Callable<T>`: devuelve un resultado de tipo `T` mediante `call()` y puede lanzar excepciones.
- `submit()` devuelve un `Future` incluso para un `Runnable`; para recoger un resultado calculado se usa habitualmente `Callable<T>`.

### `Callable` + `Future`

```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

ExecutorService executor = Executors.newFixedThreadPool(3);
List<Future<Integer>> futuros = new ArrayList<>();

for (int i = 1; i <= 5; i++) {
    final int n = i;
    Future<Integer> f = executor.submit(() -> {
        Thread.sleep(500);
        return n * n;
    });
    futuros.add(f);
}

int suma = 0;
for (Future<Integer> futuro : futuros) {
    int resultado = futuro.get(); // Espera si todavía no ha terminado
    System.out.println("Resultado: " + resultado);
    suma += resultado;
}
System.out.println("Suma: " + suma);
executor.shutdown();
```

`get()` en el orden de la lista puede hacer que esperes una tarea lenta aunque otras ya hayan terminado. Las tareas siguen ejecutándose concurrentemente, pero tú recoges los resultados en ese orden.

### Consultar estado sin esperar inmediatamente

```java
while (!futuro.isDone()) {
    System.out.println("Esperando...");
    Thread.sleep(250);
}
System.out.println("Resultado: " + futuro.get());
```

No hagas un bucle de espera sin pausa: imprimir continuamente puede gastar CPU y llenar la consola.

### `get()` con timeout

```java
try {
    String resultado = futuro.get(3, TimeUnit.SECONDS);
    System.out.println(resultado);
} catch (TimeoutException e) {
    System.out.println("Tarea fuera de plazo");
} catch (ExecutionException e) {
    System.out.println("La tarea ha fallado: " + e.getCause());
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

El timeout de `get(timeout, unit)` limita cuánto espera esa llamada; no significa por sí mismo que la tarea subyacente se haya detenido.

### Pools más comunes

```java
Executors.newFixedThreadPool(4);      // Número fijo de hilos
Executors.newSingleThreadExecutor();   // Una tarea a la vez
Executors.newCachedThreadPool();       // Crea/reutiliza hilos según demanda
Executors.newScheduledThreadPool(2);  // Tareas programadas
```

Para ejercicios con muchas tareas, el pool fijo facilita limitar la concurrencia.

---

## 12. Cierre correcto de ejecutores

`shutdown()` deja de aceptar nuevas tareas y permite que las enviadas terminen. `shutdownNow()` intenta detener las tareas activas y devuelve las que estaban esperando; no garantiza detener por la fuerza cualquier tarea.

### Patrón habitual

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
try {
    // Enviar tareas y recoger los resultados que se necesiten
} finally {
    executor.shutdown();
    try {
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

No llames a `shutdown()` inmediatamente si después necesitas enviar más tareas. En un examen sencillo basta muchas veces con enviar todas las tareas, esperar a que terminen y finalmente cerrar el executor.

---

## 13. `CountDownLatch` y `CyclicBarrier`

### `CountDownLatch`: esperar N finalizaciones

El hilo principal espera hasta que el contador llegue a cero.

```java
int cantidad = 5;
CountDownLatch latch = new CountDownLatch(cantidad);
ExecutorService executor = Executors.newFixedThreadPool(cantidad);

for (int i = 1; i <= cantidad; i++) {
    final int id = i;
    executor.submit(() -> {
        try {
            System.out.println("Trabajador " + id + " cargando datos");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            latch.countDown(); // Debe ejecutarse incluso si hay error
        }
    });
}

latch.await();
System.out.println("Sistema listo");
executor.shutdown();
```

`CountDownLatch` sirve para esperar una fase. Una vez llega a cero, no se reinicia.

### `CyclicBarrier`: todos esperan en el mismo punto

```java
int cantidad = 5;
CyclicBarrier barrera = new CyclicBarrier(cantidad,
        () -> System.out.println("¡Comienza la carrera!"));

ExecutorService executor = Executors.newFixedThreadPool(cantidad);
for (int i = 1; i <= cantidad; i++) {
    final int id = i;
    executor.submit(() -> {
        try {
            System.out.println("Corredor " + id + " preparado");
            barrera.await();
            Thread.sleep(500 + (long) (Math.random() * 2000));
            System.out.println("Corredor " + id + " ha terminado");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            System.out.println("La barrera se ha roto");
        }
    });
}

executor.shutdown();
```

**Ojo:** si el pool tiene menos hilos que participantes y todos los hilos del pool se quedan esperando en la barrera, los participantes pendientes no podrán empezar. El pool debe poder ejecutar a todos los participantes que necesitan llegar a la barrera.

### Diferencia esencial

- `CountDownLatch`: esperar a que N tareas completen una acción.
- `CyclicBarrier`: N participantes esperan a que todos lleguen al mismo punto.

---

## 14. `CompletableFuture`: cadenas y combinación

Importación habitual:

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
```

### A. Una cadena de transformaciones

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

CompletableFuture<Void> fin = CompletableFuture
        .supplyAsync(() -> 10, executor)
        .thenApply(n -> n * 2)
        .thenApply(n -> n + 5)
        .thenApply(String::valueOf)
        .thenAccept(resultado -> System.out.println("Resultado: " + resultado));

fin.join();
executor.shutdown();
```

Salida: `Resultado: 25`.

### B. Qué hace cada método

| Método | Uso |
|---|---|
| `supplyAsync(() -> valor, executor)` | Ejecuta una operación que devuelve un valor. |
| `runAsync(() -> accion, executor)` | Ejecuta una operación que no devuelve valor. |
| `thenApply(valor -> nuevoValor)` | Transforma un resultado y devuelve otro valor. |
| `thenAccept(valor -> ...)` | Consume el resultado, no devuelve un resultado útil (`Void`). |
| `thenRun(() -> ...)` | Ejecuta una acción sin recibir el valor anterior. |
| `thenCompose(valor -> otroFuture)` | Encadena otra operación asíncrona y aplana el futuro. |
| `thenCombine(otroFuture, (a, b) -> ...)` | Combina dos resultados independientes. |
| `allOf(f1, f2, ...)` | Crea un futuro que termina cuando terminan todos los futuros indicados. |
| `exceptionally(error -> valorAlternativo)` | Recupera de un error con un valor alternativo. |
| `handle((resultado, error) -> ...)` | Transforma resultado o error en un nuevo resultado. |

### C. `thenCompose`: operación asíncrona dependiente

```java
CompletableFuture<String> obtenerUsuario(ExecutorService executor) {
    return CompletableFuture.supplyAsync(() -> {
        dormir(1000);
        return "Laura";
    }, executor);
}

CompletableFuture<String> obtenerEmail(String usuario, ExecutorService executor) {
    return CompletableFuture.supplyAsync(() -> {
        dormir(1000);
        return usuario.toLowerCase() + "@example.com";
    }, executor);
}

// Uso:
ExecutorService executor = Executors.newFixedThreadPool(2);
CompletableFuture<String> email = obtenerUsuario(executor)
        .thenCompose(usuario -> obtenerEmail(usuario, executor));

System.out.println(email.join());
executor.shutdown();
```

`thenCompose()` es útil si la siguiente función ya devuelve un `CompletableFuture`. No hace falta llamar a `get()` o `join()` entre pasos.

### D. `thenCombine`: dos operaciones independientes

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

CompletableFuture<Integer> temperatura = CompletableFuture.supplyAsync(() -> {
    dormir(2000);
    return 25;
}, executor);

CompletableFuture<Integer> humedad = CompletableFuture.supplyAsync(() -> {
    dormir(3000);
    return 60;
}, executor);

CompletableFuture<Void> fin = temperatura.thenCombine(humedad, (t, h) -> {
    System.out.println("Temperatura: " + t + " ºC");
    System.out.println("Humedad: " + h + " %");
    return null;
});

fin.join();
executor.shutdown();
```

Las dos operaciones pueden transcurrir en paralelo. Si haces `temperatura.join()` y después `humedad.join()` antes de empezar a combinarlas, puedes bloquear el hilo coordinador innecesariamente.

### E. `allOf`: esperar varias tareas y recoger cada resultado

`allOf()` devuelve `CompletableFuture<Void>`, no una lista de resultados. Los resultados se obtienen de los futuros originales después de que todos hayan terminado.

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 10, executor);
CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 20, executor);
CompletableFuture<Integer> c = CompletableFuture.supplyAsync(() -> 30, executor);

CompletableFuture<Void> todas = CompletableFuture.allOf(a, b, c);
todas.join();

int total = a.join() + b.join() + c.join();
System.out.println("Total: " + total); // 60
executor.shutdown();
```

Si un futuro falla, el `allOf()` que agrupa esos futuros también puede terminar excepcionalmente. Cuando quieras conservar el resto de resultados, controla los errores de cada futuro antes de esperar al grupo.

### F. Helper para esperas simuladas

En ejemplos donde la función necesita hacer `Thread.sleep()` y no puede declarar `InterruptedException`, puedes usar:

```java
import java.util.concurrent.CompletionException;

static void dormir(long milisegundos) {
    try {
        Thread.sleep(milisegundos);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new CompletionException(e);
    }
}
```

Si el examen permite soluciones más simples, también puedes manejar la excepción dentro de cada lambda, restaurando el estado de interrupción.

---

## 15. Errores y timeouts en `CompletableFuture`

### A. `exceptionally()`: valor alternativo cuando falla

```java
CompletableFuture<String> servicio = CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Error del servidor");
}).exceptionally(error -> "DATOS POR DEFECTO");

System.out.println(servicio.join());
```

### B. `handle()`: tratar éxito y error

```java
CompletableFuture<Integer> division = CompletableFuture
        .supplyAsync(() -> 10 / 0)
        .handle((resultado, error) -> {
            if (error != null) {
                return 0;
            }
            return resultado;
        });

System.out.println(division.join()); // 0
```

También se puede hacer un método reutilizable:

```java
CompletableFuture<Integer> dividir(int a, int b, ExecutorService executor) {
    return CompletableFuture.supplyAsync(() -> a / b, executor)
            .handle((resultado, error) -> error == null ? resultado : 0);
}
```

### C. `orTimeout()`: terminar excepcionalmente

```java
CompletableFuture<String> consulta = CompletableFuture.supplyAsync(() -> {
    dormir(5000);
    return "OK";
}).orTimeout(2, TimeUnit.SECONDS)
  .exceptionally(error -> "NO DISPONIBLE");

System.out.println(consulta.join());
```

Si vence el plazo, el futuro termina con una excepción de timeout; `exceptionally()` transforma esa excepción en el valor alternativo.

### D. `completeOnTimeout()`: completar con un valor alternativo

```java
CompletableFuture<String> consulta = CompletableFuture.supplyAsync(() -> {
    dormir(5000);
    return "OK";
}).completeOnTimeout("NO DISPONIBLE", 2, TimeUnit.SECONDS);

System.out.println(consulta.join());
```

### E. Patrón de tres servicios: cada servicio puede fallar

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

CompletableFuture<String> usuarios = CompletableFuture.supplyAsync(() -> {
    dormir(1000);
    return "Marta";
}, executor).exceptionally(e -> "NO DISPONIBLE");

CompletableFuture<Integer> pedidos = CompletableFuture.supplyAsync(() -> {
    dormir(1500);
    return 8;
}, executor).exceptionally(e -> -1);

CompletableFuture<String> estadisticas = CompletableFuture.supplyAsync(() -> {
    dormir(5000);
    return "1250 visitas";
}, executor).orTimeout(2, TimeUnit.SECONDS)
  .exceptionally(e -> "NO DISPONIBLES");

CompletableFuture<Void> todas = CompletableFuture.allOf(usuarios, pedidos, estadisticas);
todas.join();

System.out.println("Usuario: " + usuarios.join());
System.out.println("Pedidos: " + pedidos.join());
System.out.println("Estadísticas: " + estadisticas.join());
executor.shutdown();
```

Aquí cada futuro tiene un valor de recuperación, por lo que el `allOf()` no falla por esos errores ya gestionados. Adapta el valor alternativo al tipo del futuro: `String`, `Integer`, `Double`, etc.

**Matiz importante:** un timeout del `CompletableFuture` no garantiza que la función ya iniciada haya dejado de ejecutarse físicamente. El futuro puede completarse con el timeout mientras el trabajo subyacente sigue ocupando recursos. La cancelación/interrupción debe diseñarse por separado cuando el enunciado lo exija.

### `join()` frente a `get()`

- `get()` declara excepciones comprobadas como `InterruptedException` y `ExecutionException`; tiene variante con timeout.
- `join()` suele ser más corto, pero propaga errores de finalización como `CompletionException`.
- No llames a ninguno entre dos fases dependientes si puedes encadenarlas con `thenApply()` o `thenCompose()`.

---

## 16. `ScheduledExecutorService`

Sirve para ejecutar tareas tras una demora o repetidamente.

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

ScheduledFuture<?> tarea = scheduler.scheduleAtFixedRate(
        () -> System.out.println("Actualización periódica"),
        0,              // demora inicial
        1,              // intervalo
        TimeUnit.SECONDS
);

// Cuando haya que detenerlo:
tarea.cancel(false);
scheduler.shutdown();
```

Métodos habituales:

- `schedule(...)`: ejecutar una vez tras una demora.
- `scheduleAtFixedRate(...)`: periodicidad basada en el ritmo planificado.
- `scheduleWithFixedDelay(...)`: espera el retraso indicado después de finalizar una ejecución antes de iniciar la siguiente.

No olvides detener el programador; de lo contrario, puede mantener el proceso vivo.

---

## 17. Herramientas adicionales del tutorial

Estos conceptos aparecen en el tutorial ampliado. Repásalos después de dominar los patrones anteriores, salvo que el profesor los haya destacado expresamente.

### `ReadWriteLock`

Permite varios lectores a la vez, pero protege la escritura con un bloqueo exclusivo.

```java
ReadWriteLock lock = new ReentrantReadWriteLock();

lock.readLock().lock();
try {
    // Leer datos compartidos
} finally {
    lock.readLock().unlock();
}

lock.writeLock().lock();
try {
    // Modificar datos compartidos
} finally {
    lock.writeLock().unlock();
}
```

### `Exchanger`

Permite que dos hilos intercambien objetos en un punto común. Tiene un uso más específico y menor prioridad para una práctica básica de dos horas.

### `Phaser`

Permite coordinar participantes a lo largo de varias fases. Es una herramienta más avanzada que `CountDownLatch` cuando hay fases repetidas o cambia el número de participantes.

### Hilos virtuales (Java 21)

En Java 21 se puede crear un executor de hilos virtuales:

```java
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> System.out.println(Thread.currentThread()));
}
```

Úsalo solo si tu entorno de examen permite Java 21 y el enunciado lo solicita o lo habéis practicado. Para los ejercicios habituales del temario, un `newFixedThreadPool(n)` es más que suficiente.

### `ReadWriteLock`, `StampedLock`, `Exchanger` y `Phaser`

No los priorices por delante de `synchronized`, `ReentrantLock`, `Semaphore`, `BlockingQueue`, `Future` y `CompletableFuture` si el tiempo de estudio es limitado.

---

## 18. Plantillas integradoras

### A. Pool fijo + muchas tareas + contador seguro

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
AtomicInteger completados = new AtomicInteger(0);

for (int i = 1; i <= 20; i++) {
    final int id = i;
    executor.submit(() -> {
        // Trabajo
        completados.incrementAndGet();
        System.out.println("Tarea " + id + " terminada");
    });
}

executor.shutdown();
try {
    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
        executor.shutdownNow();
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}

System.out.println("Completadas: " + completados.get());
```

### B. Servicio que devuelve un resultado con `CompletableFuture`

```java
CompletableFuture<Integer> consulta = CompletableFuture.supplyAsync(() -> {
    // Simular operación
    return 42;
}, executor);

CompletableFuture<String> texto = consulta
        .thenApply(n -> "Resultado: " + n)
        .exceptionally(e -> "Error en la consulta");

System.out.println(texto.join());
```

### C. Confirmar una compra a partir de tres tareas independientes

```java
CompletableFuture<Boolean> stock = CompletableFuture.supplyAsync(() -> true, executor);
CompletableFuture<Double> precio = CompletableFuture.supplyAsync(() -> 25.0, executor);
CompletableFuture<Boolean> envio = CompletableFuture.supplyAsync(() -> true, executor);

CompletableFuture<String> compra = stock
        .thenCombine(precio, (hayStock, importe) -> hayStock && importe > 0)
        .thenCombine(envio, (condicionesPrecioYStock, hayEnvio) ->
                condicionesPrecioYStock && hayEnvio ? "Compra confirmada" : "Compra no disponible")
        .exceptionally(error -> "Compra no disponible")
        .orTimeout(5, TimeUnit.SECONDS)
        .exceptionally(error -> "Compra no disponible");

System.out.println(compra.join());
```

Las tres consultas se inician independientemente. La decisión final se calcula cuando los resultados necesarios estén disponibles. El `exceptionally()` final permite convertir un error/timeout global en un resultado visible.

---

## 19. Fallos típicos que revisar

Antes de entregar, revisa esta lista:

- [ ] He llamado a `start()`, no a `run()`, cuando necesito crear un hilo.
- [ ] He esperado a que terminen los hilos con `join()`, `Future.get()`, `allOf()` o una herramienta adecuada.
- [ ] Los datos compartidos están protegidos con `synchronized`, `Lock`, `AtomicInteger` u otra herramienta apropiada.
- [ ] No he asumido que `volatile` hace atómico `contador++`.
- [ ] Todo `lock()` se empareja con `unlock()` en `finally`.
- [ ] Todo permiso de `Semaphore` adquirido se libera, incluso si hay una excepción.
- [ ] Uso `while` para reexaminar la condición al volver de `wait()`.
- [ ] Los consumidores de una `BlockingQueue` tienen una forma definida de terminar.
- [ ] Cada `Callable<T>` devuelve el tipo correcto y recojo sus resultados con los `Future<T>`.
- [ ] No llamo a `get()`/`join()` antes de lanzar tareas independientes, haciendo que dejen de aprovechar el paralelismo.
- [ ] Sé que `allOf()` devuelve `CompletableFuture<Void>` y que debo leer cada futuro para obtener sus resultados.
- [ ] He gestionado los errores si el enunciado requiere que el programa continúe.
- [ ] He entendido si el timeout devuelve un valor alternativo o deja el futuro excepcionalmente completado.
- [ ] He cerrado el `ExecutorService`/`ScheduledExecutorService`.
- [ ] He restaurado la interrupción con `Thread.currentThread().interrupt()` si capturo `InterruptedException` y no la propago.
- [ ] He probado el resultado normal y al menos un caso límite (sin stock, cola llena/vacía, error o timeout según el ejercicio).

### Errores de compilación que son fáciles de evitar

1. Importar las clases correctas (`java.util.concurrent.*`, `java.util.concurrent.atomic.AtomicInteger`, `java.util.concurrent.locks.ReentrantLock`).
2. Usar la misma clase genérica en todas partes: `CompletableFuture<Integer>`, `Future<String>`, etc.
3. No devolver `null` desde una tarea que está declarada para devolver un valor que después se usa, salvo que el diseño lo permita.
4. No marcar `final` un `int` que se debe modificar (`private int stock = 100`, no `private final int stock = 100`).
5. Si usas variables locales dentro de lambdas, deben ser finales o efectivamente finales; puedes crear una variable `final int id = i` dentro del bucle.

---

## 20. Plan de examen de 120 minutos

Un reparto orientativo si el examen tiene varios ejercicios:

- **Minutos 0–5:** leer todos los enunciados y marcar qué herramienta pide cada uno.
- **Minutos 5–35:** resolver primero el ejercicio más directo para asegurar puntos.
- **Minutos 35–75:** completar el ejercicio principal de concurrencia (contador, inventario, cola o semáforo).
- **Minutos 75–105:** resolver el ejercicio de `ExecutorService`/`CompletableFuture`.
- **Minutos 105–120:** compilar, ejecutar, revisar resultados, gestionar el cierre y corregir errores.

Adapta este reparto al examen real. Si un ejercicio se atasca, deja una implementación mínima funcional y pasa al siguiente; vuelve a los detalles al final.

### Esqueleto de imports que cubre muchos ejercicios

No siempre necesitarás todos, pero son los más frecuentes en esta guía:

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
```

### Recuerda

**El código de referencia te ayuda, pero la clave es reconocer el patrón y adaptarlo al problema del enunciado.** Prioriza: contador seguro, `ReentrantLock`, `Semaphore`, productor-consumidor, `ExecutorService` + `Future`, `CompletableFuture` + combinación/errores/timeouts y cierre correcto.