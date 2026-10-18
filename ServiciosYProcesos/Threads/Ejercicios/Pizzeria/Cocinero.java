package Threads.Ejercicios.Pizzeria;

public class Cocinero implements Runnable{
    private final Pizzeria pizzeria;
    private final int id;

    public Cocinero(Pizzeria pizzeria, int id) {
        this.pizzeria = pizzeria;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Pedido pedido;

            try {
                pedido = pizzeria.getCola().take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // Señal de finalización
            if (pedido.getTipo() == Pedido.TipoPedido.FIN) {
                return;
            }

            boolean hornoAdquirido = false;

            try {
                pizzeria.getHornos().acquire();
                hornoAdquirido = true;

                Thread.sleep((long) pedido.getTipo().getTiempo());

                pizzeria.getPedidosCompletados().incrementAndGet();

                System.out.println("Cocinero " + id
                        + " termina " + pedido.getPedido());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;

            } finally {
                if (hornoAdquirido) {
                    pizzeria.getHornos().release();
                }

                pizzeria.getConteo().countDown();
            }
        }
    }
}
