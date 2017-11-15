/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Administrator
 */
public class customFileFilterFormat extends FileFilter {

    public String[] suffarr;
    public String description;

    public customFileFilterFormat() {
        super();
    }

    public customFileFilterFormat(String[] suffarr, String description) {
        super();
        this.suffarr = suffarr;
        this.description = description;
    }

    public boolean accept(File f) {
        for (String s : suffarr) {
            if (f.getName().toLowerCase().endsWith(s)) {//小写
                return true;
            }
        }
        return f.isDirectory();
    }

    public String getDescription() {
        return this.description;
    }

    public static void removeFileFilter(JFileChooser jFileChooser) {
        javax.swing.filechooser.FileFilter[] fileFilters = jFileChooser.getChoosableFileFilters();
        for (int i = 0; i < fileFilters.length; i++) {
            jFileChooser.removeChoosableFileFilter(fileFilters[i]);
        }
    }

    public static void setFileFilter(JFileChooser jFileChooser, customFileFilterFormat... fff) {
        for (customFileFilterFormat ff : fff) {
            jFileChooser.setFileFilter(ff);
        }
    }
}
