package combox.geopen;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

public class ComboCheckPopUp extends BasicComboPopup {

    private static final long serialVersionUID = 3780666461108975905L;

    public ComboCheckPopUp(JComboBox cBox) {
        super(cBox);
    }

    @Override
    protected MouseListener createListMouseListener() {
        return new CheckBoxListMouseHandler();
    }

    protected class CheckBoxListMouseHandler extends MouseAdapter {

        public void mousePressed(MouseEvent anEvent) {
            if (anEvent.getX() < 13) {
                int index = list.getSelectedIndex();
                ComboCheckBoxEntry item = (ComboCheckBoxEntry) list.getModel()
                        .getElementAt(index);
                boolean checked = !item.getChecked();
                int size = list.getModel().getSize();
                item.setChecked(checked);
                updateListBoxSelectionForEvent(anEvent, false);
                Rectangle rect = list.getCellBounds(0, size - 1);
                list.repaint(rect);
            }
        }

        public void mouseReleased(MouseEvent anEvent) {
            if (anEvent.getX() > 13) {
                comboBox.setSelectedIndex(list.getSelectedIndex());
                comboBox.setPopupVisible(false);
            }
        }
    }
}
