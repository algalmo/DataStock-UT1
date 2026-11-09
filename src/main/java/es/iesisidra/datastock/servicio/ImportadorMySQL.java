package es.iesisidra.datastock.servicio;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.iesisidra.datastock.config.ConexionBD;
import es.iesisidra.datastock.datos.CategoriaDAO;
import es.iesisidra.datastock.datos.ProductoDAO;
import es.iesisidra.datastock.datos.ProveedorDAO;
import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.modelo.ResultadoImportacion;

public class ImportadorMySQL {

    public static ResultadoImportacion importar(List<Producto> productosMemoria) {
        ResultadoImportacion resultado = new ResultadoImportacion();
        List<Producto> aceptados = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            
            for (Producto p : productosMemoria) {
                resultado.incrementarAnalizados();
                
                // Normalización de código
                String codigoNorm = "";

                if (p.getCodigo() != null) {
                    codigoNorm = p.getCodigo().toUpperCase().trim();
                }
                
                String motivoRechazo = "";
                boolean valido = true;

                // Validaciones básicas
                if (codigoNorm.isEmpty() || codigoNorm.length() > 12) {
                    motivoRechazo = "Código inválido (longitud/formato).";
                    valido = false;
                } else if (p.getNombre() == null || p.getNombre().trim().length() < 3) {
                    motivoRechazo = "Nombre demasiado corto o nulo.";
                    valido = false;
                } else if (p.getStock() < 0) {
                    motivoRechazo = "El stock inicial es negativo.";
                    valido = false;
                } else {
                BigDecimal precioBD = p.getPrecio();                    
                if (precioBD.scale() > 2) {
                        motivoRechazo = "El precio tiene más de 2 decimales.";
                        valido = false;
                    } else if (precioBD.compareTo(BigDecimal.ZERO) <= 0) {
                        motivoRechazo = "El precio no es estrictamente positivo.";
                        valido = false;
                    }
                }

                //Comprobacion contra la BD usando la misma conexión
                if (valido) {
                    try {
                        if (ProductoDAO.existeCodigo(conexion, codigoNorm)) {
                            resultado.incrementarOmitidos();
                            resultado.addIncidencia("[OMITIDO] " + codigoNorm + " - Ya existe en MySQL.");
                            valido = false;
                        } else if (!CategoriaDAO.existeId(conexion, p.getCategoriaId())) {
                            motivoRechazo = "No existe la categoría con ID " + p.getCategoriaId() + " en MySQL.";
                            valido = false;
                        } else if (!ProveedorDAO.existeId(conexion, p.getProveedorId())) {
                            motivoRechazo = "No existe el proveedor con ID " + p.getProveedorId() + " en MySQL.";
                            valido = false;
                        }
                    } catch (SQLException e) {
                        motivoRechazo = "Error SQL al comprobar referencias: " + e.getMessage();
                        valido = false;
                    }
                }

                if (valido) {
                    aceptados.add(p);
                } else if (!motivoRechazo.isEmpty()) {
                    resultado.incrementarRechazados();
                    resultado.addIncidencia("[RECHAZADO] " + codigoNorm + " - " + motivoRechazo);
                }
            }

            if (!aceptados.isEmpty()) {
                boolean autocommitOriginal = conexion.getAutoCommit();
                try {
                    conexion.setAutoCommit(false); 

                    for (Producto p : aceptados) {
                        String codigoNorm = p.getCodigo().toUpperCase().trim();
                        BigDecimal precioBD = p.getPrecio();                        
                        ProductoDAO.insertar(conexion, codigoNorm, p.getNombre(), precioBD, p.getStock(), p.getCategoriaId(), p.getProveedorId());
                    }

                    conexion.commit(); 
                    resultado.setInsertados(aceptados.size());

                } catch (SQLException e) {
                    conexion.rollback(); 
                    resultado.setRollbackEjecutado(true);
                    resultado.addIncidencia("[FATAL] Error SQL durante la inserción del lote: " + e.getMessage());
                } finally {
                    conexion.setAutoCommit(autocommitOriginal); 
                }
            }

        } catch (SQLException e) {
            resultado.addIncidencia("[FATAL] Imposible conectar a la BD para la importación: " + e.getMessage());
        }

        return resultado;
    }
}
