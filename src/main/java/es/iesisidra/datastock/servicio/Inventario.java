package es.iesisidra.datastock.servicio;

import es.iesisidra.datastock.modelo.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado común del inventario durante la ejecución de UT1.
 * <p>Esta clase solo proporciona la colección compartida. No implementa
 * importación, validación, búsqueda, exportación ni persistencia: esos son
 * ejercicios que deberá desarrollar el alumnado.</p>
 */

//Comprobación git hub


public class Inventario {
    private final List<Producto> productos = new ArrayList<>(); 

    /**
     * Obtiene la colección que comparten las opciones del menú.
     * <p>ATENCIÓN: la lista es modificable. El alumnado deberá aplicar las
     * reglas del enunciado antes de añadir o sustituir elementos.</p>
     */
    public List<Producto> getProductos() { 
        return productos; 
    }

    public boolean estaVacio(){
        //Lista productos no contiene elementos
        return this.productos.isEmpty();
    }

    public boolean existeCodigo(String codigo){

        boolean existe = false;
        //Se comprueba si algún producto contiene ese código
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

    public List<String> validarProducto(Producto p){
        List<String> errores = new ArrayList<>();

        if (p == null) {
            errores.add("El producto es nulo");
        }else{
            String codCorrecto = p.getCodigo();
            if ( codCorrecto == null || codCorrecto.trim().isEmpty()) {                    errores.add("El cÃ³digo no respeta el formato permitido ([A-Z0-9-]+).");
            
                errores.add("El código es obligatorio. ");
            }else{
                codCorrecto = codCorrecto.trim();

                if (codCorrecto.length() > 12) {
                    errores.add("El código no puede superar los 12 caracteres");
                }

                if (!codCorrecto.toUpperCase().matches("^[A-Z0-9-]+$")) {
                    errores.add("El código no respeta el formato permitido ([A-Z0-9-]+).");
                }
            }

            //NOMBRE VALID
            String nombreCorrecto = p.getNombre();

            if (nombreCorrecto == null || nombreCorrecto.trim().isEmpty()) {
                errores.add("El nombre es obligatorio");
            }else{
                nombreCorrecto = nombreCorrecto.trim();

                if (nombreCorrecto.length() > 3 && nombreCorrecto.length() < 100) {
                    errores.add("El nombre debe tener entre 3 y 100 caracteres.");
                }
            }

            //PRECIO VALID
            if (p.getPrecio() == null) {
                errores.add("El precio es obligatorio");
            }else if (p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add("El precio debe ser mayor que 0.");
            }

            //STOCK VALID
            if (p.getStock() < 0) {
                errores.add("El stock debe ser mayor o igual que 0");
            }

            //CATEGORIA VALID
            if (p.getCategoriaId() <= 0) {
                errores.add("El ID de la categoría debe ser mayor que 0");
            }

            //PROVEEDOR VALID
            if (p.getProveedorId() <= 0) {
                errores.add("El ID de proveedor debe ser un entero positivo.");
            }

        }
        return errores;
    }

    public boolean agregarProducto(Producto p){
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




}
