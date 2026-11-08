package es.iesisidra.datastock.modelo;

import java.math.BigDecimal;

public class ProductoDetalleDTO {
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final int stock;
    private final String nombreCategoria;
    private final String nombreProveedor;

    public ProductoDetalleDTO(String codigo, String nombre, BigDecimal precio, int stock, String nombreCategoria, String nombreProveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.nombreCategoria = nombreCategoria;
        this.nombreProveedor = nombreProveedor;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getNombreCategoria() { return nombreCategoria; }
    public String getNombreProveedor() { return nombreProveedor; }
}
