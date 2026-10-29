package es.iesisidra.datastock.complementarias;

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.util.LectorConsola;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Punto de entrada reservado para RF-FIC-08: RandomAccessFile.
 * <p>Actividad obligatoria e independiente del menú principal.</p>
 */
public final class AccesoAleatorioActividad {

    private static final int TAMANO_REGISTRO = 36;

    private AccesoAleatorioActividad() { }

    public static void main(String[] args) {
        System.out.println("===== ACCESO ALEATORIO =====");
        String ruta = LectorConsola.leerTexto("Fichero de stock (ej: datos/salidas/stock.bin): ");
        File archivo = new File(ruta.trim());

        if (!archivo.exists()) {
            System.out.println("El fichero especificado no existe.");
            String resp = LectorConsola.leerTexto("¿Deseas generar " + ruta + " con productos de prueba? (s/n): ");
            if (resp.equalsIgnoreCase("s")) {
                List<Producto> prueba = obtenerProductosPrueba();
                generarFicheroBinario(archivo, prueba);
            } else {
                return;
            }
        }

        if (archivo.length() == 0 || archivo.length() % TAMANO_REGISTRO != 0) {
            System.out.println("Error: El fichero es inválido (su tamaño no es múltiplo de 36 bytes).");
            return;
        }

        long totalRegistros = archivo.length() / TAMANO_REGISTRO;
        System.out.println("Fichero: " + archivo.getPath());
        System.out.println("Registros: " + totalRegistros + " | Tamaño: " + archivo.length() + " bytes");

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            int pos = LectorConsola.leerEntero("Consultar posición (0-" + (totalRegistros - 1) + "): ");

            if (pos < 0 || pos >= totalRegistros) {
                System.out.println("Error: Posición fuera de rango.");
                return;
            }

            int desplazamientoInicio = pos * TAMANO_REGISTRO;
            raf.seek(desplazamientoInicio);

            StringBuilder sbCodigo = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                sbCodigo.append(raf.readChar());
            }
            String codigo = sbCodigo.toString().trim();
            double precio = raf.readDouble();
            int stockActual = raf.readInt();

            System.out.println(codigo + " | " + precio + " € | stock " + stockActual);

            int nuevoStock = LectorConsola.leerEntero("Nuevo stock: ");
            if (nuevoStock < 0) {
                System.out.println("Error: El stock no puede ser negativo.");
                return;
            }

            int desplazamientoStock = (pos * TAMANO_REGISTRO) + 32;
            raf.seek(desplazamientoStock);
            raf.writeInt(nuevoStock);

            System.out.println("Actualización correcta: stock " + stockActual + " → " + nuevoStock);

        } catch (Exception e) {
            System.out.println("Error al acceder al fichero aleatorio: " + e.getMessage());
        }
    }

    public static void generarFicheroBinario(File archivo, List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            System.out.println("El inventario está vacío. No se genera el fichero.");
            return;
        }

        List<Producto> lista = new ArrayList<>(productos);
        lista.sort(Comparator.comparing(Producto::getCodigo));

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            raf.setLength(0);

            for (Producto p : lista) {
                String cod = (p.getCodigo() != null) ? p.getCodigo() : "";
                if (cod.length() > 12) {
                    cod = cod.substring(0, 12);
                } else {
                    cod = String.format("%-12s", cod);
                }

                for (int i = 0; i < 12; i++) {
                    raf.writeChar(cod.charAt(i));
                }

                double precio = (p.getPrecio() != null) ? p.getPrecio().doubleValue() : 0.0;
                raf.writeDouble(precio);
                raf.writeInt(p.getStock());
            }

            System.out.println("Fichero binario generado correctamente (" + lista.size() + " registros).");

        } catch (Exception e) {
            System.out.println("Error al generar el fichero binario: " + e.getMessage());
        }
    }

    private static List<Producto> obtenerProductosPrueba() {
        List<Producto> lista = new ArrayList<>();

        Producto p1 = new Producto();
        p1.setCodigo("TEC-001");
        p1.setNombre("Teclado USB");
        p1.setPrecio(new BigDecimal("19.95"));
        p1.setStock(12);

        Producto p2 = new Producto();
        p2.setCodigo("RAT-002");
        p2.setNombre("Ratón inalámbrico");
        p2.setPrecio(new BigDecimal("14.50"));
        p2.setStock(25);

        Producto p3 = new Producto();
        p3.setCodigo("MON-003");
        p3.setNombre("Monitor 24 pulgadas");
        p3.setPrecio(new BigDecimal("149.99"));
        p3.setStock(8);

        Producto p4 = new Producto();
        p4.setCodigo("AUR-004");
        p4.setNombre("Auriculares Gamer");
        p4.setPrecio(new BigDecimal("45.00"));
        p4.setStock(15);

        lista.add(p1);
        lista.add(p2);
        lista.add(p3);
        lista.add(p4);
        return lista;
    }
}
