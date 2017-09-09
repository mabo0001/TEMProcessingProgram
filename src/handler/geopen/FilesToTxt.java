/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.geopen.TEMProcessingProgramWin;

/**
 *
 * @author Administrator
 */
public class FilesToTxt {

    public static void savePara(FileWriter fw, double[] value, boolean isClosed) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("0.00000");
            String format1 = "%1$-" + 15 + "s";
            for (int i = 0; i < value.length; i++) {
                fw.write(String.format(format1, decimalFormat.format(value[i])));
                if (i != value.length - 1) {
//                    fw.write(",");
                } else {
                    fw.write("\n");
                }
            }
            //关闭
            if (isClosed == true) {
                fw.close();
            }
        } catch (IOException ex) {
        }
    }
}