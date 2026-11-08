package es.iesisidra.datastock.modelo;

public class InfoBaseDatos {
    private final String nombreBaseDatos;
    private final String nombreGestor;
    private final String versionGestor;
    private final int totalProductos;

    public InfoBaseDatos(String nombreBaseDatos, String nombreGestor, String versionGestor, int totalProductos) {
        this.nombreBaseDatos = nombreBaseDatos;
        this.nombreGestor = nombreGestor;
        this.versionGestor = versionGestor;
        this.totalProductos = totalProductos;
    }

    public String getNombreBaseDatos() { return nombreBaseDatos; }
    public String getNombreGestor() { return nombreGestor; }
    public String getVersionGestor() { return versionGestor; }
    public int getTotalProductos() { return totalProductos; }
}
