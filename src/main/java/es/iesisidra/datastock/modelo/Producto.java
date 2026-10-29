package es.iesisidra.datastock.modelo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * Representa un artículo del catálogo de DataStock.
 * <p>En UT1 se almacena en una colección en memoria. En UT2 su información
 * se persistirá en la tabla {@code producto} de MySQL.</p>
 * <p>Los identificadores de categoría y proveedor son números positivos en UT1;
 * su existencia en base de datos se comprobará al trabajar con JDBC en UT2.</p>
 */



/**
 * Representa un artículo del catálogo de DataStock.
 * <p>En UT1 se almacena en una colección en memoria. En UT2 su información
 * se persistirá en la tabla {@code producto} de MySQL.</p>
 * <p>Los identificadores de categoría y proveedor son números positivos en UT1;
 * su existencia en base de datos se comprobará al trabajar con JDBC en UT2.</p>
 */
public class Producto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Los nombres de los atributos coinciden con las claves del JSON,
    // así Gson los rellena sin necesidad de anotaciones.
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private int categoriaId;
    private int proveedorId;

    /** Constructor sin argumentos, necesario para Gson. */
    public Producto() { }

    /** Construye un producto con los campos previstos en el enunciado. */
    public Producto(String codigo, String nombre, BigDecimal precio, int stock,
                    int categoriaId, int proveedorId) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.proveedorId = proveedorId;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public int getProveedorId() { return proveedorId; }
    public void setProveedorId(int proveedorId) { this.proveedorId = proveedorId; }

    @Override
    public String toString() {
        return "Producto{codigo='" + codigo + "', nombre='" + nombre + "', precio=" + precio
                + ", stock=" + stock + ", categoriaId=" + categoriaId
                + ", proveedorId=" + proveedorId + "}";
    }
}
