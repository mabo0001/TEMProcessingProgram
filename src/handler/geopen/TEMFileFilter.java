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
public class TEMFileFilter extends JFileChooser {

    public TEMFileFilter() {
        super();
        setAcceptAllFileFilterUsed(false);
        addFilter();
    }

    public TEMFileFilter(String currentDirectoryPath) {
        super(currentDirectoryPath);
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
        this.addChoosableFileFilter(new MyFileFilter(new String[]{".gptm"}, "gptm (*.gptm)")); 
        this.addChoosableFileFilter(new MyFileFilter(new String[]{".ctm"}, "ctm (*.ctm)"));
        this.addChoosableFileFilter(new MyFileFilter(new String[]{".tm"}, "tm (*.tm)"));
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
                if (f.getName().toLowerCase().endsWith(s)) {//小写
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
