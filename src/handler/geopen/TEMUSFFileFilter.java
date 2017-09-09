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
public class TEMUSFFileFilter extends JFileChooser {

    public TEMUSFFileFilter() {
        super();
        setAcceptAllFileFilterUsed(false);
        addFilter();
    }

    public String getSuf() {
        FileFilter fileFilter = this.getFileFilter();
        String desc = fileFilter.getDescription();
        String[] suffarr = desc.split(" ");//在addFilter 存在空格
        String suf = suffarr[0].equals("所有图形文件") ? "" : suffarr[0];
        return suf.toLowerCase();
    }

    public void addFilter() {
        this.addChoosableFileFilter(new MyFileFilter(new String[]{".usf"}, "usf (*.usf)"));
    }

    class MyFileFilter extends FileFilter {

        String[] suffarr;
        String description;

        public MyFileFilter() {
            super();
        }

        public MyFileFilter(String[] suffarr, String description) {
            super();
            this.suffarr = suffarr;
            this.description = description;
        }

        public boolean accept(File f) {
            for (String s : suffarr) {
                if (f.getName().toLowerCase().endsWith(s)) {
                    return true;
                }
            }
            return f.isDirectory();
        }

        public String getDescription() {
            return this.description;
        }
    }
}
