package es.iesisidra.datastock.modelo;

import java.sql.Timestamp;

public class MovimientoStockDTO {
    private final String codigoProducto;
    private final String tipo;
    private final int cantidad;
    private final Timestamp fecha;

    public MovimientoStockDTO(String codigoProducto, String tipo, int cantidad, Timestamp fecha) {
        this.codigoProducto = codigoProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public Timestamp getFecha() { return fecha; }
}
