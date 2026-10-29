package es.iesisidra.datastock.servicio;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GestorArchivos {
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
}
