/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import handler.geopen.TEMImageFileFilter;
import handler.geopen.TEMIntegrationMethod;
import handler.geopen.TEMResisAndDepth;
import handler.geopen.TEMSourceData;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import positionChart.geopen.DefinedChartPanel;
import positionChart.geopen.Resis_Volt_TimeChart;
/**
 *
 * @author Administrator
 */
public class TEMTime_Resis_VoltWin extends javax.swing.JFrame {

    public Resis_Volt_TimeChart resis_volt_timeChart;
    public static int recIndexFileNames = -1;//记录显示文件在总文件名数组中的位置
    public XYSeriesCollection xyseriescollection0 = new XYSeriesCollection();
    private XYSeriesCollection xyseriescollection1 = new XYSeriesCollection();
    private XYSeriesCollection xyseriescollection2 = new XYSeriesCollection();

    /**
     * Creates new form TEMTime_Resis_VoltWin
     */
    public TEMTime_Resis_VoltWin(XYSeriesCollection xyseriescollection) {
        initComponents();
        resis_volt_timeChart = new Resis_Volt_TimeChart(xyseriescollection);
        volt_timePanel.add(createDemoPanel("时间-电压曲线", "Time( ms )", "Voltage( mV/AM2 )", "VT", xyseriescollection0, false));
        resis_timePanel.add(createDemoPanel("时间-电阻率曲线", "Time( ms )", "Resistivity( ohm.m )", "RT", xyseriescollection1, false));
        depth_resisPanel.add(createDemoPanel("电阻率-深度曲线", "Resistivity( ohm.m )", "Depth( m )", "RD", xyseriescollection2, false));
    }

    /**
     * 创建面板
     *
     * @param title
     * @param xLabel
     * @param yLabel
     * @param chartVersion
     * @param dataset //
     * @param flagLineOrSingle//标记是单个点显示还是测线显示
     * @return \
     */
    public DefinedChartPanel createDemoPanel(
            String title, String xLabel, String yLabel, String chartVersion, XYDataset dataset, boolean flagLineOrSingle) {
        JFreeChart chart = resis_volt_timeChart.createChart(title, xLabel, yLabel, chartVersion, dataset, flagLineOrSingle);
        DefinedChartPanel chartPanel = new DefinedChartPanel(chart);
        //增加属性
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setPopupMenu(null);
        //设定图标最小值
        chartPanel.setPreferredSize(new Dimension(250, 200));
        //防止图形变形
        chartPanel.setMaximumDrawWidth(5000);
        chartPanel.setMaximumDrawHeight(5000);
        chartPanel.setDismissDelay(100000);//设定显示时间
//        chartPanel.setMouseWheelEnabled(true);
        return chartPanel;
    }

    /**
     * 当电压和时间数目不对应时取小 在TEMHandelChannelDataWin。java 有同样方法
     *
     * @param voltage
     * @param timeMid
     */
    public void updateVolTim(ArrayList voltage, ArrayList timeMid) {
        int counts = voltage.size();
        int counts1 = timeMid.size();
        int interval = Math.abs(counts1 - counts);
        if (counts1 - counts < 0) {
            counts = voltage.size();
            for (int i = 0; i < interval; i++) {
                voltage.remove(counts - 1 - i);
            }
        } else if (counts1 - counts > 0) {
            counts = timeMid.size();
            for (int i = 0; i < interval; i++) {
                timeMid.remove(counts - 1 - i);
            }
        }
    }

    /**
     * 建立时间电压数据组
     *
     * @param voltage
     * @param timeMid
     * @return
     */
    public XYSeries extractVolt_Time(ArrayList voltage, ArrayList timeMid) {
//        System.out.println(voltage.size() + "," + timeMid.size());
        updateVolTim(voltage, timeMid);
        //同步更新完全一一致
        XYSeries volt_time = new XYSeries("时间/电压");
        int counts = voltage.size();
        //更新数据
        for (int i = 0; i < counts; i++) {
            double vol = (Double) voltage.get(i);
            double timeM = (Double) timeMid.get(i);
            volt_time.add(timeM, vol);
//            System.out.println(timeM + "," + vol);
        }
        return volt_time;
    }

    /**
     * 建立时间电压数据组
     *
     * @param voltage
     * @param timeMid
     * @return
     */
    public XYSeries extractResis_Time(ArrayList voltage, ArrayList timeMid, ArrayList currents, ArrayList areas, ArrayList trlength, ArrayList trwidth, ArrayList turns) {
        int counts = voltage.size();
        XYSeries resis_time = new XYSeries("时间/电阻率");
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);//以ms为单位的
            double vol = (Double) voltage.get(i);
            double current = (Double) currents.get(i);
            double lengthValue = (Double) trlength.get(i);
            double widthValue = (Double) trwidth.get(i);
            double turnsValue = (Double) turns.get(i);
            double resis = TEMResisAndDepth.resistyCal(widthValue, lengthValue, current, vol / 1000, timeM / 1000, turnsValue, "");
            resis_time.add(timeM, resis);
//            System.out.println(resis + "=" + timeM);
//            System.out.println(widthValue + "===" + lengthValue + "===" + current + "===" + vol + "===" + timeM);
        }
        return resis_time;
    }

    /**
     * 建立视电阻率和深度数据组
     *
     * @param voltage
     * @param timeMid
     * @return
     */
    public XYSeries extractResis_Depth(ArrayList voltage, ArrayList timeMid, ArrayList currents, ArrayList areas, ArrayList trlength, ArrayList trwidth, ArrayList turns) {
        int counts = voltage.size();
        XYSeries resis_time = new XYSeries("电阻率/深度", false);
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);//以毫秒为单位的
            double vol = (Double) voltage.get(i);
            double current = (Double) currents.get(i);
            double lengthValue = (Double) trlength.get(i);
            double widthValue = (Double) trwidth.get(i);
            double turnsValue = (Double) turns.get(i);
            double resis = TEMResisAndDepth.resistyCal(widthValue, lengthValue, current, vol / 1000, timeM / 1000, turnsValue, "");
            double depth = TEMResisAndDepth.depthCal(resis, timeM);//深度是以毫秒为单位
            resis_time.add(resis, depth);
//            System.out.println(resis + "=" + depth);
        }
        return resis_time;
    }

    /**
     * 见深度和电阻率值保存在list集合中 用于保存dat数据格式
     *
     * @param voltage
     * @param timeMid
     * @param currents
     * @param areas
     * @param trlength
     * @param trwidth
     * @return
     */
    public ArrayList extractResis_DepthList(ArrayList voltage, ArrayList timeMid, ArrayList currents, ArrayList areas, ArrayList trlength, ArrayList trwidth, ArrayList turns) {
        int counts = voltage.size();
        ArrayList<ArrayList> depth_resisList = new ArrayList<ArrayList>();
        ArrayList<Double> depthList = new ArrayList<Double>();
        ArrayList<Double> resisList = new ArrayList<Double>();
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);//以秒为单位的
            double vol = (Double) voltage.get(i);
            double current = (Double) currents.get(i);
            double area = (Double) areas.get(i);
            double lengthValue = (Double) trlength.get(i);
            double widthValue = (Double) trwidth.get(i);
            double turnsValue = (Double) turns.get(i);
            double resis = TEMResisAndDepth.resistyCal(widthValue, lengthValue, current, vol / 1000, timeM / 1000, turnsValue, "");//时间以秒为单位
            double depth = TEMResisAndDepth.depthCal(resis, timeM);//深度是以毫秒为单位
            resisList.add(resis);
            depthList.add(depth);
        }
        depth_resisList.add(resisList);
        depth_resisList.add(depthList);
        return depth_resisList;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbarPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        savePicButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        inversionButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        lastButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        nextButton = new javax.swing.JButton();
        showPanel = new javax.swing.JPanel();
        resis_depthSplitPane = new javax.swing.JSplitPane();
        resis_vol_timeSplitPane = new javax.swing.JSplitPane();
        volt_timePanel = new javax.swing.JPanel();
        resis_timePanel = new javax.swing.JPanel();
        depth_resisPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        savePicMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        inversionMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        lastMenuItem = new javax.swing.JMenuItem();
        nextMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("pic/geopen/GP.png")));
        setMinimumSize(new java.awt.Dimension(400, 344));

        toolbarPanel.setMinimumSize(new java.awt.Dimension(100, 36));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel2.setText(" ");
        jToolBar1.add(jLabel2);

        savePicButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/pictrue28.png"))); // NOI18N
        savePicButton.setToolTipText("图片");
        savePicButton.setFocusable(false);
        savePicButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        savePicButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        savePicButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePicButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(savePicButton);

        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("|");
        jToolBar1.add(jLabel5);

        inversionButton1.setForeground(new java.awt.Color(0, 0, 255));
        inversionButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/inversion28.png"))); // NOI18N
        inversionButton1.setToolTipText("反演计算");
        inversionButton1.setFocusable(false);
        inversionButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        inversionButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        inversionButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inversionButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(inversionButton1);

        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("|");
        jToolBar1.add(jLabel6);

        lastButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/last32.png"))); // NOI18N
        lastButton.setToolTipText("上一个数据");
        lastButton.setFocusable(false);
        lastButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lastButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(lastButton);

        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("|");
        jToolBar1.add(jLabel7);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/next32.png"))); // NOI18N
        nextButton.setToolTipText("下一个数据");
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(nextButton);

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        showPanel.setLayout(new java.awt.GridLayout(1, 0));

        resis_depthSplitPane.setDividerSize(6);
        resis_depthSplitPane.setResizeWeight(0.5);
        resis_depthSplitPane.setOneTouchExpandable(true);

        resis_vol_timeSplitPane.setDividerSize(6);
        resis_vol_timeSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        resis_vol_timeSplitPane.setResizeWeight(0.5);
        resis_vol_timeSplitPane.setMinimumSize(new java.awt.Dimension(200, 300));
        resis_vol_timeSplitPane.setOneTouchExpandable(true);
        resis_vol_timeSplitPane.setPreferredSize(new java.awt.Dimension(200, 300));

        volt_timePanel.setBackground(new java.awt.Color(255, 255, 255));
        volt_timePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N
        volt_timePanel.setMinimumSize(new java.awt.Dimension(198, 150));
        volt_timePanel.setPreferredSize(new java.awt.Dimension(198, 150));
        volt_timePanel.setLayout(new java.awt.GridLayout(1, 0));
        resis_vol_timeSplitPane.setLeftComponent(volt_timePanel);

        resis_timePanel.setBackground(new java.awt.Color(255, 255, 255));
        resis_timePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N
        resis_timePanel.setMinimumSize(new java.awt.Dimension(200, 150));
        resis_timePanel.setPreferredSize(new java.awt.Dimension(200, 150));
        resis_timePanel.setLayout(new java.awt.GridLayout(1, 0));
        resis_vol_timeSplitPane.setBottomComponent(resis_timePanel);

        resis_depthSplitPane.setLeftComponent(resis_vol_timeSplitPane);

        depth_resisPanel.setBackground(new java.awt.Color(255, 255, 255));
        depth_resisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N
        depth_resisPanel.setMinimumSize(new java.awt.Dimension(200, 300));
        depth_resisPanel.setPreferredSize(new java.awt.Dimension(200, 300));
        depth_resisPanel.setLayout(new java.awt.GridLayout(1, 0));
        resis_depthSplitPane.setRightComponent(depth_resisPanel);

        showPanel.add(resis_depthSplitPane);

        jMenu2.setMnemonic(KeyEvent.VK_S);
        jMenu2.setText("保存(S)");

        savePicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        savePicMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/pictrue16.png"))); // NOI18N
        savePicMenuItem.setText("图片(P)");
        savePicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePicMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(savePicMenuItem);

        jMenuBar1.add(jMenu2);

        jMenu1.setMnemonic(KeyEvent.VK_I);
        jMenu1.setText("反演(I)");

        inversionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        inversionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/inversion16.png"))); // NOI18N
        inversionMenuItem.setText("反演计算(I)");
        inversionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inversionMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(inversionMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu3.setMnemonic(KeyEvent.VK_S);
        jMenu3.setText("显示(S)");

        lastMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        lastMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/last16.png"))); // NOI18N
        lastMenuItem.setText("上一个数据点(L)");
        lastMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(lastMenuItem);

        nextMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        nextMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/next16.png"))); // NOI18N
        nextMenuItem.setText("下一个数据点(N)");
        nextMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(nextMenuItem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(showPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inversionButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inversionButton1ActionPerformed
        // TODO add your handling code here:
        inversion();
    }//GEN-LAST:event_inversionButton1ActionPerformed
    /**
     * 单个点反演
     */
    public void inversion() {
        //先清理之前的图形
        xyseriescollection0.removeAllSeries();
        xyseriescollection1.removeAllSeries();
        xyseriescollection2.removeAllSeries();
        //进行深度与时间反演计算
        ArrayList voltage_time = TEMSourceData.integrationValue.get(TEMProcessingProgramWin.fileName);
//        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection0.addSeries(extractVolt_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1)));
        xyseriescollection1.addSeries(extractResis_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
                (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5), (ArrayList) voltage_time.get(6)));

        //深度与电阻率反演计算
//        ArrayList resis_depth = TEMSourceData.integrationValue.get(TEMProcessingProgramWin.fileName);
//        XYSeriesCollection xyseriescollection1 = new XYSeriesCollection();
        xyseriescollection2.addSeries(extractResis_Depth((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
                (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5), (ArrayList) voltage_time.get(6)));
//        depth_resisPanel.add(createDemoPanel("电阻率-深度曲线", "Resistivity( ohm.m )", "Depth( m )", "RD", xyseriescollection1, false));

        //设定可divide 实现反演图标的可视
        resis_vol_timeSplitPane.setOneTouchExpandable(true);
        resis_depthSplitPane.setOneTouchExpandable(true);
        resis_vol_timeSplitPane.getBottomComponent().setVisible(true);
        resis_depthSplitPane.getRightComponent().setVisible(true);
        resis_vol_timeSplitPane.setDividerLocation(resis_vol_timeSplitPane.getHeight() / 2);//关键不然不会自动划分 进行平分
        resis_depthSplitPane.setDividerLocation(resis_depthSplitPane.getWidth() / 2);
    }

    /**
     * 前后选择文件 进行反演
     *
     * @param fileName
     */
    public void inversion(String fileName) {
        //先清理之前的图形
        xyseriescollection0.removeAllSeries();
        xyseriescollection1.removeAllSeries();
        xyseriescollection2.removeAllSeries();
        //进行深度与时间反演计算
        ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
        //电压时间曲线
        xyseriescollection0.addSeries(extractVolt_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1)));
        TEMProcessingProgramWin.setFixedRange(volt_timePanel, TEMIntegrationMethod.voltMin, TEMIntegrationMethod.voltMax);
        //电阻率时间曲线
        xyseriescollection1.addSeries(extractResis_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
                (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5), (ArrayList) voltage_time.get(6)));
        //深度与电阻率反演计算
        xyseriescollection2.addSeries(extractResis_Depth((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
                (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5), (ArrayList) voltage_time.get(6)));
    }
    private void savePicButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePicButtonActionPerformed
        // TODO add your handling code here:
        saveImage(showPanel);
    }//GEN-LAST:event_savePicButtonActionPerformed

    private void inversionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inversionMenuItemActionPerformed
        // TODO add your handling code here:
        inversionButton1ActionPerformed(evt);
    }//GEN-LAST:event_inversionMenuItemActionPerformed
    public void saveImage(JPanel panel) {
        TEMImageFileFilter imageChooser = new TEMImageFileFilter();
        if (imageChooser.showSaveDialog(this) == TEMImageFileFilter.APPROVE_OPTION) {
            // 获取当前路径
            File currentDirectory = imageChooser.getCurrentDirectory();
            // 获取文件名
            String fileName = imageChooser.getSelectedFile().getName();
            String[] readFile = fileName.split("[.]");
            // 获取后缀名
            String suf = imageChooser.getSuf();
            // 组合保存路径
            String savePath = currentDirectory + "\\" + readFile[0] + "."
                    + suf;
            File imageMerge = new File(savePath);//监测是否有重复的
            if (imageMerge.exists()) {
                int count = JOptionPane.showConfirmDialog(this, "文件已存在,是否覆盖？", "文件存在", JOptionPane.OK_OPTION);
                if (count == 0) {
                    try {
                        // 将图片写到保存路径
                        exportOtherShapesImage(imageMerge, panel, suf);
                    } catch (Exception ie) {
                    }
                } else {
                    saveImage(panel);
                }
            } else {
                exportOtherShapesImage(imageMerge, panel, suf);
            }
        }
    }

    public boolean exportOtherShapesImage(File f, JPanel panel, String suffix) {
        Dimension imageSize = panel.getSize();
        BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        panel.paint(g);
        g.dispose();
        try {
            ImageIO.write(image, suffix, f);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void lastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastButtonActionPerformed
        // TODO add your handling code here:
        if (recIndexFileNames > 0) {
            recIndexFileNames--;
            String fileName = TEMSourceData.filesName[recIndexFileNames];
            TEMProcessingProgramWin.fileName = fileName;
            inversion(fileName);
            setTitle(fileName);

        }
    }//GEN-LAST:event_lastButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        if (recIndexFileNames < TEMSourceData.filesName.length) {
            recIndexFileNames++;
            if (recIndexFileNames == TEMSourceData.filesName.length) {
                recIndexFileNames = TEMSourceData.filesName.length - 1;
            }
            String fileName = TEMSourceData.filesName[recIndexFileNames];
            TEMProcessingProgramWin.fileName = fileName;
            inversion(fileName);
            setTitle(fileName);
        }
    }//GEN-LAST:event_nextButtonActionPerformed
    /**
     * 设定左边范围
     */
    public void upDateRange() {
        ChartPanel cp = (ChartPanel) volt_timePanel.getComponent(0);
//              System.out.println(time_resis_rolt.volt_timePanel.getComponentCount() + "+++++++");
        JFreeChart jfreechart = cp.getChart();
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        NumberAxis numberaxisY = (NumberAxis) xyplot.getRangeAxis();
        numberaxisY.setLowerBound(TEMIntegrationMethod.voltMin - TEMIntegrationMethod.voltMin * 0.5);
        numberaxisY.setUpperBound(TEMIntegrationMethod.voltMax + TEMIntegrationMethod.voltMax * 0.1);
//            numberaxisY.setDefaultAutoRange(new Range(TEMIntegrationMethod.voltMin - TEMIntegrationMethod.voltMin * 0.5, TEMIntegrationMethod.voltMax + TEMIntegrationMethod.voltMax * 0.1));
        numberaxisY.setDefaultAutoRange(new Range(TEMIntegrationMethod.voltMin - TEMIntegrationMethod.voltMin * 0.5, TEMIntegrationMethod.voltMax + TEMIntegrationMethod.voltMax * 0.1));
        System.out.println(TEMIntegrationMethod.voltMin + "+++++++");
    }
    private void savePicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePicMenuItemActionPerformed
        // TODO add your handling code here:
        savePicButtonActionPerformed(evt);
    }//GEN-LAST:event_savePicMenuItemActionPerformed

    private void lastMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastMenuItemActionPerformed
        // TODO add your handling code here:
        lastButtonActionPerformed(evt);
    }//GEN-LAST:event_lastMenuItemActionPerformed

    private void nextMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMenuItemActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_nextMenuItemActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel depth_resisPanel;
    private javax.swing.JButton inversionButton1;
    private javax.swing.JMenuItem inversionMenuItem;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton lastButton;
    private javax.swing.JMenuItem lastMenuItem;
    private javax.swing.JButton nextButton;
    private javax.swing.JMenuItem nextMenuItem;
    public javax.swing.JSplitPane resis_depthSplitPane;
    public javax.swing.JPanel resis_timePanel;
    public javax.swing.JSplitPane resis_vol_timeSplitPane;
    private javax.swing.JButton savePicButton;
    private javax.swing.JMenuItem savePicMenuItem;
    private javax.swing.JPanel showPanel;
    private javax.swing.JPanel toolbarPanel;
    public javax.swing.JPanel volt_timePanel;
    // End of variables declaration//GEN-END:variables
}
