package combox.geopen;

public class ComboCheckBoxEntry implements ComboCheckBoxItem {

    boolean checked;
    boolean state;
    String id;
    String value;

    public ComboCheckBoxEntry() {
        this.checked = false;
        this.state = true;
        this.id = "-1";
        this.value = "Empty Entry";
    }

    public ComboCheckBoxEntry(String id, String value) {
        this.checked = false;
        this.state = true;
        this.id = id;
        this.value = value;
    }

    public ComboCheckBoxEntry(boolean checked, String id, String value) {
        this.checked = checked;
        this.state = true;
        this.id = id;
        this.value = value;
    }

    public ComboCheckBoxEntry(boolean checked, boolean state, String id,
            String value) {
        this.checked = checked;
        this.state = state;
        this.id = id;
        this.value = value;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getValue() {
        return id;
    }

    public String getLabel() {
        return value;
    }

    public boolean getState() {
        return state;
    }

    @Override
    public String toString() {
        return id;
    }
}
