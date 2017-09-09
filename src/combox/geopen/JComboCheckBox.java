package combox.geopen;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class JComboCheckBox extends JComboBox {

    private static final long serialVersionUID = 434969150380145423L;

    public JComboCheckBox() {
        super();
        this.setRenderer(new ComboCheckBoxRenderer());
    }

    public void addItem(ComboCheckBoxEntry item) {
        super.addItem(item);
    }

    public void addItem(boolean checked, boolean state, String id, String value) {
        super.addItem(new ComboCheckBoxEntry(checked, state, id, value));
    }

    public String[] getCheckedValues() {
        Vector<String> values = new Vector<String>();
        DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ComboCheckBoxEntry item = (ComboCheckBoxEntry) model
                    .getElementAt(i);
            boolean checked = item.getChecked();
            if (i == 0 && checked) {
                return null;
            }
            if (i != 0 && checked) {
                values.add(item.getValue());
            }
        }
        String[] retVal = new String[values.size()];
        values.copyInto(retVal);
        return retVal;
    }

    public void updateUI() {
        setUI(new ComboCheckBoxUI());
    }
}
