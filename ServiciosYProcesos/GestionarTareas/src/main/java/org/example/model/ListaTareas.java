package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class ListaTareas {
    private int maxTareas;
    private  int contadorId;
    private List<Tarea> listaTareas ;

    public ListaTareas(int maxTareas) {
        this.maxTareas = maxTareas;
        this.contadorId = 1;
        this.listaTareas = new ArrayList<>();
    }

    public int getMaxTareas() {
        return maxTareas;
    }

    public void setMaxTareas(int maxTareas) {
        this.maxTareas = maxTareas;
    }

    public List<Tarea> getListaTareas() {
        return listaTareas;
    }

    public void setListaTareas(List<Tarea> listaTareas) {
        this.listaTareas = listaTareas;
    }

    public int getContadorId() {
        return contadorId;
    }

    public void setContadorId(int contadorId) {
        this.contadorId = contadorId;
    }

    public int cantTareas(){
        return this.listaTareas.size();
    }

    public boolean puedesAñadir(){
        if (this.listaTareas.size() < this.maxTareas){
            return true;
        }
        return false;
    }

    public Tarea buscarTarea (int id){
        for (Tarea t : this.listaTareas){
            if (id == t.getId()){
                return t;
            }
        }
        return null;
    }

    public boolean añadirTarea(Tarea tarea) {
        if (puedesAñadir()) {
            tarea.setId(this.contadorId); // Asignas el ID autoincremental
            this.listaTareas.add(tarea);
            this.contadorId++; // Incrementas el contador para la siguiente tarea
            return true;
        }
        return false; // No había sitio
    }

    public boolean eliminarTarea(int id) {
        Tarea t = buscarTarea(id);
        if (t != null) {
            return this.listaTareas.remove(t);
        }
        return false;
    }
}
