package Vista;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;


public class ModeloTabla extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final String[] columnas;
    private final Class<?>[] tipos;
    private List<Object[]> filas = new ArrayList<Object[]>();

   
    public ModeloTabla(String[] columnas, Class<?>[] tipos) {
        this.columnas = columnas;
        this.tipos = tipos;
    }

    
    public void setFilas(List<Object[]> nuevasFilas) {
        this.filas = nuevasFilas;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        return tipos[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return filas.get(fila)[columna];
    }
}
