package es.iesisidra.datastock.validacion;

import java.math.BigDecimal;

public class Validador {
    
    // Valida y normaliza el código
     
    public static String validarCodigo(String codigo) {
        String resultado = null;
        if (codigo != null) {
            String normalizado = codigo.trim().toUpperCase();
            
            if (normalizado.length() >= 1 && normalizado.length() <= 12) {
                boolean valido = true;
                int i = 0;
                while (i < normalizado.length() && valido) {
                    char c = normalizado.charAt(i);
                    boolean esLetra = (c >= 'A' && c <= 'Z');
                    boolean esDigito = (c >= '0' && c <= '9');
                    boolean esGuion = (c == '-');

                    if (!esLetra && !esDigito && !esGuion) {
                        valido = false;
                    }
                    i++;
                }

                if (valido) {
                    resultado = normalizado;
                } else {
                    throw new IllegalArgumentException("El código solo puede contener letras (A-Z), números y guiones.");
                }
            } else {
                throw new IllegalArgumentException("El código debe tener entre 1 y 12 caracteres.");
            }
        } else {
            throw new IllegalArgumentException("El código no puede ser nulo.");
        }
        return resultado;
    }

    // Valida el email de forma clásica (comprobando '@' y '.'). Es opcional.
     
    public static String validarEmail(String email) {
        String resultado = null;
        if (email == null || email.trim().isEmpty()) {
            resultado = null;
        } else {
            String normalizado = email.trim();
            int arrobita = normalizado.indexOf('@');
            int punto = normalizado.lastIndexOf('.');

            if (normalizado.length() <= 120 && arrobita > 0 && punto > arrobita + 1 && punto < normalizado.length() - 1) {
                resultado = normalizado;
            } else {
                throw new IllegalArgumentException("El formato del email no es válido o supera los 120 caracteres.");
            }
        }
        return resultado;
    }

    // Valida el nombre de producto (3-100 caracteres).
     
    public static String validarNombreProducto(String nombre) {
        String resultado = null;
        if (nombre != null) {
            String normalizado = nombre.trim();
            if (normalizado.length() >= 3 && normalizado.length() <= 100) {
                resultado = normalizado;
            } else {
                throw new IllegalArgumentException("El nombre del producto debe tener entre 3 y 100 caracteres.");
            }
        } else {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo.");
        }
        return resultado;
    }

    // Valida el nombre de categoría (1-60 caracteres).
    
    public static String validarNombreCategoria(String nombre) {
        String resultado = null;
        if (nombre != null) {
            String normalizado = nombre.trim();
            if (normalizado.length() >= 1 && normalizado.length() <= 60) {
                resultado = normalizado;
            } else {
                throw new IllegalArgumentException("El nombre de la categoría debe tener entre 1 y 60 caracteres.");
            }
        } else {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo.");
        }
        return resultado;
    }

    // Valida el nombre de proveedor (máximo 100 caracteres).

    public static String validarNombreProveedor(String nombre) {
        String resultado = null;
        if (nombre != null) {
            String normalizado = nombre.trim();
            if (!normalizado.isEmpty() && normalizado.length() <= 100) {
                resultado = normalizado;
            } else {
                throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío y debe tener máximo 100 caracteres.");
            }
        } else {
            throw new IllegalArgumentException("El nombre del proveedor no puede ser nulo.");
        }
        return resultado;
    }

    //  Valida el precio con BigDecimal (obligatorio, > 0, máx 2 decimales, DECIMAL(10,2)).
     
    public static BigDecimal validarPrecio(BigDecimal precio) {
        BigDecimal resultado = null;
        if (precio != null) {
            if (precio.compareTo(BigDecimal.ZERO) > 0) {
                if (precio.scale() <= 2) {
                    int digitosEnteros = precio.precision() - precio.scale();
                    if (digitosEnteros <= 8) {
                        resultado = precio;
                    } else {
                        throw new IllegalArgumentException("El precio excede el rango permitido para DECIMAL(10,2).");
                    }
                } else {
                    throw new IllegalArgumentException("El precio no puede tener más de 2 decimales.");
                }
            } else {
                throw new IllegalArgumentException("El precio debe ser mayor que 0.");
            }
        } else {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }
        return resultado;
    }

    // Valida el stock (entero >= 0).

    public static int validarStock(int stock) {
        int resultado;
        if (stock >= 0) {
            resultado = stock;
        } else {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        return resultado;
    }

    // Valida IDs (enteros positivos > 0).
     
    public static int validarId(int id) {
        int resultado;
        if (id > 0) {
            resultado = id;
        } else {
            throw new IllegalArgumentException("El ID debe ser un número entero positivo.");
        }
        return resultado;
    }
}
