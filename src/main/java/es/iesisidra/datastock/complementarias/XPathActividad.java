package es.iesisidra.datastock.complementarias;

import es.iesisidra.datastock.util.LectorConsola;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.math.BigDecimal;

/**
 * Punto de entrada reservado para RF-FIC-07: consultas XPath.
 * <p>
 * Actividad obligatoria e independiente del menú principal.
 * </p>
 */
public final class XPathActividad {

    private XPathActividad() {
    }

    public static void main(String[] args) {
        System.out.println("===== CONSULTAS XPATH =====");
        String rutaArchivo = LectorConsola.leerTexto("Fichero XML: ");

        File archivo = new File(rutaArchivo.trim());
        if (!archivo.exists() || !archivo.isFile()) {
            System.out.println("Error: El fichero especificado no existe o no es un archivo válido.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new DefaultHandler());
            Document doc = builder.parse(archivo);

            if (!doc.getDocumentElement().getNodeName().equals("catalogo")) {
                System.out.println("Error: La raíz del documento debe ser 'catalogo'.");
                return;
            }

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            boolean continuar = true;
            while (continuar) {
                System.out.println("\n1. Por código | 2. Por categoría | 3. Precio superior | 4. Contar | 0. Salir");
                int opcion = LectorConsola.leerEntero("Opción: ");

                switch (opcion) {
                    case 1 -> {
                        String cod = LectorConsola.leerTexto("Código: ").trim();
                        if (cod.isEmpty()) {
                            System.out.println("Error: El código no puede estar vacío.");
                        } else {
                            NodeList todos = (NodeList) xpath.evaluate("/catalogo/producto", doc,
                                    XPathConstants.NODESET);
                            mostrarCoincidencias(todos, cod);
                        }
                    }
                    case 2 -> {
                        int catId = LectorConsola.leerEntero("Categoría ID: ");
                        if (catId <= 0) {
                            System.out.println("Error: La categoría debe ser un entero positivo.");
                        } else {
                            String expresion = "/catalogo/producto[categoriaId=" + catId + "]";
                            NodeList nodos = (NodeList) xpath.evaluate(expresion, doc, XPathConstants.NODESET);
                            mostrarProductos(nodos);
                        }
                    }
                    case 3 -> {
                        double precioMin = LectorConsola.leerDecimal("Precio mínimo: ");
                        if (precioMin < 0 || Double.isNaN(precioMin) || Double.isInfinite(precioMin)) {
                            System.out.println("Error: El precio debe ser un número mayor o igual que 0.");
                        } else {

                            String precioTexto = BigDecimal.valueOf(precioMin).toPlainString();
                            String expresion = "/catalogo/producto[precio > " + precioTexto + "]";
                            NodeList nodos = (NodeList) xpath.evaluate(expresion, doc, XPathConstants.NODESET);
                            mostrarProductos(nodos);
                        }
                    }
                    case 4 -> {
                        Double total = (Double) xpath.evaluate("count(/catalogo/producto)", doc, XPathConstants.NUMBER);
                        System.out.println("Total productos en catálogo: " + total.intValue());
                    }
                    case 0 -> continuar = false;
                    default -> System.out.println("Opción no válida.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error de lectura, formato XML o seguridad (DTD/XXE): " + e.getMessage());
        }
    }

    private static void mostrarProductos(NodeList nodos) {
        for (int i = 0; i < nodos.getLength(); i++) {
            mostrarProducto((Element) nodos.item(i));
        }
        resumen(nodos.getLength());
    }

    private static void mostrarCoincidencias(NodeList todos, String codigoBuscado) {
        int contador = 0;
        for (int i = 0; i < todos.getLength(); i++) {
            Element elem = (Element) todos.item(i);
            if (textoDe(elem, "codigo").equalsIgnoreCase(codigoBuscado)) {
                mostrarProducto(elem);
                contador++;
            }
        }
        resumen(contador);
    }

    private static void mostrarProducto(Element elem) {
        System.out.println(textoDe(elem, "codigo") + "   " + textoDe(elem, "nombre")
                + "   " + textoDe(elem, "precio") + " €");
    }

    private static void resumen(int contador) {
        if (contador == 0) {
            System.out.println("No se ha encontrado ningún producto.");
        } else {
            System.out.println("Productos encontrados: " + contador);
        }
    }

    private static String textoDe(Element producto, String etiqueta) {
        NodeList lista = producto.getElementsByTagName(etiqueta);
        String texto = "(sin dato)";
        if (lista.getLength() > 0) {
            texto = lista.item(0).getTextContent().trim();
        }
        return texto;
    }
}
