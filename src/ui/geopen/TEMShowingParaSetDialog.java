/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import handler.geopen.TEMIntegrationMethod;
import handler.geopen.TEMSetTableRowColor;
import handler.geopen.TEMSourceData;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.CellEditor;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Administrator
 */
public class TEMShowingParaSetDialog extends javax.swing.JDialog implements PropertyChangeListener {
    //表格处理参数

    private boolean FLAG_CLICK_HEADER = true;//点击表头时 弹出快捷输入
    //记住第几列
    private int recColumn = -1;
    //xy坐标列
    public int columm1 = 1;//文件名列
    public int columm2 = 2;//tx边长_长
    public int columm3 = 3;//tx边长_宽
    public int columm4 = 4;//tx中心x
    public int columm5 = 5;//tx中心y
    public int columm6 = 6;//tx方位角
    public int columm7 = 7;//匝数
    public int columm8 = 8;//接收x
    public int columm9 = 9;//接收y
    public int columm10 = 10;//接收z高程
    public int columm11 = 11;//经度
    public int columm12 = 12;//维度
    public int columm13 = 13;//等效面积
    public int columm14 = 14;//装置型式
    public int columm15 = 15;//关断时间
    public int columm16 = 16;//电流
    public int columm17 = 17;//第一道
    public int columm18 = 18;//第二道
    public int columm19 = 19;//第三道
    private int selectedHeadColumn = -1;//多选的列
    private boolean onlyOnceClickHeader = false;//只点击一次
    //主界面
    private TEMProcessingProgramWin frame;
    //装置型式切换
    private int arrayNum = 0;//0中心回线1大定源2重叠回线
    //大定源是否超出界限
    private boolean outBourndary = false;
    //保存通道是否选择
    public boolean selectC[] = new boolean[]{true, true, true};

    /**
     * Creates new form TEMShowingParaSetDialog
     */
    public TEMShowingParaSetDialog(TEMProcessingProgramWin frame, boolean modal) {
        super(frame, modal);
        this.frame = frame;
        initComponents();
        //设定大小及显示位置
        pack();
        Rectangle parentBounds = frame.getBounds();
        Dimension size = getSize();
        // Center in the parent
        int x = Math.max(0, parentBounds.x + (parentBounds.width - size.width) / 2);
        int y = Math.max(0, parentBounds.y + (parentBounds.height - size.height) / 2);
        setLocation(new Point(x, y));
        //设定table的奇数偶数行颜色
        TEMSetTableRowColor.makeFace(pointsParasTable);
        //设定方位角 关断时间不可见 在fitTableColumns方法里也需要重新设定 才可隐藏
        int[] columnIndex = new int[]{columm6, columm15};
        TableColumnModel tcm = pointsParasTable.getColumnModel();
        for (int i = 0; i < columnIndex.length; i++) {
            TableColumn tc = tcm.getColumn(columnIndex[i]);
            tc.setResizable(false);
            tc.setPreferredWidth(-1);
            tc.setMaxWidth(-1);
            tc.setMinWidth(-1);
        }
    }

    public void addHeadTableMouseListner(final JTable table, final JDialog dialog) {
        //监听鼠标点击标题
        final JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            //释放鼠标单击时启动   
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == e.BUTTON1 && header.columnAtPoint(e.getPoint()) > 1) {
//                if (onlyOnceClickHeader == false && e.getButton() == e.BUTTON1 && header.columnAtPoint(e.getPoint()) > 1) {
//                    onlyOnceClickHeader = true;
                    //取消所有选择   
                    if (!e.isShiftDown()) {
                        table.clearSelection();
                    }
                    //解决单选时没有确定 不能赋值的问题
                    lostTableFocus(table);
                    int countColumn = header.columnAtPoint(e.getPoint());
                    //获得单击的列数   
                    int column = header.columnAtPoint(e.getPoint());
                    //只选择一列   
                    table.addColumnSelectionInterval(column, column);
                    table.setColumnSelectionAllowed(true);
                    table.setRowSelectionAllowed(false);
                    int rowCount = table.getRowCount();
                    if (countColumn == 17 || countColumn == 18 || countColumn == 19) {//如果是道数列
                        boolean flagAllTrueOrFalse = true;
                        boolean tem = false;
                        int tem1 = 0;//第一行参数 都是false时 防止 不能全选
                        for (int i = 0; i < rowCount; i++) {
                            if (table.getValueAt(i, countColumn) instanceof Boolean && tem == false && tem1 == 0) {
                                tem = (Boolean) table.getValueAt(i, countColumn);
                                tem1 = 1;
                                continue;
                            } else if (table.getValueAt(i, countColumn) instanceof Boolean && (Boolean) table.getValueAt(i, countColumn) != tem && tem1 == 1) {
                                flagAllTrueOrFalse = false;
                                break;
                            }
                        }
//                        System.out.println(flagAllTrueOrFalse);
                        if (!flagAllTrueOrFalse) {//当不全是一样的时候 选定为一样
                            for (int i = 0; i < rowCount; i++) {
                                if (table.getValueAt(i, countColumn) == null || table.getValueAt(i, countColumn).toString().trim().equals("-1")) {
                                } else {
                                    table.setValueAt(true, i, countColumn);
                                }
                            }
                        } else {
                            for (int i = 0; i < rowCount; i++) {
                                if (table.getValueAt(i, countColumn) == null || table.getValueAt(i, countColumn).toString().trim().equals("-1")) {
                                } else {
                                    if ((Boolean) table.getValueAt(i, countColumn) == true) {
                                        table.setValueAt(false, i, countColumn);
                                        if (i == 0) {
                                            if (countColumn == 17) {
                                                selectC[0] = false;//17
                                            } else if (countColumn == 18) {
                                                selectC[1] = false;//18
                                            } else {
                                                selectC[2] = false;//19
                                            }
                                        }
                                    } else {
                                        table.setValueAt(true, i, countColumn);
                                        if (i == 0) {
                                            if (countColumn == 17) {
                                                selectC[0] = true;//17
                                            } else if (countColumn == 18) {
                                                selectC[1] = true;//18
                                            } else {
                                                selectC[2] = true;//19
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (countColumn == 14) {//装置型式
                        if (arrayNum == 0) {
                            for (int i = 0; i < rowCount; i++) {
                                table.setValueAt("中心回线", i, countColumn);
                            }
                            arrayNum++;
                        } else if (arrayNum == 1) {
                            for (int i = 0; i < rowCount; i++) {
                                table.setValueAt("大定源", i, countColumn);
                            }
                            arrayNum++;
                        } else if (arrayNum == 2) {
                            arrayNum = 0;
                            for (int i = 0; i < rowCount; i++) {
                                table.setValueAt("重叠回线", i, countColumn);
                            }
                        }
//                        System.out.println(arrayNum);
                    }
                    FLAG_CLICK_HEADER = true;
                    //获得点击表头的列数
                    selectedHeadColumn = header.columnAtPoint(e.getPoint());
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setShowingParaPop = new javax.swing.JPopupMenu();
        inputDia = new javax.swing.JMenuItem();
        setShowingStepDialog = new javax.swing.JDialog(TEMShowingParaSetDialog.this);
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        originalValSpinner = new javax.swing.JSpinner();
        stepSpinner = new javax.swing.JSpinner();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        integrationProgressBarDia = new javax.swing.JDialog(this,true);
        integrationProgressBar = new javax.swing.JProgressBar();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pointsParasTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        chooseTimeWinCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        currentLab3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        endTSpinner = new javax.swing.JSpinner();
        timeWins = new javax.swing.JSpinner();
        currentLab2 = new javax.swing.JLabel();
        startTSpinner = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        pos_integrationButton = new javax.swing.JButton();
        cancelButton1 = new javax.swing.JButton();
        setPosButton = new javax.swing.JButton();

        inputDia.setFont(new java.awt.Font("新宋体", 0, 12)); // NOI18N
        inputDia.setText("快捷输入数值");
        inputDia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputDiaActionPerformed(evt);
            }
        });
        setShowingParaPop.add(inputDia);

        setShowingParaPop.add(inputDia);

        setShowingStepDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setShowingStepDialog.setModal(true);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("参数设置"));

        jLabel6.setText("初始值：");

        jLabel7.setText("步  长：");

        originalValSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(0.5d)));

        stepSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(0.5d)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(originalValSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(originalValSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(stepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.setText("确定");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("退出");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout setShowingStepDialogLayout = new javax.swing.GroupLayout(setShowingStepDialog.getContentPane());
        setShowingStepDialog.getContentPane().setLayout(setShowingStepDialogLayout);
        setShowingStepDialogLayout.setHorizontalGroup(
            setShowingStepDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(setShowingStepDialogLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        setShowingStepDialogLayout.setVerticalGroup(
            setShowingStepDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setShowingStepDialogLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(setShowingStepDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        integrationProgressBarDia.setUndecorated(true);

        integrationProgressBar.setString("时窗积分");
        integrationProgressBar.setStringPainted(true);

        javax.swing.GroupLayout integrationProgressBarDiaLayout = new javax.swing.GroupLayout(integrationProgressBarDia.getContentPane());
        integrationProgressBarDia.getContentPane().setLayout(integrationProgressBarDiaLayout);
        integrationProgressBarDiaLayout.setHorizontalGroup(
            integrationProgressBarDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(integrationProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
        );
        integrationProgressBarDiaLayout.setVerticalGroup(
            integrationProgressBarDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(integrationProgressBarDiaLayout.createSequentialGroup()
                .addComponent(integrationProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("测点参数设置");
        setMinimumSize(new java.awt.Dimension(500, 300));
        setResizable(false);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "测点参数设置", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N

        pointsParasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "文件名", "Tx边长X(m)", "Tx边长Y(m)", "Tx中心点坐标X(m)", "Tx中心点坐标Y(m)", "Tx方位角(°) ", "Tx匝数", "Rx中心点坐标X(m) ", "Rx中心点坐标Y(m) ", "Rx高程Z(m) ", "Rx中心点经度", "Rx中心点纬度 ", "Rx等效面积(m²)  ", "装置形式", "关断时间(μs)", "供电电流(A)", "第一道  ", "第二道", "第三道"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, false, false, true, false, false, true, false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        pointsParasTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        pointsParasTable.setShowHorizontalLines(false);
        pointsParasTable.setShowVerticalLines(false);
        pointsParasTable.getTableHeader().setReorderingAllowed(false);
        pointsParasTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pointsParasTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pointsParasTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(pointsParasTable);

        chooseTimeWinCheckBox.setForeground(new java.awt.Color(0, 0, 255));
        chooseTimeWinCheckBox.setSelected(true);
        chooseTimeWinCheckBox.setText("积分时窗");
        chooseTimeWinCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chooseTimeWinCheckBoxStateChanged(evt);
            }
        });
        chooseTimeWinCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chooseTimeWinCheckBoxItemStateChanged(evt);
            }
        });
        chooseTimeWinCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseTimeWinCheckBoxActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        currentLab3.setText("终止时间");

        jLabel4.setText("时间窗口");

        endTSpinner.setModel(new javax.swing.SpinnerNumberModel(10.0d, 0.0010d, 80.0d, 0.0010d));
        endTSpinner.setToolTipText("<html>" + "25Hz：<b>" + 10 + " </b><br> " + "12.5Hz：<b>" +20 + " </b><br> "+ "6.25Hz：<b>" + 40 + " </b><br> "+ "3.125Hz：<b>" +80 + " </b><br> "+ "25Hz 高：<b>" + 10 + " </b><br> "+ "50Hz 高：<b>" +5 + " </b><br> "+ "100Hz 高：<b>" +2.5 + " </b><br> "+ "</html>");

        timeWins.setModel(new javax.swing.SpinnerNumberModel(30, 1, 1200, 1));

        currentLab2.setText("起始时间");

        startTSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0010d), Double.valueOf(0.0010d), null, Double.valueOf(0.0010d)));
        startTSpinner.setToolTipText("<html>" + "25Hz：<b>" + 0.033 + " </b><br> " + "12.5Hz：<b>" + 0.033 + " </b><br> "+ "6.25Hz：<b>" + 0.033 + " </b><br> "+ "3.125Hz：<b>" + 0.067 + " </b><br> "+ "25Hz 高：<b>" + 0.001 + " </b><br> "+ "50Hz 高：<b>" +0.001 + " </b><br> "+ "100Hz 高：<b>" +0.001 + " </b><br> "+ "</html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(currentLab2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startTSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(currentLab3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(endTSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(timeWins, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endTSpinner, startTSpinner, timeWins});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(currentLab2)
                    .addComponent(startTSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentLab3)
                    .addComponent(endTSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(timeWins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "编辑处理", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N

        pos_integrationButton.setText("积分");
        pos_integrationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pos_integrationButtonActionPerformed(evt);
            }
        });

        cancelButton1.setText("退出");
        cancelButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton1ActionPerformed(evt);
            }
        });

        setPosButton.setText("显示测点");
        setPosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPosButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pos_integrationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setPosButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton1, pos_integrationButton, setPosButton});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pos_integrationButton)
                    .addComponent(cancelButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setPosButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton1, pos_integrationButton, setPosButton});

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(chooseTimeWinCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(317, 317, 317)))
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(chooseTimeWinCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pos_integrationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pos_integrationButtonActionPerformed
        // TODO add your handling code here:
        setTabbedPaneVisual();//设定坐标图表可见
        setPos_Integration(true);
    }//GEN-LAST:event_pos_integrationButtonActionPerformed

    public void setPos_Integration(boolean integrationBoolean) {
        //防止series1重复添加 将集合类清零
        if (integrationBoolean == true) {
            //如果选择了积分
            if (!chooseTimeWinCheckBox.isSelected()) {
                int choice = JOptionPane.showConfirmDialog(this, "积分窗口没有选择，是否选择？", "选择积分", JOptionPane.OK_OPTION);
                if (choice == 0) {
                    chooseTimeWinCheckBox.setSelected(true);
                    return;
                } else {
                    // 设置数据点
                    if (TEMChartPanle.series1.getItemCount() == 0) {
//                        int posEqual = setPos();
                        if (outBourndary == true) {
                            setoutBourndaryFalse();
                        }
                    }
                }
            } else {
                // 设置数据点
//                int posEqual = setPos();
                int posEqual = -1;
                if (TEMChartPanle.series1.getItemCount() == 0) {
                    posEqual = setPos();
                }
                if (posEqual == 0) {//无相同坐标 输入参数无误
                    //积分
                    integration();
//                    frame.temData.flagFileType = 2;//代表已经积分 为了在调取gptm数据的时候 让积分不可用
                    //将图表添加到主界面内
                    frame.temData.addTEMChartPanel();
//                    JOptionPane.showMessageDialog(this, "积分完毕");
                    if (outBourndary == true) {
                        setoutBourndaryFalse();
                    }
                } else if (posEqual == 1) {
                    //积分
                    integration();
//                    frame.temData.flagFileType = 2;//代表已经积分
//                    JOptionPane.showMessageDialog(this, "积分完毕，坐标不予显示，因存在坐标相同点！");
                } else if (posEqual == -1) {
                    //积分
                    integration();
//                    JOptionPane.showMessageDialog(this, "积分完毕");
                }

            }
        } else {
            if (TEMChartPanle.series1.getItemCount() == 0) {
                if (outBourndary == true) {
                    setoutBourndaryFalse();
                }
            }
        }
    }

    public void setoutBourndaryFalse() {
        JOptionPane.showMessageDialog(this, "测点超出边框，请重新设定！");
        outBourndary = false;
    }

    /**
     * 清理缓存数据
     */
    public void clearSeries() {
        TEMChartPanle.series1.clear();
        TEMChartPanle.series2.clear();
        TEMChartPanle.series3.clear();
        TEMChartPanle.series4.clear();
        TEMChartPanle.series5.clear();
    }

    /**
     * 设置数据点
     */
    public int setPos() {
        //清理坐标
        clearSeries();
        //解决表格单选时没有确定 不能赋值的问题
        lostTableFocus(pointsParasTable);
        //先判断是否都选定好道数了
        int filesCount = TEMSourceData.temData.length;//文件数
        for (int i = 0; i < filesCount; i++) {
            int channelsCount = TEMSourceData.temData[i].length;//道数
            if (channelsCount == 1) {
                if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true) {
                } else {
                    JOptionPane.showMessageDialog(this, "无法积分!" + "发现第" + i + "行数据没有选定测点道数!");
                    return 2;
                }
            } else if (channelsCount == 2) {
                if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true
                        || (Boolean) pointsParasTable.getValueAt(i, columm18) == true) {
                } else {
                    JOptionPane.showMessageDialog(this, "无法积分!" + "发现第" + i + "行数据没有选定测点道数!");
                    return 2;
                }
            } else if (channelsCount == 3) {
                if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true
                        || (Boolean) pointsParasTable.getValueAt(i, columm18) == true
                        || (Boolean) pointsParasTable.getValueAt(i, columm19) == true) {
                } else {
                    JOptionPane.showMessageDialog(this, "无法积分!" + "发现第" + i + "行数据没有选定测点道数!");
                    return 2;
                }
            }
        }
        //添加计算参数数据
        int rowCounts = pointsParasTable.getRowCount();
        double x = -1;
        double y = -1;
        double z = -1;
        try {
            for (int i = 0; i < rowCounts; i++) {
                //添加坐标值
                x = Double.parseDouble(pointsParasTable.getValueAt(i, columm8).toString());
                y = Double.parseDouble(pointsParasTable.getValueAt(i, columm9).toString());
                z = Double.parseDouble(pointsParasTable.getValueAt(i, columm10).toString());
                updateArray(x, y, z, i);
                if (TEMSourceData.Array[i].toString().equalsIgnoreCase("大定源")) {
                    ArrayList<Double> xy = new ArrayList<Double>();
                    x = x;
                    y = y;
                    xy.add(x);
                    xy.add(y);
                    TEMChartPanle.series1.add(x, y);//series1只负责绘制边框
                    //如果是大定源需要判断 发射线框是否把所有测点包含在内
                    boolean outboundary = justifyLargeFixedSource(pointsParasTable);
                    if (this.outBourndary == false && outboundary == false) {
                        this.outBourndary = true;
                    }
                    TEMSourceData.xy_fileName.put(xy, pointsParasTable.getValueAt(i, columm1).toString().trim());//点位和文件名一一对应
                } else {
                    outBourndary = false;
                    ArrayList<Double> xy = new ArrayList<Double>();
                    xy.add(x);
                    xy.add(y);
                    TEMChartPanle.series1.add(x, y);//series1只负责绘制边框
                    TEMSourceData.xy_fileName.put(xy, pointsParasTable.getValueAt(i, columm1).toString().trim());//点位和文件名一一对应
                }
            }
            //更改装置型式类型
            frame.temData.addTEMChartPanel();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法积分，输入计算参数存在格式错误，请检查并修改！");
            return 2;
        }
        //检查表格坐标是不是一样 一样的话退出

//        boolean posEqual = testXY(pointsParasTable, columm8, columm9);
//        if (posEqual == true) {
////            clearSeries();//如果坐标有相同清楚所有点
//            frame.pointsPositionPanel.removeAll();
//            JOptionPane.showMessageDialog(this, "存在坐标相同点！");
//            return -1;
//        }

        if (frame.pointsPositionPanel.getComponents().length == 0) {//如果没有添加组件为零
            //将图表添加到主界面内
            frame.temData.addTEMChartPanel();
        }
        return 0;
    }

    public void updateArray(double x, double y, double z, int i) {
        TEMSourceData.rCenterX[i] = x;
        TEMSourceData.rCenterY[i] = y;
        TEMSourceData.rCenterZ[i] = z;
        TEMSourceData.z_fileName.put(pointsParasTable.getValueAt(i, columm1).toString().trim(), z);//高程和文件名一一对应
        //添加电流和接受面积 发射线框的边长,装置型式
        TEMSourceData.trCenterX[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm4).toString());
        TEMSourceData.trCenterY[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm5).toString());
        TEMSourceData.trLength[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm2).toString());
        TEMSourceData.trWidth[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm3).toString());
        TEMSourceData.rArea[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm13).toString());
        TEMSourceData.Array[i] = pointsParasTable.getValueAt(i, columm14).toString();
        TEMSourceData.current[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm16).toString());
        TEMSourceData.trTurns[i] = Double.parseDouble(pointsParasTable.getValueAt(i, columm7).toString());
    }

    public boolean justifyLargeFixedSource(JTable table) {
        //检查是不是所有值相等 2 3 4 5列内容
        int rows = table.getRowCount();
        /**
         * 发射线框最大最小值
         */
        double tL = Double.parseDouble(pointsParasTable.getValueAt(0, columm2).toString());
        double tW = Double.parseDouble(pointsParasTable.getValueAt(0, columm3).toString());
        double maxTX = Double.parseDouble(pointsParasTable.getValueAt(0, columm4).toString()) + tL / 2;
        double minTX = Double.parseDouble(pointsParasTable.getValueAt(0, columm4).toString()) - tL / 2;
        double maxTY = Double.parseDouble(pointsParasTable.getValueAt(0, columm5).toString()) + tW / 2;
        double minTY = Double.parseDouble(pointsParasTable.getValueAt(0, columm5).toString()) - tW / 2;
        /**
         * 接受线框最大最小值
         */
        double maxRX = TEMChartPanle.series1.getMaxX();
        double minRX = TEMChartPanle.series1.getMinX();
        double maxRY = TEMChartPanle.series1.getMaxY();
        double minRY = TEMChartPanle.series1.getMinY();
        if (maxTX < maxRX) {
            return false;
        }
        if (minTX > minRX) {
            return false;
        }
        if (maxTY < maxRY) {
            return false;
        }
        if (minTY > minRY) {
            return false;
        }
        return true;
    }

    public boolean testXY(JTable table, int xcolumn, int ycolumn) {
        int rowCount = table.getRowCount();
        double x = -1;
        double y = -1;
        HashSet<Double> pos = new HashSet<Double>();
        for (int i = 0; i < rowCount; i++) {
            x = Double.parseDouble(table.getValueAt(i, xcolumn).toString());
            y = Double.parseDouble(table.getValueAt(i, ycolumn).toString());
            pos.add(x * 0.000000001 + y);
        }
        if (pos.size() != rowCount) {
            return true;
        }
        return false;
    }

    /**
     * 让table失去焦点
     *
     * @param table
     */
    public void lostTableFocus(JTable table) {
        CellEditor ce = pointsParasTable.getCellEditor();
        if (ce != null) {
            pointsParasTable.getCellEditor().stopCellEditing();
        }
    }
    private void cancelButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_cancelButton1ActionPerformed

    private void pointsParasTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pointsParasTableMouseClicked
        // TODO add your handling code here:
        // 左键
        if (evt.getButton() == evt.BUTTON1) {
            FLAG_CLICK_HEADER = false;//点击任意的地方设为false
            int column = pointsParasTable.getSelectedColumn();
            if (column > 1) {
                pointsParasTable.setColumnSelectionAllowed(true);
                pointsParasTable.setRowSelectionAllowed(true);
            } else {
//                pointsParasTable.setColumnSelectionAllowed(false);
//                pointsParasTable.setRowSelectionAllowed(false);
            }
        }
        //弹出快捷输入 右键 
        int selectedColumn = pointsParasTable.getSelectedColumn();
        if (selectedColumn == selectedHeadColumn) {
            if (evt.getButton() == evt.BUTTON3 && FLAG_CLICK_HEADER == true && selectedHeadColumn < 17
                    && selectedHeadColumn != 14 && selectedHeadColumn != 11 && selectedHeadColumn != 12) {
                setShowingParaPop.show(pointsParasTable, evt.getX(), evt.getY());
            }
        }

    }//GEN-LAST:event_pointsParasTableMouseClicked

    private void chooseTimeWinCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseTimeWinCheckBoxActionPerformed
        // TODO add your handling code here:
        chooseTimeWinCheckBoxSelected();
    }//GEN-LAST:event_chooseTimeWinCheckBoxActionPerformed
    public void chooseTimeWinCheckBoxSelected() {
        if (chooseTimeWinCheckBox.isSelected()) {//只有选择积分才可用
            startTSpinner.setEnabled(true);
            endTSpinner.setEnabled(true);
            timeWins.setEnabled(true);
            pos_integrationButton.setEnabled(true);
        } else {
            startTSpinner.setEnabled(false);
            endTSpinner.setEnabled(false);
            timeWins.setEnabled(false);
            pos_integrationButton.setEnabled(false);
        }
    }
    private void inputDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputDiaActionPerformed
        // TODO add your handling code here:
        recColumn = pointsParasTable.getSelectedColumn();
        //可视化及初始化
        try {
            int[] rows = pointsParasTable.getSelectedRows();
            if (rows.length == 0) {
                Double firstValue;
                firstValue = 1.0;
//                Double firstValue = Double.parseDouble(pointsParasTable.getValueAt(0, recColumn).toString());
                originalValSpinner.setValue(firstValue);
            } else {
                Double firstValue = Double.parseDouble(pointsParasTable.getValueAt(rows[0], recColumn).toString());
                originalValSpinner.setValue(firstValue);
            }
            //显示对话框
            setShowingStepDialog.pack();
            ShowScope.setLocation(setShowingStepDialog);
            setShowingStepDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "第一个数据有误！");
            return;
        }
    }//GEN-LAST:event_inputDiaActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
        int[] tempRow = pointsParasTable.getSelectedRows();
        if (tempRow.length == 0) {
            int rowCounts = pointsParasTable.getRowCount();
            double originalValue = (Double) originalValSpinner.getValue();
            double step = (Double) stepSpinner.getValue();
            double value = 0D;
            for (int i = 0; i < rowCounts; i++) {
                value = originalValue + i * step;
                pointsParasTable.setValueAt(value, i, recColumn);
            }
        } else {
            double recInitialV = (Double) originalValSpinner.getValue();
            double recStep = Double.parseDouble(stepSpinner.getValue().toString());
//            System.out.println(recInitialV);
//            System.out.println(recStep);
//            System.out.println(tempRow.length);
            for (int i = 0; i < tempRow.length; i++) {
                double para = recInitialV + recStep * i;
                pointsParasTable.setValueAt(para, tempRow[i], recColumn);
            }
        }
        setShowingStepDialog.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if ("progress" == pce.getPropertyName()) {
            int progress = (Integer) pce.getNewValue();
            integrationProgressBar.setValue(progress);
            integrationProgressBar.setString("时窗积分：" + progress + "%");
        }
    }
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        setShowingStepDialog.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    public void integration() {
        //先失去焦点
        lostTableFocus(pointsParasTable);
        //更新数组
        double x = -1;
        double y = -1;
        double z = -1;
        for (int i = 0; i < pointsParasTable.getRowCount(); i++) {
            //添加坐标值
            x = Double.parseDouble(pointsParasTable.getValueAt(i, columm8).toString());
            y = Double.parseDouble(pointsParasTable.getValueAt(i, columm9).toString());
            z = Double.parseDouble(pointsParasTable.getValueAt(i, columm10).toString());
            updateArray(x, y, z, i);
        }
        //积分处理
        Task task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
        //进度条显示
        integrationProgressBarDia.pack();
        ShowScope.setLocation(integrationProgressBarDia);
        integrationProgressBarDia.setVisible(true);
        //只有积分完毕才能设置编辑按钮可用
//        frame.editButton.setEnabled(true);
//        frame.editMenuItem.setEnabled(true);
    }
    private void chooseTimeWinCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chooseTimeWinCheckBoxItemStateChanged
        // TODO add your handling code here:
//         chooseTimeWinCheckBoxSelected();
    }//GEN-LAST:event_chooseTimeWinCheckBoxItemStateChanged
    /**
     * 数据编辑
     */
    public void editData() {
        //判断有没有数据
        if (frame.pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
        } else {
            JOptionPane.showMessageDialog(this, "无测点数据文件，请先读取文件！");
            return;
        }
        //判断是否积分 gptm文件
        if (TEMSourceData.integrationValue.size() == 0) {
            JOptionPane.showMessageDialog(this, "文件没有积分，请先进行先积分！");
            return;
        }
        //退出设置对话框 位置放到这里 为了弹出编辑对话框
        setVisible(false);
        //将文件名赋值给TEMHandelChannelDataWin中的文件集合 为了根据文件名调相应的积分数据
        ArrayList<String> filesName = new ArrayList<String>();
        //为了删除数据做准备 先建立好 文件名——删除点的 hashMap
        HashMap<String, ArrayList> fileName_DeletedPoints = new HashMap<String, ArrayList>();
        //为了改变数据做准备 先建立好 文件名——改变点的 hashMap
        HashMap<String, ArrayList> fileName_ChangedPoints = new HashMap<String, ArrayList>();
        //用于存储每个文件 时间分界点 
        HashMap<String, Double> fileName_TimeBoundary = new HashMap<String, Double>();//用于存储每个文件 时间分界点 
        //临时存储各个文件对应的电压值集合
        HashMap<String, ArrayList> tempVoltageList = new HashMap<String, ArrayList>();
        //临时存储各个文件对应的时间集合
        HashMap<String, ArrayList> tempTimeList = new HashMap<String, ArrayList>();
        int rows = TEMSourceData.filesName.length;
        for (int i = 0; i < rows; i++) {
            String fileName = TEMSourceData.filesName[i];
            filesName.add(fileName);
//            System.out.println(fileName);
            //赋值给文件名_删除点集合 默认集合大小为零
            fileName_DeletedPoints.put(fileName, new ArrayList());
            //赋值给文件名_改变点集合 默认集合大小为零
            fileName_ChangedPoints.put(fileName, new ArrayList());
            //用于存储每个文件 时间分界点 
            fileName_TimeBoundary.put(fileName, -1D);
            //给各个文件的电压值赋值
            ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
//            System.out.println(fileName + "," + voltage_time);
            tempVoltageList.put(fileName, (ArrayList) voltage_time.get(0));
            //给各个文件的时间赋值
            tempTimeList.put(fileName, (ArrayList) voltage_time.get(1));
        }
        //赋值
        TEMHandelChannelDataWin handelChannelDataWin = new TEMHandelChannelDataWin(this);
        handelChannelDataWin.setFilesNameList(filesName);//赋值给TEMHandelChannelDataWin中的文件集合
        handelChannelDataWin.setFileName_TimeBoundary(fileName_TimeBoundary);//赋值给EMHandelChannelDataWin中的文件名_时间界限fileName_TimeBoundary集合
        handelChannelDataWin.setFileName_DeletedPoints(fileName_DeletedPoints);//赋值给EMHandelChannelDataWin中的文件名_删除点fileName_DeletedPoints集合
        handelChannelDataWin.setFileName_ChangedPoints(fileName_ChangedPoints);//赋值给EMHandelChannelDataWin中的文件名_改变点fileName_ChangedPoints集合
        handelChannelDataWin.setTempVoltageList(tempVoltageList);//赋值给EMHandelChannelDataWin中的文件名_电压值tempVoltageList集合
        handelChannelDataWin.setTempTimeList(tempTimeList);//赋值给EMHandelChannelDataWin中的文件名_时间tempTimeList集合
        //增加表格面板 默认为第一个文件 初始为零
        String fileName = filesName.get(TEMHandelChannelDataWin.recListIndex);//获得第一个文件的文件名
        handelChannelDataWin.getXyseriescollection().addSeries(handelChannelDataWin.extractVolt_Time(handelChannelDataWin.getTempVoltageList().get(fileName), handelChannelDataWin.getTempTimeList().get(fileName)));
        handelChannelDataWin.voltage_timeChartPanel.add(handelChannelDataWin.getHandlerVolt_TimeCharts().createDemoPanel("时间-电压曲线", "Time( ms )", "Voltage( mV/AM2 )", handelChannelDataWin.getXyseriescollection()));
        //增加标签数字
        handelChannelDataWin.filesAccountLabel.setText("文件总数：" + rows);
        handelChannelDataWin.fileNameLabel.setText("文件名：" + fileName);
        handelChannelDataWin.dataPointsLabel.setText("数据点：" + handelChannelDataWin.getTempVoltageList().get(fileName).size());
        //获得当前文件名
        handelChannelDataWin.setCurrentFileName(fileName);
        //可视化
        handelChannelDataWin.setTitle(fileName);
        handelChannelDataWin.pack();
        ShowScope.setLocation(handelChannelDataWin);
        handelChannelDataWin.setVisible(true);
        frame.setFixedRange(handelChannelDataWin.voltage_timeChartPanel, TEMIntegrationMethod.voltMin, TEMIntegrationMethod.voltMax);
        //获得焦点 为了监听键盘
        handelChannelDataWin.requestFocus();
    }
    private void setPosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPosButtonActionPerformed
        // TODO add your handling code here:
        setTabbedPaneVisual();//设定可见
        int posEqual = setPos();
        if (outBourndary == true) {
            setoutBourndaryFalse();
        }
    }//GEN-LAST:event_setPosButtonActionPerformed
    public void setTabbedPaneVisual() {
        frame.dataVisualTabbedPane.setSelectedIndex(0);
    }
    private void pointsParasTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pointsParasTableMouseReleased
        // TODO add your handling code here:
        //当选定一个部分列数据
        int partRows = pointsParasTable.getSelectedRowCount();
        int columns = pointsParasTable.getSelectedColumnCount();
        int column = pointsParasTable.getSelectedColumn();
        int[] unableCol = {0, 1, 11, 12, 14, 17, 18, 19};
        boolean equal = false;
        if (evt.getButton() == MouseEvent.BUTTON1 && partRows > 1 && columns == 1) {
            for (int i = 0; i < unableCol.length; i++) {
                if (unableCol[i] == column) {
                    equal = true;
                    break;
                }
            }
        }
        if (equal == false && evt.getButton() == MouseEvent.BUTTON3) {
            setShowingParaPop.show(pointsParasTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_pointsParasTableMouseReleased

    private void chooseTimeWinCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chooseTimeWinCheckBoxStateChanged
        // TODO add your handling code here:  chooseTimeWinCheckBoxSelected();
        chooseTimeWinCheckBoxSelected();
    }//GEN-LAST:event_chooseTimeWinCheckBoxStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton cancelButton1;
    public javax.swing.JCheckBox chooseTimeWinCheckBox;
    private javax.swing.JLabel currentLab2;
    private javax.swing.JLabel currentLab3;
    public static javax.swing.JSpinner endTSpinner;
    private javax.swing.JMenuItem inputDia;
    private javax.swing.JProgressBar integrationProgressBar;
    private javax.swing.JDialog integrationProgressBarDia;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JSpinner originalValSpinner;
    public static javax.swing.JTable pointsParasTable;
    public javax.swing.JButton pos_integrationButton;
    private javax.swing.JButton setPosButton;
    private javax.swing.JPopupMenu setShowingParaPop;
    private javax.swing.JDialog setShowingStepDialog;
    public static javax.swing.JSpinner startTSpinner;
    private javax.swing.JSpinner stepSpinner;
    private javax.swing.JPanel tablePanel;
    public static javax.swing.JSpinner timeWins;
    // End of variables declaration//GEN-END:variables

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */

        @Override
        public Void doInBackground() throws FileNotFoundException {
            setProgress(0);//给进度条添加数值默认
            int filesCount = TEMSourceData.temData.length;//文件数
            //开始积分
            double startT = Double.parseDouble(startTSpinner.getValue().toString());//转为秒
            double endT = Double.parseDouble(endTSpinner.getValue().toString());
            int wins = Integer.parseInt(timeWins.getValue().toString());
            for (int i = 0; i < filesCount; i++) {
                setProgress((int) (100.0 * i / (filesCount - 1)));
                double trLengthSingle = Double.parseDouble(pointsParasTable.getValueAt(i, columm2).toString());
                double trWidthSingle = Double.parseDouble(pointsParasTable.getValueAt(i, columm3).toString());
                double area = Double.parseDouble(pointsParasTable.getValueAt(i, columm13).toString());
                double current = Double.parseDouble(pointsParasTable.getValueAt(i, columm16).toString());
                double turns = Double.parseDouble(pointsParasTable.getValueAt(i, columm7).toString());
                double sampletRate = 0;
                if (TEMSourceData.fundfrequency[i] == 4) {
                    sampletRate = 15000;
                } else if (TEMSourceData.fundfrequency[i] < 4) {
                    sampletRate = 30000;
                } else if (TEMSourceData.fundfrequency[i] > 4) {
                    sampletRate = 1000000;
                }
                double[] voltValue1 = null;//临时存数数据
                double[] voltValue2 = null;//临时存数数据
                double[] voltValue3 = null;//临时存数数据
                int recChannels = 0;
                int channelsCount = TEMSourceData.temData[i].length;//道数
                if (channelsCount == 1) {//只有一道的时候
                    if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                            voltValue1 = new double[dataCount];
                            voltValue2 = new double[dataCount];
                            voltValue3 = new double[dataCount];
                            for (int m = 0; m < dataCount; m++) {
//                                voltValue1[m] = 10;
                                voltValue1[m] = TEMSourceData.temData[i][j][m];
                            }
                        }
                    }
                } else if (channelsCount == 2) {//只有前两道
                    if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            if (j == 0) {
                                int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                                voltValue1 = new double[dataCount];
                                if (voltValue2 == null) {
                                    voltValue2 = new double[dataCount];
                                }
                                voltValue3 = new double[dataCount];
                                for (int m = 0; m < dataCount; m++) {
                                    voltValue1[m] = TEMSourceData.temData[i][j][m];
                                }
                            }
                        }
                    }
                    if ((Boolean) pointsParasTable.getValueAt(i, columm18) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            if (j == 1) {
                                int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                                voltValue2 = new double[dataCount];
                                if (voltValue1 == null) {
                                    voltValue1 = new double[dataCount];
                                }
                                voltValue3 = new double[dataCount];
                                for (int m = 0; m < dataCount; m++) {
                                    voltValue2[m] = TEMSourceData.temData[i][j][m];
                                }
                            }
                        }
                    }
                } else if (channelsCount == 3) {//只有三道
                    if ((Boolean) pointsParasTable.getValueAt(i, columm17) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            if (j == 0) {
                                int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                                voltValue1 = new double[dataCount];
                                if (voltValue2 == null) {
                                    voltValue2 = new double[dataCount];
                                }
                                if (voltValue3 == null) {
                                    voltValue3 = new double[dataCount];
                                }
                                for (int m = 0; m < dataCount; m++) {
                                    voltValue1[m] = TEMSourceData.temData[i][j][m];
                                }
                            }
                        }
                    }
                    if ((Boolean) pointsParasTable.getValueAt(i, columm18) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            if (j == 1) {
                                int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                                voltValue2 = new double[dataCount];
                                if (voltValue1 == null) {
                                    voltValue1 = new double[dataCount];
                                }
                                if (voltValue3 == null) {
                                    voltValue3 = new double[dataCount];
                                }
                                for (int m = 0; m < dataCount; m++) {
                                    voltValue2[m] = TEMSourceData.temData[i][j][m];
                                }
                            }
                        }
                    }
                    if ((Boolean) pointsParasTable.getValueAt(i, columm19) == true) {
                        recChannels++;
                        for (int j = 0; j < channelsCount; j++) {
                            if (j == 2) {
                                int dataCount = TEMSourceData.temData[i][j].length;//数据点数
                                voltValue3 = new double[dataCount];
                                if (voltValue1 == null) {
                                    voltValue1 = new double[dataCount];
                                }
                                if (voltValue2 == null) {
                                    voltValue2 = new double[dataCount];
                                }
                                for (int m = 0; m < dataCount; m++) {
                                    voltValue3[m] = TEMSourceData.temData[i][j][m];
                                }
                            }
                        }
                    }
                }
                //合并所有数据 求平均值
                double[] voltValue = new double[voltValue1.length];
                for (int j = 0; j < voltValue1.length; j++) {
                    voltValue[j] = (voltValue1[j] + voltValue2[j] + voltValue3[j]) / recChannels;
                }
                TEMIntegrationMethod integrationMethod = new TEMIntegrationMethod();
//                System.out.println(pointsParasTable.getValueAt(i, columm1).toString().trim() + "," + startT + "," + endT + "," + voltValue.length);
                ArrayList integrationList = integrationMethod.AddressDatas(
                        startT, endT,
                        wins, area,
                        trLengthSingle,
                        trWidthSingle,
                        current,
                        turns,
                        voltValue,
                        sampletRate);//积分处理
                TEMSourceData.integrationValue.put(pointsParasTable.getValueAt(i, columm1).toString().trim(), integrationList);//文件名 积分数据
            }
//            JOptionPane.showMessageDialog(frame, "积分完毕");
            return null;
        }
        /*
         * Executed in event dispatching thread
         */

        @Override
        public void done() {
            integrationProgressBar.setVisible(false);
            integrationProgressBarDia.dispose();
        }
    }
}
