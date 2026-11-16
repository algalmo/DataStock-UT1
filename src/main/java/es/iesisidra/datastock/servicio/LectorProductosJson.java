package es.iesisidra.datastock.servicio;

import java.util.List;

import es.iesisidra.datastock.modelo.Producto;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.io.BufferedReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.util.ArrayList;

public class LectorProductosJson {
    public record ResultadoLectura(
            int encontrados,
            List<Producto> validos,
            List<String> incidencias) {

        public ResultadoLectura {
            validos = List.copyOf(validos);
            incidencias = List.copyOf(incidencias);
        }
    }

    public ResultadoLectura leer(Path ruta) throws IOException {

        Objects.requireNonNull(ruta, "La ruta del JSON no puede ser nula.");

        if (!Files.isRegularFile(ruta) || !Files.isReadable(ruta)) {
            throw new IOException(
                    "El archivo JSON no existe, no es un fichero o no se puede leer.");
        }
        List<Producto> validos = new ArrayList<>();
        List<String> incidencias = new ArrayList<>();
        Gson gson = new Gson();

        try (BufferedReader br = Files.newBufferedReader(ruta)) {

            JsonElement raiz = JsonParser.parseReader(br);

            if (!raiz.isJsonObject()
                    || !raiz.getAsJsonObject().has("productos")
                    || !raiz.getAsJsonObject().get("productos").isJsonArray()) {

                throw new IllegalArgumentException(
                        "Estructura incorrecta: se esperaba un objeto con el array 'productos'.");
            }

            JsonArray registros = raiz.getAsJsonObject()
                    .getAsJsonArray("productos");

            int posicion = 0;

            for (JsonElement elem : registros) {
                posicion++;

                try {
                    Producto producto = gson.fromJson(elem, Producto.class);

                    List<String> errores = Inventario.validarDatosProducto(producto);

                    if (elem.isJsonObject() && !elem.getAsJsonObject().has("stock")) {
                        errores.add("El stock es obligatorio.");
                    }

                    if (tieneDecimales(elem, "stock")) {
                        errores.add("El stock debe ser un número entero.");
                    }

                    if (tieneDecimales(elem, "categoriaId")) {
                        errores.add("El ID de categoría debe ser un número entero.");
                    }

                    if (tieneDecimales(elem, "proveedorId")) {
                        errores.add("El ID de proveedor debe ser un número entero.");
                    }

                    if (errores.isEmpty()) {
                        validos.add(producto);
                    } else {
                        incidencias.add("Producto " + posicion + ": "
                                + String.join(" ", errores));
                    }

                } catch (JsonParseException | IllegalStateException | NumberFormatException e) {
                    incidencias.add("Producto " + posicion
                            + ": tipos de datos inválidos.");
                }
            }

            return new ResultadoLectura(registros.size(), validos, incidencias);

        }

    }

    private boolean tieneDecimales(JsonElement registro, String campo) {
        boolean decimales = false;

        if (registro.isJsonObject()) {
            JsonElement valor = registro.getAsJsonObject().get(campo);
            if (valor != null && valor.isJsonPrimitive() && valor.getAsJsonPrimitive().isNumber()) {
                decimales = valor.getAsBigDecimal().remainder(BigDecimal.ONE).signum() != 0;
            }
        }

        return decimales;
    }

}
