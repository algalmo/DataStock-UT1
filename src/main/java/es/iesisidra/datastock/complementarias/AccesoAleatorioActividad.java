package es.iesisidra.datastock.complementarias;

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.servicio.GestorArchivos;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.LectorConsola;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Punto de entrada reservado para RF-FIC-08: RandomAccessFile.
 * <p>
 * Actividad obligatoria e independiente del menú principal.
 * </p>
 */
public final class AccesoAleatorioActividad {

    private static final int TAMANO_REGISTRO = 36;
    private static final int LONGITUD_CODIGO = 12;
    private static final int DESPLAZAMIENTO_STOCK = 32;

    private AccesoAleatorioActividad() {
    }

    public static void main(String[] args) {
        System.out.println("===== ACCESO ALEATORIO =====");
        String ruta = LectorConsola.leerTexto("Fichero de stock (ej: datos/salidas/stock.bin): ");
        if (ruta.trim().isEmpty()) {
            System.out.println("Error: La ruta no es válida.");
            return;
        }
        File archivo = new File(ruta.trim());

        int opcion;
        do {
            System.out.println("\n1. Generar stock.bin | 2. Consultar registro | 3. Modificar stock"
                    + " | 4. Mostrar todos | 0. Salir");
            opcion = LectorConsola.leerOpcion("Opción: ", 0, 4);

            switch (opcion) {
                case 1 -> generar(archivo);
                case 2 -> consultar(archivo);
                case 3 -> modificarStock(archivo);
                case 4 -> mostrarTodos(archivo);
                default -> {

                }
            }
        } while (opcion != 0);
    }

    private static void generar(File archivo) {
        Inventario inventario = new Inventario();
        GestorArchivos gestor = new GestorArchivos();

        int origen = LectorConsola.leerOpcion("Cargar productos desde: 1. JSON | 2. Inventario .dat | 0. Cancelar: ", 0,
                2);
        if (origen == 0) {
            return;
        }
        String rutaOrigen = LectorConsola.leerTexto("Ruta del fichero: ");
        if (origen == 1) {
            System.out.println(gestor.importarJSON(rutaOrigen, inventario));
        } else {
            System.out.println(gestor.recuperarInventarioDAT(rutaOrigen, inventario));
        }

        if (inventario.estaVacio()) {
            System.out.println("El inventario está vacío. No se genera el fichero.");
            return;
        }

        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (archivo.isDirectory() || carpeta == null || !carpeta.isDirectory()) {
            System.out.println("Error: El directorio de destino no existe o la ruta es un directorio.");
            return;
        }
        if (archivo.exists() && !LectorConsola.confirmar("El fichero ya existe y se va a sobrescribir. ¿Continuar?")) {
            System.out.println("Operación cancelada. El fichero original no se ha modificado.");
            return;
        }

        List<Producto> lista = inventario.ordenadosPorCodigo();

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            raf.setLength(0);

            for (Producto p : lista) {
                String cod = p.getCodigo();
                if (cod.length() > LONGITUD_CODIGO) {
                    cod = cod.substring(0, LONGITUD_CODIGO);
                } else {
                    cod = String.format("%-" + LONGITUD_CODIGO + "s", cod);
                }

                for (int i = 0; i < LONGITUD_CODIGO; i++) {
                    raf.writeChar(cod.charAt(i));
                }
                raf.writeDouble(p.getPrecio().doubleValue());
                raf.writeInt(p.getStock());
            }

            System.out.println("Fichero binario generado correctamente (" + lista.size() + " registros).");
        } catch (IOException e) {
            System.out.println("Error al generar el fichero binario: " + e.getMessage());
        }
    }

    private static void consultar(File archivo) {
        long total = comprobarFichero(archivo);
        if (total < 0) {
            return;
        }

        int pos = LectorConsola.leerEntero("Consultar posición (0-" + (total - 1) + "): ");
        if (pos < 0 || pos >= total) {
            System.out.println("Error: Posición fuera de rango.");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            raf.seek((long) pos * TAMANO_REGISTRO);
            System.out.println(leerRegistro(raf));
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }

    private static void modificarStock(File archivo) {
        long total = comprobarFichero(archivo);
        if (total < 0) {
            return;
        }

        int pos = LectorConsola.leerEntero("Posición a modificar (0-" + (total - 1) + "): ");
        if (pos < 0 || pos >= total) {
            System.out.println("Error: Posición fuera de rango.");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            raf.seek((long) pos * TAMANO_REGISTRO);
            System.out.println(leerRegistro(raf));

            raf.seek((long) pos * TAMANO_REGISTRO + DESPLAZAMIENTO_STOCK);
            int stockActual = raf.readInt();

            int nuevoStock = LectorConsola.leerEntero("Nuevo stock: ");
            if (nuevoStock < 0) {
                System.out.println("Error: El stock no puede ser negativo.");
                return;
            }

            raf.seek((long) pos * TAMANO_REGISTRO + DESPLAZAMIENTO_STOCK);
            raf.writeInt(nuevoStock);
            System.out.println("Actualización correcta: stock " + stockActual + " → " + nuevoStock);
        } catch (IOException e) {
            System.out.println("Error al acceder al fichero: " + e.getMessage());
        }
    }

    private static void mostrarTodos(File archivo) {
        long total = comprobarFichero(archivo);
        if (total < 0) {
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            for (int i = 0; i < total; i++) {
                System.out.println(i + ": " + leerRegistro(raf));
            }
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }

    private static String leerRegistro(RandomAccessFile raf) throws IOException {
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            codigo.append(raf.readChar());
        }
        double precio = raf.readDouble();
        int stock = raf.readInt();
        return codigo.toString().trim() + " | " + precio + " € | stock " + stock;
    }

    private static long comprobarFichero(File archivo) {
        long registros = -1;

        if (!archivo.isFile()) {
            System.out.println("Error: El fichero no existe. Genéralo primero con la opción 1.");
        } else if (archivo.length() == 0) {
            System.out.println("Error: El fichero está vacío.");
        } else if (archivo.length() % TAMANO_REGISTRO != 0) {
            System.out.println("Error: Fichero inválido, su tamaño (" + archivo.length()
                    + " bytes) no es múltiplo de " + TAMANO_REGISTRO + ".");
        } else {
            registros = archivo.length() / TAMANO_REGISTRO;
            System.out.println("Registros: " + registros + " | Tamaño: " + archivo.length() + " bytes");
        }

        return registros;
    }
}
