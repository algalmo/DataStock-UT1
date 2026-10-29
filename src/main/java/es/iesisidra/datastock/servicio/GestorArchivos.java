package es.iesisidra.datastock.servicio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.util.List;
import es.iesisidra.datastock.modelo.Producto;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class GestorArchivos {

    // 01

    public String explorarRuta(String rutaTexto) {
        String resultado = "";

        if (rutaTexto == null || rutaTexto.trim().isEmpty()) {
            resultado = "La ruta introducida no es válida.";
        } else {
            Path ruta = Path.of(rutaTexto.trim());

            if (!Files.exists(ruta)) {
                resultado = "Error: La ruta especificada no existe.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("===== EXPLORAR RUTA =====\n");
                sb.append("Ruta: ").append(rutaTexto).append("\n");

                if (Files.isDirectory(ruta)) {
                    int totalFicheros = 0;
                    int totalDirectorios = 0;

                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(ruta)) {
                        for (Path p : stream) {
                            if (Files.isDirectory(p)) {
                                totalDirectorios++;
                                sb.append(String.format("%-10s %-20s\n", "[DIR]", p.getFileName()));
                            } else {
                                totalFicheros++;
                                long medida = 0;
                                try {
                                    medida = Files.size(p);
                                } catch (IOException e) {
                                    medida = 0;
                                }
                                String medidaTexto = medida + " bytes";
                                sb.append(String.format("%-10s %-20s %s\n", "[FICHERO]", p.getFileName(), medidaTexto));
                            }
                        }
                    } catch (IOException e) {
                        sb.append("Error al leer el directorio: ").append(e.getMessage()).append("\n");
                    }

                    sb.append("Total ficheros: ").append(totalFicheros)
                            .append(" | Directorios: ").append(totalDirectorios);

                } else if (Files.isRegularFile(ruta)) {
                    long tamano = 0;
                    try {
                        tamano = Files.size(ruta);
                    } catch (IOException e) {
                        tamano = 0;
                    }
                    sb.append(String.format("%-10s %-20s %d bytes\n", "[FICHERO]", ruta.getFileName(), tamano));
                    sb.append("Total ficheros: 1 | Directorios: 0");
                }

                resultado = sb.toString();
            }
        }

        return resultado;
    }

    // 02
    public String importarJSON(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "La ruta del archivo JSON no es válida.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());

            if (!Files.isRegularFile(ruta) || !Files.isReadable(ruta)) {
                resultado = "Error: El archivo JSON no existe, no es un fichero o no se puede leer.";
            } else {
                int encontrados = 0;
                int importados = 0;
                int rechazados = 0;
                boolean documentoRechazado = false;
                StringBuilder problemas = new StringBuilder();

                try (BufferedReader br = Files.newBufferedReader(ruta)) {
                    Gson gson = new Gson();

                    JsonElement raiz = JsonParser.parseReader(br);

                    JsonArray array = null;
                    if (raiz.isJsonObject()) {
                        JsonElement nodo = raiz.getAsJsonObject().get("productos");
                        if (nodo != null && nodo.isJsonArray()) {
                            array = nodo.getAsJsonArray();
                        }
                    }

                    if (array == null) {
                        documentoRechazado = true;
                        problemas.append("- Estructura incorrecta: se esperaba un objeto con el array 'productos'.\n");
                    } else {
                        encontrados = array.size();
                        int posicion = 0;

                        for (JsonElement elem : array) {
                            posicion++;

                            try {
                                Producto p = gson.fromJson(elem, Producto.class);
                                List<String> errores = inventario.validarProducto(p);

                                if (elem.isJsonObject() && !elem.getAsJsonObject().has("stock")) {
                                    errores.add("El stock es obligatorio.");
                                }

                                String etiqueta = "Producto " + posicion;
                                if (p != null && p.getCodigo() != null && !p.getCodigo().trim().isEmpty()) {
                                    etiqueta = p.getCodigo().trim();
                                }

                                if (!errores.isEmpty()) {
                                    rechazados++;
                                    problemas.append("- ").append(etiqueta).append(": ")
                                            .append(String.join(" ", errores)).append("\n");
                                } else if (inventario.existeCodigo(p.getCodigo())) {
                                    rechazados++;
                                    problemas.append("- ").append(etiqueta)
                                            .append(": código duplicado, se conserva el existente.\n");
                                } else {
                                    inventario.agregarProducto(p);
                                    importados++;
                                }
                            } catch (JsonParseException e) {
                                rechazados++;
                                problemas.append("- Producto ").append(posicion)
                                        .append(": tipos de datos inválidos (precio, stock o IDs no numéricos).\n");
                            }
                        }
                    }
                } catch (IOException | JsonParseException e) {
                    documentoRechazado = true;
                    problemas.append("- No se pudo interpretar el fichero: ").append(e.getMessage()).append("\n");
                }

                StringBuilder resumen = new StringBuilder();
                resumen.append("===== IMPORTAR JSON =====\n");
                resumen.append("Ruta: ").append(rutaArchivo).append("\n");

                if (documentoRechazado) {
                    resumen.append("INCIDENCIAS:\n").append(problemas);
                    resumen.append("Importación cancelada: no se ha añadido ningún producto.");
                } else {
                    resumen.append("Productos encontrados: ").append(encontrados).append("\n");
                    resumen.append("Importados: ").append(importados)
                            .append(" | Rechazados: ").append(rechazados).append("\n");

                    if (rechazados > 0) {
                        resumen.append("INCIDENCIAS:\n").append(problemas);
                        resumen.append("Importación finalizada con incidencias.");
                    } else {
                        resumen.append("Importación finalizada con éxito.");
                    }
                }

                resultado = resumen.toString();
            }
        }

        return resultado;
    }
    // 03
    public String importarXML(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "La ruta del archivo XML no es válida.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());

            if (!Files.exists(ruta) || !Files.isRegularFile(ruta)) {
                resultado = "Error: El archivo XML especificado no existe o no es un fichero válido.";
            } else {
                int encontrados = 0;
                int importados = 0;
                int rechazados = 0;
                StringBuilder problemas = new StringBuilder();

                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(ruta.toFile());

                    doc.getDocumentElement().normalize();

                    NodeList listaNodos = doc.getElementsByTagName("producto");
                    encontrados = listaNodos.getLength();

                    for (int i = 0; i < encontrados; i++) {
                        Node nodoProducto = listaNodos.item(i);

                        if (nodoProducto.getNodeType() == Node.ELEMENT_NODE) {
                            String codigo = "";
                            String nombre = "";
                            String precioTexto = "";
                            String stockTexto = "";
                            String idCategoriaTexto = "";
                            String idProveedorTexto = "";

                            NodeList hijos = nodoProducto.getChildNodes();
                            for (int j = 0; j < hijos.getLength(); j++) {
                                Node hijo = hijos.item(j);
                                String nombreEtiqueta = hijo.getNodeName();

                                if (nombreEtiqueta.equals("codigo")) {
                                    codigo = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("nombre") || nombreEtiqueta.equals("denominacion")) {
                                    nombre = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("precio")) {
                                    precioTexto = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("stock")) {
                                    stockTexto = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("idCategoria")
                                        || nombreEtiqueta.equals("categoriaId")) {
                                    idCategoriaTexto = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("idProveedor")
                                        || nombreEtiqueta.equals("proveedorId")) {
                                    idProveedorTexto = hijo.getTextContent();
                                }
                            }

                            BigDecimal precio = null;
                            if (precioTexto != null && !precioTexto.trim().isEmpty()) {
                                try {
                                    precio = new BigDecimal(precioTexto.trim());
                                } catch (NumberFormatException e) {
                                    precio = null;
                                }
                            }

                            int stock = -1;
                            if (stockTexto != null && !stockTexto.trim().isEmpty()) {
                                try {
                                    stock = Integer.parseInt(stockTexto.trim());
                                } catch (NumberFormatException e) {
                                    stock = -1;
                                }
                            }

                            int idCategoria = 0;
                            if (idCategoriaTexto != null && !idCategoriaTexto.trim().isEmpty()) {
                                try {
                                    idCategoria = Integer.parseInt(idCategoriaTexto.trim());
                                } catch (NumberFormatException e) {
                                    idCategoria = 0;
                                }
                            }

                            int idProveedor = 0;
                            if (idProveedorTexto != null && !idProveedorTexto.trim().isEmpty()) {
                                try {
                                    idProveedor = Integer.parseInt(idProveedorTexto.trim());
                                } catch (NumberFormatException e) {
                                    idProveedor = 0;
                                }
                            }

                            Producto p = new Producto();
                            p.setCodigo(codigo);
                            p.setNombre(nombre);
                            p.setPrecio(precio);
                            p.setStock(stock);
                            p.setCategoriaId(idCategoria);
                            p.setProveedorId(idProveedor);

                            List<String> errores = inventario.validarProducto(p);

                            if (errores.isEmpty()) {
                                boolean agregado = inventario.agregarProducto(p);
                                if (agregado) {
                                    importados++;
                                } else {
                                    rechazados++;
                                    problemas.append("- Producto con código '")
                                            .append(codigo)
                                            .append("': Código duplicado en el inventario.\n");
                                }
                            } else {
                                rechazados++;
                                String codMostrar = "Desconocido";
                                if (codigo != null && !codigo.trim().isEmpty()) {
                                    codMostrar = codigo;
                                }
                                problemas.append("- Producto con código '")
                                        .append(codMostrar)
                                        .append("': ")
                                        .append(String.join(", ", errores))
                                        .append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                    problemas.append("Error de lectura o formato XML: ").append(e.getMessage()).append("\n");
                }

                StringBuilder resumen = new StringBuilder();
                resumen.append("===== IMPORTAR XML =====\n");
                resumen.append("Ruta: ").append(rutaArchivo).append("\n");
                resumen.append("Leyendo documento XML...\n");
                resumen.append("Encontrados: ").append(encontrados).append(" | Importados: ").append(importados)
                        .append(" | Rechazados: ").append(rechazados).append("\n");

                if (rechazados > 0 || problemas.length() > 0) {
                    resumen.append("INCIDENCIAS:\n").append(problemas);
                    resumen.append("Importación finalizada con incidencias.");
                } else {
                    resumen.append("Importación finalizada.");
                }

                resultado = resumen.toString();
            }
        }

        return resultado;
    }

    // 05
    public String exportarJSON(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "Error: La ruta del archivo de salida no es válida.";
        } else if (inventario == null || inventario.getProductos() == null || inventario.getProductos().isEmpty()) {
            resultado = "Error: El inventario está vacío. No hay productos para exportar.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());
            List<Producto> lista = inventario.getProductos();
            int total = lista.size();

            try (BufferedWriter bw = Files.newBufferedWriter(ruta)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(lista, bw);

                StringBuilder sb = new StringBuilder();
                sb.append("Fichero generado correctamente.\n");
                sb.append("Productos exportados: ").append(total);

                resultado = sb.toString();
            } catch (IOException e) {
                resultado = "Error al escribir el archivo JSON: " + e.getMessage();
            }
        }

        return resultado;
    }

public String exportarXML(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "Error: La ruta del archivo de salida no es válida.";
        } else if (inventario == null || inventario.getProductos() == null || inventario.getProductos().isEmpty()) {
            resultado = "Error: El inventario está vacío. No hay productos para exportar.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());
            List<Producto> lista = inventario.getProductos();
            int total = lista.size();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                Element root = doc.createElement("catalogo");
                doc.appendChild(root);

                for (int i = 0; i < lista.size(); i++) {
                    Producto p = lista.get(i);

                    Element prodElem = doc.createElement("producto");

                    Element codElem = doc.createElement("codigo");
                    codElem.setTextContent((p.getCodigo() != null) ? p.getCodigo() : "");
                    prodElem.appendChild(codElem);

                    Element nomElem = doc.createElement("nombre");
                    nomElem.setTextContent((p.getNombre() != null) ? p.getNombre() : "");
                    prodElem.appendChild(nomElem);

                    Element precElem = doc.createElement("precio");
                    precElem.setTextContent((p.getPrecio() != null) ? p.getPrecio().toString() : "0.00");
                    prodElem.appendChild(precElem);

                    Element stockElem = doc.createElement("stock");
                    stockElem.setTextContent(String.valueOf(p.getStock()));
                    prodElem.appendChild(stockElem);

                    Element catElem = doc.createElement("categoriaId");
                    catElem.setTextContent(String.valueOf(p.getCategoriaId()));
                    prodElem.appendChild(catElem);

                    Element provElem = doc.createElement("proveedorId");
                    provElem.setTextContent(String.valueOf(p.getProveedorId()));
                    prodElem.appendChild(provElem);

                    root.appendChild(prodElem);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(ruta.toFile());
                transformer.transform(source, result);

                StringBuilder sb = new StringBuilder();
                sb.append("Fichero generado correctamente.\n");
                sb.append("Productos exportados: ").append(total);

                resultado = sb.toString();
            } catch (Exception e) {
                resultado = "Error al generar el archivo XML: " + e.getMessage();
            }
        }

        return resultado;
    }

    // 06

    public String guardarInventarioDAT(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "Error: La ruta del archivo no es válida.";
        } else if (inventario == null || inventario.getProductos() == null || inventario.getProductos().isEmpty()) {
            resultado = "Error: El inventario está vacío. No hay datos para guardar.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(ruta))) {
                oos.writeObject(inventario.getProductos());
                resultado = "Inventario guardado correctamente.\nProductos guardados: "
                        + inventario.getProductos().size();
            } catch (IOException e) {
                resultado = "Error al guardar el inventario: " + e.getMessage();
            }
        }

        return resultado;
    }

    public String recuperarInventarioDAT(String rutaArchivo, Inventario inventario) {
        String resultado = "";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "Error: La ruta del archivo no es válida.";
        } else {
            Path ruta = Path.of(rutaArchivo.trim());

            if (!Files.exists(ruta)) {
                resultado = "Error: La ruta especificada no existe.";
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(ruta))) {
                    List<Producto> productosCargados = (List<Producto>) ois.readObject();

                    inventario.getProductos().clear();
                    inventario.getProductos().addAll(productosCargados);

                    resultado = "Inventario recuperado correctamente.\nProductos disponibles: "
                            + inventario.getProductos().size();
                } catch (ClassNotFoundException | ClassCastException e) {
                    resultado = "Error: Fichero corrupto o incompatible.";
                } catch (IOException e) {
                    resultado = "Error de lectura en el archivo: " + e.getMessage();
                }
            }
        }

        return resultado;
    }

}
