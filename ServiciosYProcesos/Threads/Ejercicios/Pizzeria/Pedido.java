package Threads.Ejercicios.Pizzeria;

public class Pedido {
    private String pedido;
    private TipoPedido tipo;

    public enum TipoPedido{
        MARGARITA(1000),
        VEGETAL(2000),
        CUATRO_QUESOS(3000);
        private final double tiempo;
        TipoPedido(double tiempo){this.tiempo  = tiempo;}

        public double getTiempo() {
            return tiempo;
        }
    }

    public Pedido(String pedido, TipoPedido tipo) {
        this.pedido = pedido;
        this.tipo = tipo;
    }

    public String getPedido() {
        return pedido;
    }

    public TipoPedido getTipo() {
        return tipo;
    }
}
