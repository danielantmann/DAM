package org.example.modelo;

public class ActivacionMisiles {
    private String nombre;
    private  String numSecreto;
    private int fase;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumSecreto() {
        return numSecreto;
    }

    public void setNumSecreto(String numSecreto) {
        this.numSecreto = numSecreto;
    }

    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }

    public ActivacionMisiles(String nombre, String numSecreto, int fase) {
        this.nombre = nombre;
        this.numSecreto = numSecreto;
        this.fase = fase;
    }

    public  ActivacionMisiles( String nombre,
                               String numSecreto){
        this(nombre,numSecreto,1);
    }
}
