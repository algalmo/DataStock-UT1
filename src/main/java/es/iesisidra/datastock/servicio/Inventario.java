package es.iesisidra.datastock.servicio;

import es.iesisidra.datastock.modelo.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Comprobación git hub

/**
 * Estado común del inventario durante la ejecución de UT1.
 * <p>
 * Esta clase solo proporciona la colección compartida. No implementa
 * importación, validación, búsqueda, exportación ni persistencia: esos son
 * ejercicios que deberá desarrollar el alumnado.
 * </p>
 */

public class Inventario {
    private final List<Producto> productos = new ArrayList<>();

    /**
     * Obtiene la colección que comparten las opciones del menú.
     * <p>
     * ATENCIÓN: la lista es modificable. El alumnado deberá aplicar las
     * reglas del enunciado antes de añadir o sustituir elementos.
     * </p>
     */
    public List<Producto> getProductos() {
        return productos;
    }

    public boolean estaVacio() {
        // Lista productos no contiene elementos
        return this.productos.isEmpty();
    }

    public boolean existeCodigo(String codigo) {

        boolean existe = false;
        // Se comprueba si algún producto contiene ese código
        if (codigo != null) {
            String codBueno = codigo.trim().toUpperCase();
            for (Producto p : productos) {
                if (p.getCodigo() != null && p.getCodigo().trim().toUpperCase().equals(codBueno)) {
                    existe = true;
                }
            }
        }
        return existe;

    }

    public List<String> validarProducto(Producto p) {
        List<String> errores = new ArrayList<>();

        if (p == null) {
            errores.add("El producto es nulo.");
        } else {

            String codCorrecto = p.getCodigo();
            if (codCorrecto == null || codCorrecto.trim().isEmpty()) {
                errores.add("El código es obligatorio.");
            } else {
                codCorrecto = codCorrecto.trim();

                if (codCorrecto.length() > 12) {
                    errores.add("El código no puede superar los 12 caracteres.");
                }

                if (!codCorrecto.toUpperCase().matches("^[A-Z0-9-]+$")) {
                    errores.add("El código no respeta el formato permitido ([A-Z0-9-]+).");
                }
            }

            String nombreCorrecto = p.getNombre();
            if (nombreCorrecto == null || nombreCorrecto.trim().isEmpty()) {
                errores.add("El nombre es obligatorio.");
            } else {
                nombreCorrecto = nombreCorrecto.trim();

                if (nombreCorrecto.length() < 3 || nombreCorrecto.length() > 100) {
                    errores.add("El nombre debe tener entre 3 y 100 caracteres.");
                }
            }

            if (p.getPrecio() == null) {
                errores.add("El precio es obligatorio.");
            } else if (p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add("El precio debe ser mayor que 0.");
            }

            if (p.getStock() < 0) {
                errores.add("El stock debe ser mayor o igual que 0.");
            }

            if (p.getCategoriaId() <= 0) {
                errores.add("El ID de la categoría debe ser un entero positivo.");
            }

            if (p.getProveedorId() <= 0) {
                errores.add("El ID del proveedor debe ser un entero positivo.");
            }
        }

        return errores;
    }

    public boolean agregarProducto(Producto p) {
        boolean adjuntado = false;

        List<String> errores = validarProducto(p);

        if (errores.isEmpty() && !existeCodigo(p.getCodigo())) {
            p.setCodigo(p.getCodigo().trim());
            if (p.getNombre() != null) {
                p.setNombre(p.getNombre().trim());
            }

            productos.add(p);
            adjuntado = true;
        }

        return adjuntado;
    }

    // 04

    public List<Producto> ordenadosPorCodigo() {
        List<Producto> copia = new ArrayList<>(this.productos);
        copia.sort((a, b) -> a.getCodigo().compareToIgnoreCase(b.getCodigo()));
        return copia;
    }

    public String listarProductos() {
        String resultado = "";

        if (this.productos == null || this.productos.isEmpty()) {
            resultado = "No hay productos en el inventario.";
        } else {

            List<Producto> listaOrdenada = new ArrayList<>(this.productos);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listaOrdenada.size(); i++) {
                Producto p = listaOrdenada.get(i);
                sb.append("Código: ").append(p.getCodigo()).append("\n")
                        .append("Nombre: ").append(p.getNombre()).append("\n")
                        .append("Precio: ").append(p.getPrecio()).append(" € | Stock: ").append(p.getStock())
                        .append("\n")
                        .append("Categoría: ").append(p.getCategoriaId()).append(" | Proveedor: ")
                        .append(p.getProveedorId()).append("\n");

                if (i < listaOrdenada.size() - 1) {
                    sb.append("------------------------------\n");
                }
            }

            resultado = sb.toString();
        }

        return resultado;
    }

    public String buscarPorCodigo(String codigoBuscar) {
        String resultado = "";

        if (codigoBuscar == null || codigoBuscar.trim().isEmpty()) {
            resultado = "El código introducido no es válido.";
        } else if (this.productos == null || this.productos.isEmpty()) {
            resultado = "No hay productos en el inventario.";
        } else {
            String codLimpio = codigoBuscar.trim();
            Producto productoEncontrado = null;

            for (int i = 0; i < this.productos.size(); i++) {
                Producto p = this.productos.get(i);
                if (p.getCodigo() != null && p.getCodigo().trim().equalsIgnoreCase(codLimpio)) {
                    productoEncontrado = p;
                }
            }

            if (productoEncontrado != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Código: ").append(productoEncontrado.getCodigo()).append("\n")
                        .append("Nombre: ").append(productoEncontrado.getNombre()).append("\n")
                        .append("Precio: ").append(productoEncontrado.getPrecio()).append(" € | Stock: ")
                        .append(productoEncontrado.getStock()).append("\n")
                        .append("Categoría: ").append(productoEncontrado.getCategoriaId()).append(" | Proveedor: ")
                        .append(productoEncontrado.getProveedorId());

                resultado = sb.toString();
            } else {
                resultado = "No se encontró ningún producto con el código: " + codLimpio;
            }
        }

        return resultado;
    }

    public String buscarPorNombre(String fragmentoNombre) {
        String resultado = "";

        if (fragmentoNombre == null || fragmentoNombre.trim().isEmpty()) {
            resultado = "El texto de búsqueda no es válido.";
        } else if (this.productos == null || this.productos.isEmpty()) {
            resultado = "No hay productos en el inventario.";
        } else {
            String textoLimpio = fragmentoNombre.trim().toLowerCase();
            List<Producto> coincidencias = new ArrayList<>();

            for (int i = 0; i < this.productos.size(); i++) {
                Producto p = this.productos.get(i);
                if (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoLimpio)) {
                    coincidencias.add(p);
                }
            }

            if (coincidencias.isEmpty()) {
                resultado = "No se encontraron productos que contengan: '" + fragmentoNombre.trim() + "'";
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < coincidencias.size(); i++) {
                    Producto p = coincidencias.get(i);
                    sb.append("Código: ").append(p.getCodigo()).append("\n")
                            .append("Nombre: ").append(p.getNombre()).append("\n")
                            .append("Precio: ").append(p.getPrecio()).append(" € | Stock: ").append(p.getStock())
                            .append("\n")
                            .append("Categoría: ").append(p.getCategoriaId()).append(" | Proveedor: ")
                            .append(p.getProveedorId()).append("\n");

                    if (i < coincidencias.size() - 1) {
                        sb.append("------------------------------\n");
                    }
                }
                resultado = sb.toString();
            }
        }

        return resultado;
    }

}
