/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handler.geopen;

import java.awt.image.ImageFilter;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * 保存成图片格式
 *
 * @author wy
 */
public class TEMImageFileFilter extends JFileChooser {

    public TEMImageFileFilter() {
        super();
        setAcceptAllFileFilterUsed(false);
        addFilter();
    }

    public String getSuf() {
        // 获取文件过滤对象
        FileFilter fileFilter = getFileFilter();
        String desc = fileFilter.getDescription();
        String[] sufarr = desc.split(" ");
        String suf = sufarr[0].equals("所有图形文件") ? "" : sufarr[0];
        return suf.toLowerCase();
    }

    public void addFilter() {
        addChoosableFileFilter(new imageFilter(new String[]{".PNG", ".png"}, "png (*.png)"));
        addChoosableFileFilter(new imageFilter(new String[]{".JPG", ".jpg"}, "jpg (*.jpg)"));
        addChoosableFileFilter(new imageFilter(new String[]{".BMP"}, "bmp (*.bmp)"));
    }

    class imageFilter extends javax.swing.filechooser.FileFilter {

        String[] suffer;
        String description;

        public imageFilter() {
            super();
        }

        public imageFilter(String[] surf, String descrip) {
            super();
            suffer = surf;
            description = descrip;
        }

        public boolean accept(File f) {
            for (String s : suffer) {
                if (f.getName().toUpperCase().endsWith(s)) {
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
