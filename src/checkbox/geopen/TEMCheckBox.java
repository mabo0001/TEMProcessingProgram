/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkbox.geopen;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class TEMCheckBox extends JPanel {

    private JCheckBox[] channelChoice = null;

    public TEMCheckBox(String[] strButtonText) {
        channelChoice = new JCheckBox[strButtonText.length];
        for (int i = 0; i < strButtonText.length; i++) {
            channelChoice[i] = new JCheckBox(strButtonText[i]);
            channelChoice[i].setFocusPainted(false);
            add(channelChoice[i]);
        }
    }

    public JCheckBox[] getChannels() {

        return channelChoice;

    }

    public void setSelectedIndex(String[] index) {
        for (int i = 0; i < index.length; i++) {
            int num = Integer.parseInt(index[i]);
            channelChoice[num].setSelected(true);
        }

    }

    public int getSelectedIndex() {
        return -1;
    }
}
