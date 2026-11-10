package Modelo;

public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private int cantidad;
    private String estante;

    public Producto() {
    }

    public Producto(String codigo, String nombre, int cantidad, String estante) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.estante = estante;
    }

    public Producto(int id, String codigo, String nombre, int cantidad, String estante) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.estante = estante;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstante() {
        return estante;
    }

    public void setEstante(String estante) {
        this.estante = estante;
    }
}
