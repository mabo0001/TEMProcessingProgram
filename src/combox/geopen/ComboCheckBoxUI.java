package combox.geopen;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class ComboCheckBoxUI extends MetalComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new ComboCheckBoxUI();
    }

    protected ComboPopup createPopup() {
        return new ComboCheckPopUp(comboBox);
    }
}
