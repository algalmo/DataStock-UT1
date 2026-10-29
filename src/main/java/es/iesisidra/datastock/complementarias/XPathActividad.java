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
import java.io.File;

/**
 * Punto de entrada reservado para RF-FIC-07: consultas XPath.
 * <p>Actividad obligatoria e independiente del menú principal.</p>
 */
public final class XPathActividad {
    
    private XPathActividad() { }

    public static void main(String[] args) {
        System.out.println("===== CONSULTAS XPATH (RF-FIC-07) =====");
        String rutaArchivo = LectorConsola.leerTexto("Fichero XML: ");

        File archivo = new File(rutaArchivo.trim());
        if (!archivo.exists() || !archivo.isFile()) {
            System.out.println("Error: El fichero especificado no existe o no es un archivo válido.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            // Protección DTD / Entidades externas (XXE)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            boolean continuar = true;
            while (continuar) {
                System.out.println("\n1. Por código | 2. Por categoría | 3. Precio superior | 4. Contar | 0. Salir");
                int opcion = LectorConsola.leerEntero("Opción: ");

                switch (opcion) {
                    case 1 -> {
                        String cod = LectorConsola.leerTexto("Código: ");
                        String expresion = "/catalogo/producto[codigo='" + cod + "']";
                        NodeList nodos = (NodeList) xpath.evaluate(expresion, doc, XPathConstants.NODESET);
                        mostrarProductos(nodos);
                    }
                    case 2 -> {
                        int catId = LectorConsola.leerEntero("Categoría ID: ");
                        String expresion = "/catalogo/producto[categoriaId=" + catId + "]";
                        NodeList nodos = (NodeList) xpath.evaluate(expresion, doc, XPathConstants.NODESET);
                        mostrarProductos(nodos);
                    }
                    case 3 -> {
                        double precioMin = LectorConsola.leerDecimal("Precio mínimo: ");
                        String expresion = "/catalogo/producto[precio > " + precioMin + "]";
                        NodeList nodos = (NodeList) xpath.evaluate(expresion, doc, XPathConstants.NODESET);
                        mostrarProductos(nodos);
                    }
                    case 4 -> {
                        String expresion = "count(/catalogo/producto)";
                        Double total = (Double) xpath.evaluate(expresion, doc, XPathConstants.NUMBER);
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
        int contador = 0;
        for (int i = 0; i < nodos.getLength(); i++) {
            Element elem = (Element) nodos.item(i);
            String cod = elem.getElementsByTagName("codigo").item(0).getTextContent();
            String nom = elem.getElementsByTagName("nombre").item(0).getTextContent();
            String prec = elem.getElementsByTagName("precio").item(0).getTextContent();
            
            System.out.println(cod);
            System.out.println(nom);
            System.out.println(prec + " €\n");
            contador++;
        }
        System.out.println("Productos encontrados: " + contador);
    }
}
