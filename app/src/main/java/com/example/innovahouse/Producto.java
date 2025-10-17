package com.example.innovahouse;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;

    public Producto(int id, String nombre, double precio, String descripcion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
}