package es.iesisidra.datastock.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utilidad compartida para solicitar datos por consola.
 * <p>
 * Utiliza un único {@link Scanner} para toda la aplicación. Se leen líneas
 * completas y se convierten cuando procede, evitando conflictos entre
 * {@code nextInt()} y {@code nextLine()}.
 * </p>
 */
public final class LectorConsola {
    private static final Scanner TECLADO = new Scanner(System.in);

    private LectorConsola() {
    }

    /** Lee una línea; permite una cadena vacía. */
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!TECLADO.hasNextLine()) {
            // Entrada cerrada (por ejemplo, ejecución no interactiva).
            System.out.println();
            System.exit(0);
        }
        return TECLADO.nextLine();
    }

    /** Lee un entero; repite la pregunta ante una entrada no válida. */
    public static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                System.out
                        .println(ColoresConsola.pintar("Introduce un número entero válido.", ColoresConsola.AMARILLO));
            }
        }
    }

    /** Lee una opción comprendida entre los límites indicados. */
    public static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int opcion = leerEntero(mensaje);
            if (opcion >= minimo && opcion <= maximo)
                return opcion;
            System.out.println(ColoresConsola.pintar("Opción fuera de rango.", ColoresConsola.AMARILLO));
        }
    }

    /** Solicita confirmación con S/N y devuelve true únicamente si se elige S. */
    public static boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (S/N): ").trim();
            if (respuesta.equalsIgnoreCase("S"))
                return true;
            if (respuesta.equalsIgnoreCase("N"))
                return false;
            System.out.println("Responde S o N.");
        }
    }

    /** Detiene el menú hasta que el usuario pulse Intro. */
    public static void pausar() {
        leerTexto("Pulsa Intro para continuar...");
    }

    public static double leerDecimal(String mensaje) {
        double valor = 0.0;
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);
            String entrada = TECLADO.nextLine().trim().replace(',', '.'); // Permite usar tanto coma como punto
            try {
                valor = Double.parseDouble(entrada);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número decimal válido (ej: 12.50).");
            }
        }

        return valor;
    }

    // Lectura de BigDecimal
    public static BigDecimal leerBigDecimal(String mensaje) {

        BigDecimal numero = null;
        boolean valido = false;

        while (!valido) {
            try {
                System.out.println(mensaje);
                String entrada = TECLADO.nextLine();

                if (entrada == null) {
                    ColoresConsola.error("No se ha podido leer la entrada");
                } else {
                    entrada = entrada.trim();

                    if (entrada.equalsIgnoreCase("CANCELAR")) {
                        throw new IllegalArgumentException("CANCELAR");
                    }

                    if (entrada.isEmpty()) {
                        ColoresConsola.error("El valor no puede estar vacío");
                    } else {
                        // En caso de que pase las validaciones, intentamos cambiarlo
                        numero = new BigDecimal(entrada);
                        valido = true;
                    }
                }
            } catch (NumberFormatException e) {
                ColoresConsola.error("Formato numérico incorrecto. Usa un punto para los decimales");
            }
        }
        return numero;
    }

    public static String leerTextoOpcional(String mensaje) {
        String resultado = "";
        boolean valido = false;

        while (!valido) {
            System.out.println(mensaje);

            if (TECLADO.hasNextLine()) {
                String entrada = TECLADO.nextLine().trim();
                
                if (entrada.equalsIgnoreCase("CANCELAR")) {
                    throw new IllegalArgumentException("CANCELAR");
                }

                resultado = entrada;
                valido = true;
            }
        }

        return resultado;
    }
}
