/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 继承jcheckbox 并实现tablecellrender
 *
 * @author Administrator
 */
public class TEMTableRenderer extends JCheckBox implements TableCellRenderer {
    //此方法可以查考JDK文档的说明   

//    TEMTableRenderer() {//设定居中
//        setHorizontalAlignment(JLabel.CENTER);
//    }
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        if (value == null) {
            this.setSelected(false);
            this.setEnabled(false);
            return null;
        }
        Boolean b = (Boolean) value;
        this.setSelected(b.booleanValue());
        return this;
    }
}
