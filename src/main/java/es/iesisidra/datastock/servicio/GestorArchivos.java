package es.iesisidra.datastock.servicio;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import es.iesisidra.datastock.modelo.Producto;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GestorArchivos {

    //01
    public String explorarRuta(String rutaTexto) {

        String resultado = " ";

        if (rutaTexto == null || rutaTexto.trim().isEmpty()) {
            resultado = "La ruta introducida no es válida";
        } else {
            Path ruta = Paths.get(rutaTexto.trim());

            if (!Files.exists(ruta)) {
                resultado = "Error: La ruta específica no existe";
            } else {
                StringBuilder sb = new StringBuilder();

                if (Files.isDirectory(ruta)) {
                    sb.append("---- DIRECTORIO: ").append(ruta.toAbsolutePath()).append(" ---\n");
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(ruta)) {
                        for (Path p : stream) {
                            String tipo = "";
                            if (Files.isDirectory(p)) {
                                tipo = "[DIR] ";
                            } else {
                                tipo = "[FIC] ";
                            }
                            sb.append(tipo).append(p.getFileName()).append("\n");
                        }
                    } catch (IOException e) {
                    }
                } else if (Files.isRegularFile(ruta)) {
                    sb.append("--- FICHERO: ").append(ruta.getFileName()).append(" ---\n");
                    sb.append("Ruta absoluta: ").append(ruta.toAbsolutePath()).append("\n");
                    try {
                        sb.append("Tamaño: ").append(Files.size(ruta)).append(" bytes\n");
                    } catch (Exception e) {
                        sb.append("Tamaño: No se pudo obtener\n");
                    }
                    if (Files.isReadable(ruta)) {
                        sb.append("Lectura: Sí\n");
                    } else {
                        sb.append("Lectura: No\n");
                    }

                    if (Files.isWritable(ruta)) {
                        sb.append("Escritura: Sí\n");
                    } else {
                        sb.append("Escritura: No\n");
                    }
                } else {
                    sb.append("La ruta existe pero no es un fichero regular ni un directorio.");
                }
                resultado = sb.toString();
            }
        }

        return resultado;
    }


    //02
    public String importarJSON(String rutaArchivo, Inventario inventario){
        String resultado = " ";

        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            resultado = "La ruta del archivo JSON no es válida. ";
        }else{
            Path ruta =Path.of(rutaArchivo.trim());

            if (!Files.exists(ruta) || !Files.isRegularFile(ruta)) {
                resultado = "Error: El archivo JSON especificado no existe o no es un fichero válido";
            }else{
                int encontrados = 0;
                int importados = 0;
                int rechazados = 0;

                StringBuilder problemas = new StringBuilder();

                try (BufferedReader br = Files.newBufferedReader(ruta)){
                    Gson gson = new Gson();
                    List<Producto> listaLeida = gson.fromJson(br, new TypeToken<ArrayList<Producto>>(){}.getType());
                    
                    if (listaLeida != null) {
                        encontrados = listaLeida.size();

                        for (Producto p : listaLeida) {
                            List<String> errores = inventario.validarProducto(p);

                            if (errores.isEmpty()) {
                                boolean adjuntado = inventario.agregarProducto(p);
                                if (adjuntado) {
                                    importados++;
                                }else{
                                    rechazados++;
                                    problemas.append("-Producto con código ").append(p.getCodigo()).append(" :Código duplicado en el inventario. \n");

                                }
                            }else{
                                rechazados++;
                                String codProducto = "Desconocido";
                                if (p != null && p.getCodigo() != null) {
                                    codProducto = p.getCodigo();
                                }

                                problemas.append("Producto con codigo").append(codProducto).append(" : ").append(String.join(", ", errores)).append("\n");
                            }

                        }
                    }
                
                } catch (IOException e) {
                    problemas.append("Error de lectura E/S: ").append(e.getMessage()).append("\n");
                }catch (Exception e) {
                    problemas.append("Error de formato JSON: ").append(e.getMessage()).append("\n");
                }
                StringBuilder resumen = new StringBuilder();
                resumen.append("--- RESUMEN DE IMPORTACIÓN JSON ---\n");
                resumen.append("Encontrados: ").append(encontrados).append("\n");
                resumen.append("Importados:  ").append(importados).append("\n");
                resumen.append("Rechazados:  ").append(rechazados).append("\n");
                if (rechazados > 0 || problemas.length() > 0) {
                    resumen.append("\nMotivos de rechazo y problemas:\n").append(problemas);
                }

                resultado = resumen.toString();
            }
        }
        return resultado;
    }

    //03
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

                            NodeList hijos = nodoProducto.getChildNodes();
                            for (int j = 0; j < hijos.getLength(); j++) {
                                Node hijo = hijos.item(j);
                                String nombreEtiqueta = hijo.getNodeName();

                                if (nombreEtiqueta.equals("codigo")) {
                                    codigo = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("nombre")) {
                                    nombre = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("precio")) {
                                    precioTexto = hijo.getTextContent();
                                } else if (nombreEtiqueta.equals("stock")) {
                                    stockTexto = hijo.getTextContent();
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

                            Producto p = new Producto();
                            p.setCodigo(codigo);
                            p.setNombre(nombre);
                            p.setPrecio(precio);
                            p.setStock(stock);

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
                resumen.append("== RESUMEN DE IMPORTACIÓN XML ==\n");
                resumen.append("Encontrados: ").append(encontrados).append("\n");
                resumen.append("Importados:  ").append(importados).append("\n");
                resumen.append("Rechazados:  ").append(rechazados).append("\n");

                if (rechazados > 0 || problemas.length() > 0) {
                    resumen.append("\nMotivos de rechazo y problemas:\n").append(problemas);
                }

                resultado = resumen.toString();
            }
        }

        return resultado;
    }

    
}
