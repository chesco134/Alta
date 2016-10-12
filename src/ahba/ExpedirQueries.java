/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahba;

import connection.DatabaseConnection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Hector Torres
 */
public class ExpedirQueries {
    
    public static void exportAlumnosParticipantes(String fileName) throws SQLException, IOException {
        Connection con = new DatabaseConnection().getConnection();
        PreparedStatement pstmnt = con.prepareStatement(""
                + "select boleta, apPaterno, apMaterno, idPrograma_Academico from "
                + "(select * from Alumno_Cursa_Unidad_Aprendizaje join Alumno using(boleta))a "
                + "join "
                + "Apellidos_Alumno "
                + "using(boleta) group by boleta");
        ResultSet rs = pstmnt.executeQuery();
        PrintWriter pw = new PrintWriter(new FileWriter(createExportFile(fileName)));
        pw.println("boleta,apPaterno,apMaterno,idPrograma_Academico");
        while(rs.next())
            pw.println(rs.getString(1) + "," + rs.getString(2) + "," + rs.getString(3) + "," + rs.getString(4));
        pw.close();
        con.close();
    }
    
    private static File createExportFile(String fileName){
        Path path = Paths.get(System.getProperty("user.home"), "Documents", fileName);
        File file = new File(path.toString());
        return file;
    }
}
