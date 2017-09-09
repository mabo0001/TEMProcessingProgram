/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkbox.geopen;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Administrator
 */
public class TEMCheckBoxCellRenderer extends TEMCheckBox
        implements TableCellRenderer {

    public TEMCheckBoxCellRenderer(String[] strButtonTexts) {

        super(strButtonTexts);

    }

    @Override
    public Component getTableCellRendererComponent(
            JTable jtable,
            Object value,
            boolean bln,
            boolean bln1,
            int i,
            int i1) {
        if (value instanceof Integer) {
//            setSelectedIndex(((Integer) value).intValue());
        }
        return this;
    }
}
