package connection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import ahba.ModelTabla;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;

/**
 *
 * @author jcapiz
 */
public class AccionesTablaAlumnoCursaMateria {

    public static final String BIO = "INGENIERIA BIÓNICA";
    public static final String MEC = "INGENIERIA MECATRÓNICA";
    public static final String TEL = "INGENIERIA TELEMÁTICA";
    public static final String ISISA = "ISISA";

    public static void actualizaRecurse(String boleta, String ua, boolean esRecurse) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement cstmnt = con.prepareCall("{call actualizaRecurse(?,?,?)}");
            cstmnt.setString(1, boleta);
            cstmnt.setString(2, ua);
            cstmnt.setBoolean(3, esRecurse);
            cstmnt.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertaAlumnoCursaUnidadDeAprendizaje(String boleta, String ua, boolean esRecurse) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            try (Connection con = db.getConnection()) {
                CallableStatement cstmnt
                        = con.prepareCall("{call insertaAlumno_Cursa_Unidad_Aprendizaje(?,?,?)}");
                cstmnt.setString(1, boleta);
                cstmnt.setString(2, ua);
                cstmnt.setBoolean(3, esRecurse);
                cstmnt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminaAlumnoCursaUnidadDeAprendizaje(String boleta, String ua) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement cstmnt
                    = con.prepareCall("{call eliminaAlumno_Cursa_Unidad_Aprendizaje(?,?)}");
            cstmnt.setString(1, boleta);
            cstmnt.setString(2, ua);
            cstmnt.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean esRecurse(String boleta, String ua) {
        boolean esRecurse = false;
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select isRecurse "
                    + "from Alumno_Cursa_Unidad_Aprendizaje where boleta "
                    + "like ? and idUnidad_Aprendizaje like ?");
            pstmnt.setString(1, boleta);
            pstmnt.setString(2, ua);
            ResultSet rs = pstmnt.executeQuery();
            while (rs.next()) {
                esRecurse = rs.getInt(1) != 0;
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return esRecurse;
    }

    public static boolean cursaUnidadAprendizaje(String boleta, String ua) {
        boolean cursaUA = false;
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select count(*) "
                    + "from Alumno_Cursa_Unidad_Aprendizaje where boleta like "
                    + "? and idUnidad_Aprendizaje like ?");
            pstmnt.setString(1, boleta);
            pstmnt.setString(2, ua);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                cursaUA = rs.getInt(1) != 0;
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cursaUA;
    }

    public static boolean existeBoleta(String boleta) {
        boolean existeBoleta = false;
        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection con = db.getConnection();
            PreparedStatement pstmnt = con.prepareStatement("select count(*)"
                    + " from Alumno_Periodo_Academico where boleta like ?");
            pstmnt.setString(1, boleta);
            ResultSet rs = pstmnt.executeQuery();
            if (rs.next()) {
                existeBoleta = rs.getInt(1) != 0;
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existeBoleta;
    }

    public static String carrera(String boleta) {
        String carrera = null;
        try {
            DatabaseConnection db = new DatabaseConnection();
            Connection con = db.getConnection();
            PreparedStatement pstmt = con.prepareStatement("select idPrograma_Academico from Alumno where boleta like ?");
            pstmt.setString(1, boleta);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                carrera = rs.getString(1);
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carrera;
    }

    public static int insertaAlumno(String nombre, String apPaterno, String apMaterno, String boleta, String carrera) {
        int inserted = 0;
        DatabaseConnection db = new DatabaseConnection();

        try {
            Connection con = db.getConnection();
            CallableStatement cst = con.prepareCall("{call insertaAlumno(?, ?, ?, ?)}");
            cst.setString(1, boleta);
            cst.setString(2, apPaterno);
            cst.setString(3, apMaterno);
            cst.setString(4, carrera);
            cst.executeUpdate();
            inserted = 1;
        } catch (SQLException e) {
            System.out.println("First exception: " + e.getErrorCode());
            if (e.getMessage().contains("Duplicate")) {
                try {
                    Connection con2 = db.getConnection();
                    PreparedStatement pst = con2.prepareCall("INSERT INTO apellidosalumno VALUES(?,?,?)");
                    pst.setString(1, boleta);
                    pst.setString(2, apPaterno);
                    pst.setString(3, apMaterno);
                    pst.executeUpdate();
                    inserted = 2;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        try {
            Connection con = db.getConnection();
            CallableStatement cst = con.prepareCall("{call insertaNombreAlumno(?, ?)}");
            cst.setString(1, boleta);
            cst.setString(2, nombre);
            cst.executeUpdate();

            inserted += 3;

        } catch (SQLException e) {
            System.out.println("Second exception: " + e.getErrorCode());
            e.printStackTrace();
        }

        db.closeConnection();
        return inserted;
    }

    public static String[] getListaAcademias() {
        List<String> lista = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection();

        try {
            Connection con = db.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT nombre FROM academia");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.closeConnection();

        return lista.toArray(new String[0]);
    }

    public static String[] getListaCarreras() {
        List<String> lista = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection();

        try {
            Connection con = db.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT nombre FROM programa_academico");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.closeConnection();

        return lista.toArray(new String[0]);
    }

    public static String[] getListaMaterias() {
        List<String> lista = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection();

        try {
            Connection con = db.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT nombre FROM unidad_aprendizaje");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.closeConnection();

        return lista.toArray(new String[0]);
    }
    
    public static String[] getListaMaterias(String carrera) {
        List<String> lista = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection();

        try {
            Connection con = db.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT idUnidad_Aprendizaje FROM programa_academico_unidad_aprendizaje where idPrograma_Academico like ?");
            pst.setString(1, carrera);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.closeConnection();

        return lista.toArray(new String[0]);
    }

    public static boolean insertaNuevaUnidadDeAprendizaje(String materia, String carrera, String academia) {
        boolean inserted = false;
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement cst = con.prepareCall("{call insertaUnidad_Aprendizaje(?)}");
            cst.setString(1, materia);
            cst.executeUpdate();
            cst = con.prepareCall("{call insertaAcademia_Unidad_Aprendizaje(?,?)}");
            cst.setString(1, academia);
            cst.setString(2, materia);
            cst.executeUpdate();
            cst = con.prepareCall("{call insertaPrograma_Academico_Unidad_Aprendizaje(?,?)}");
            cst.setString(1, carrera);
            cst.setString(2, materia);
            cst.executeUpdate();
            inserted = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.closeConnection();
        return inserted;
    }

    public static int getParticipantes(String carrera) {
        int quantity = 0;
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement cst;
            if (carrera.equals("todos")) {
                cst = con.prepareCall("{call obtener_total_participantes()}");
            } else {
                cst = con.prepareCall("{call obtener_participantes_por_carrera(?)}");
                cst.setString(1, carrera);
            }
            cst.executeUpdate();
            ResultSet rs = cst.getResultSet();
            if (rs.next()) {
                quantity = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error " + ex.getErrorCode());
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        db.closeConnection();
        return quantity;
    }

    public static int getMatricula(String carrera) {
        int quantity = 0;
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement cst;
            if (carrera.equals("todos")) {
                cst = con.prepareCall("{call obtener_total_registrados}");
            } else {
                cst = con.prepareCall("{call obtener_total_registrados_por_carrera(?)}");
                cst.setString(1, carrera);
            }
            cst.executeUpdate();
            ResultSet rs = cst.getResultSet();
            if (rs.next()) {
                quantity = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        return quantity;
    }

    public static Object[][] getConsultaPorCarrera(String carrera, JScrollPane panel) {
        ModelTabla model = null;
        Object[][] data = null;
        String[] columnas = null;
        boolean[] editables = null;
        List<List<Object>> contenido = new ArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement pst = con.prepareCall("{call obtener_top_por_ua_pa(?)}");
            pst.setString(1, carrera);
            pst.executeUpdate();
            ResultSet rs = pst.getResultSet();
            List<String> columnNames = new ArrayList<>();
            editables = new boolean[rs.getMetaData().getColumnCount()];
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
                editables[i-1] = false;
            }
            while (rs.next()) {
                List<Object> lst = new ArrayList<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    lst.add(rs.getString(i + 1) == null ? "0" : rs.getString(i + 1));
                }
                contenido.add(lst);
            }
            columnas = columnNames.toArray(new String[0]);
            Object[] row = contenido.toArray();
            data = new Object[row.length][];
            for (int k = 0; k < row.length; k++) {
                data[k] = contenido.get(k).toArray();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        Class[] tipos = new Class[]{String.class, Integer.class, Integer.class};
        model = new ModelTabla(columnas, editables, tipos);
        model.agregarTabla(panel);
        model.llenarDeDatos(data);
        return data;
    }

    public static Object[][] getConsultaPorAcademia(String academia, JScrollPane panel) {
        ModelTabla model = null;
        Object[][] data = null;
        String[] columnas = null;
        boolean[] editables = null;
        List<List<Object>> contenido = new ArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement pst = con.prepareCall("{call obtener_top_por_ua_a(?)}");
            pst.setString(1, academia);
            pst.executeUpdate();
            ResultSet rs = pst.getResultSet();
            List<String> columnNames = new ArrayList<>();
            editables = new boolean[rs.getMetaData().getColumnCount()];
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
                editables[i-1] = false;
            }
            while (rs.next()) {
                List<Object> lst = new ArrayList<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    lst.add(rs.getString(i + 1) == null ? "0" : rs.getString(i + 1));
                }
                contenido.add(lst);
            }
            columnas = columnNames.toArray(new String[0]);
            Object[] row = contenido.toArray();
            data = new Object[row.length][];
            for (int k = 0; k < row.length; k++) {
                data[k] = contenido.get(k).toArray();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        Class[] tipos = new Class[]{String.class, Integer.class, Integer.class};
        model = new ModelTabla(columnas, editables, tipos);
        model.agregarTabla(panel);
        model.llenarDeDatos(data);
        return data;
    }
    
    public static Object[][] getConsultaPorMateriaDePrograma(String carrera, String materia, JScrollPane panel) {
        ModelTabla model = null;
        Object[][] data = null;
        String[] columnas = null;
        boolean[] editables = null;
        List<List<String>> contenido = new ArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement pst = con.prepareCall("{call obtener_demanda_de_ua_por_pa(?, ?)}");
            pst.setString(1, materia);
            pst.setString(2, carrera);
            pst.executeUpdate();
            ResultSet rs = pst.getResultSet();
            List<String> columnNames = new ArrayList<>();
            editables = new boolean[rs.getMetaData().getColumnCount()];
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
                editables[i-1] = false;
            }
            while (rs.next()) {
                List<String> lst = new ArrayList<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    lst.add(rs.getString(i + 1) == null ? "0" : rs.getString(i + 1));
                }
                contenido.add(lst);
            }
            columnas = columnNames.toArray(new String[0]);
            Object[] row = contenido.toArray();
            data = new Object[row.length][];
            for (int k = 0; k < row.length; k++) {
                data[k] = contenido.get(k).toArray();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        Class[] tipos = new Class[]{Integer.class, Integer.class};
        model = new ModelTabla(columnas, editables, tipos);
        model.agregarTabla(panel);
        model.llenarDeDatos(data);
        return data;
    }

    public static Object[][] getConsultaPorMateria(String materia, JScrollPane panel) {
        ModelTabla model = null;
        Object[][] data = null;
        String[] columnas = null;
        boolean[] editables = null;
        List<List<String>> contenido = new ArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement pst = con.prepareCall("{call obtener_demanda_de_ua(?)}");
            pst.setString(1, materia);
            pst.executeUpdate();
            ResultSet rs = pst.getResultSet();
            List<String> columnNames = new ArrayList<>();
            editables = new boolean[rs.getMetaData().getColumnCount()];
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
                editables[i-1] = false;
            }
            while (rs.next()) {
                List<String> lst = new ArrayList<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    lst.add(rs.getString(i + 1) == null ? "0" : rs.getString(i + 1));
                }
                contenido.add(lst);
            }
            columnas = columnNames.toArray(new String[0]);
            Object[] row = contenido.toArray();
            data = new Object[row.length][];
            for (int k = 0; k < row.length; k++) {
                data[k] = contenido.get(k).toArray();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        Class[] tipos = new Class[]{Integer.class, Integer.class};
        model = new ModelTabla(columnas, editables, tipos);
        model.agregarTabla(panel);
        model.llenarDeDatos(data);
        return data;
    }
    public static Object[][] getConsultaGeneral(JScrollPane panel) {
        ModelTabla model = null;
        Object[][] data = null;
        String[] columnas = null;
        boolean[] editables = null;
        List<List<String>> contenido = new ArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection con = db.getConnection();
            CallableStatement pst = con.prepareCall("{call obtener_top_todo()}");
            pst.executeUpdate();
            ResultSet rs = pst.getResultSet();
            List<String> columnNames = new ArrayList<>();
            editables = new boolean[rs.getMetaData().getColumnCount()];
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
                editables[i-1] = false;
            }
            while (rs.next()) {
                List<String> lst = new ArrayList<>();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    lst.add(rs.getString(i + 1) == null ? "0" : rs.getString(i + 1));
                }
                contenido.add(lst);
            }
            columnas = columnNames.toArray(new String[0]);
            Object[] row = contenido.toArray();
            data = new Object[row.length][];
            for (int k = 0; k < row.length; k++) {
                data[k] = contenido.get(k).toArray();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        db.closeConnection();
        Class[] tipos = new Class[]{String.class, String.class, Integer.class, Integer.class};
        model = new ModelTabla(columnas, editables, tipos);
        model.agregarTabla(panel);
        model.llenarDeDatos(data);
        return data;
    }
}
