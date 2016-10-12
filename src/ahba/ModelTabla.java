/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahba;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author azaraf
 */
public class ModelTabla extends DefaultTableModel implements Serializable{
    
    private Object[][] datos;
    private String[] nombreColumnas;
    private JTable tabla;
    private TableRowSorter sorter;
    private Class[] tipoColumnas = {String.class, String.class, String.class};
    private boolean[] editables;

    public ModelTabla() {
        super();
        tabla = new JTable();
        tabla.setDragEnabled(false);
        tabla.setAutoscrolls(true);
        tabla.setAutoCreateRowSorter(true);
    }
    
    public ModelTabla(String[] columnas, boolean[] editables, Class[] types) {
        super(null, columnas);
        this.nombreColumnas = columnas;
        this.editables = editables;
        this.tipoColumnas = types;
        tabla = new JTable();
        tabla.setDragEnabled(false);
        tabla.setAutoscrolls(true);
        tabla.setAutoCreateRowSorter(true);
        
        createTable();
    }
    
    public void createTable() {
        tabla = new JTable(this) {
            @Override
            public Class getColumnClass(int column) {
                return tipoColumnas[column]; //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return editables[column]; //To change body of generated methods, choose Tools | Templates.
            }
        };

        tabla.setSelectionMode(2);
        sorter = new TableRowSorter<>(this);
        tabla.setRowSorter(sorter);

    }
    
    public void llenarDeDatos(Object[][] datos) {
        this.datos = datos;
        setDataVector(datos, nombreColumnas);
    }
    
    public JTable getTabla() {
        return tabla;
    }

    public void agregarTabla(JScrollPane scrollPane) {
        scrollPane.setViewportView(tabla);
    }
    
    public void setClasses(Class... tipoColumnas) {
        this.tipoColumnas = tipoColumnas;
    }

    public void setEditables(boolean... editables) {
        this.editables = editables;
    }

    public void setNombreColumnas(String[] nombreColumnas) {
        this.nombreColumnas = nombreColumnas;
    }

    public void setTipoColumnas(Class[] tipoColumnas) {
        this.tipoColumnas = tipoColumnas;
    }
    
}
