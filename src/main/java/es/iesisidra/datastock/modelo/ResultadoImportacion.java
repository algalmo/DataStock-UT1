package es.iesisidra.datastock.modelo;

import java.util.ArrayList;
import java.util.List;

public class ResultadoImportacion {
    private int totalAnalizados = 0;
    private int insertados = 0;
    private int omitidosDuplicados = 0;
    private int rechazados = 0;
    private boolean rollbackEjecutado = false;
    private List<String> incidencias = new ArrayList<>();

    // Getters y Setters
    public int getTotalAnalizados() { return totalAnalizados; }
    public void incrementarAnalizados() { this.totalAnalizados++; }

    public int getInsertados() { return insertados; }
    public void setInsertados(int insertados) { this.insertados = insertados; }

    public int getOmitidosDuplicados() { return omitidosDuplicados; }
    public void incrementarOmitidos() { this.omitidosDuplicados++; }

    public int getRechazados() { return rechazados; }
    public void incrementarRechazados() { this.rechazados++; }

    public boolean isRollbackEjecutado() { return rollbackEjecutado; }
    public void setRollbackEjecutado(boolean rollbackEjecutado) { this.rollbackEjecutado = rollbackEjecutado; }

    public List<String> getIncidencias() { return incidencias; }
    public void addIncidencia(String incidencia) { this.incidencias.add(incidencia); }
}
