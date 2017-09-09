/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkbox.geopen;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author Administrator
 */
public class TEMCheckBoxCellEditor extends DefaultCellEditor implements ItemListener {

    TEMCheckBoxCellRenderer panel = null;

    public TEMCheckBoxCellEditor(JCheckBox checkBox, TEMCheckBoxCellRenderer panel) {
        super(checkBox);
        JCheckBox[] buttons = panel.getChannels();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof String) {
            String[] strs = value.toString().split("/");
            int length = strs.length;
            for (int i = 0; i < length; i++) {
//                setSelectedIndex(Integer.parseInt(strs[i]));
                System.out.println(i);
            }
        }
        return panel;
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        super.fireEditingStopped();
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
