package combox.geopen;

public interface ComboCheckBoxItem {

    /**
     * �����Ŀ��ֵ
     *
     * @return String
     */
    public String getValue();

    /**
     * �����Ŀ�����
     *
     * @return String
     */
    public String getLabel();

    /**
     * �����Ŀ�Ƿ����ѡ���״̬
     *
     * @return boolean
     */
    public boolean getState();

    /**
     * �����Ŀ�Ƿ��Ѿ������״̬
     *
     * @return boolean
     */
    public boolean getChecked();
}
