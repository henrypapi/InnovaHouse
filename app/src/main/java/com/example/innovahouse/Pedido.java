package com.example.innovahouse;

public class Pedido {
    private int id;
    private String cliente;
    private String fecha;
    private double total;
    private String estado;

    public Pedido(int id, String cliente, String fecha, double total, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public String getFecha() { return fecha; }
    public double getTotal() { return total; }
    public String getEstado() { return estado; }
}
