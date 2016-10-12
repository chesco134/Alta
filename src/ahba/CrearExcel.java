/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ahba;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author azaraf
 */
public class CrearExcel {
    private File file;
    private Object[][] data;
    
    public CrearExcel(Object[][] data) {
        this.data = data;
        showSaveDialog();
    }
    
    
    private void showSaveDialog(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File("/CONSULTA" + new SimpleDateFormat("ddMMyyyy_hhmmss").format(new Date()) +".csv"));
        
        if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
            crearExcel();
        }
    }
    
    private void crearExcel(){
        try {
            file.createNewFile();
            try (PrintWriter fout = new PrintWriter(new FileWriter(file))) {
                
                for (Object[] lst : data) {
                    for (Object currentValue : lst) {
                        fout.print((!currentValue.equals(lst[lst.length - 1]) ? currentValue.toString() + "," : currentValue.toString()));
                    }
                    fout.println();
                }
                fout.flush();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        Object[] options = {"Abrir",
            "No"};
        int open = JOptionPane.showOptionDialog(
                null,
                "¿Desea abrir el reporte?",
                "Reporte guardado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (open == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error en el archivo");
            }
        }
    }
    
    
}
