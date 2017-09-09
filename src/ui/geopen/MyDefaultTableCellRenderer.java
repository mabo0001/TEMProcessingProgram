/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
//设置表内每行的颜色
public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (row % 2 == 0) {
            setBackground(Color.white); //设置奇数行底色
        } else if (row % 2 == 1) {
            setBackground(new Color(206, 231, 255)); //设置偶数行底色
            //(206, 231, 255)
        }
        return super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
    }
}
