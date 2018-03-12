/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import handler.geopen.TEMIntegrationMethod;
import handler.geopen.TEMSourceData;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javaalgorithm.algorithm.Interpolation;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import positionChart.geopen.CircleFilledDrawer;
import positionChart.geopen.HandlerVolt_TimeCharts;

/**
 *
 * @author Administrator
 */
public class TEMHandelChannelDataWin extends javax.swing.JFrame {

    private ArrayList<String> filesNameList = new ArrayList<String>();//存放表格中的文件名
    private HandlerVolt_TimeCharts handlerVolt_TimeCharts;//绘制图形的图表
    private XYSeriesCollection xyseriescollection = new XYSeriesCollection();//临时存储单个文件电压时间数据的集合
    private HashMap<String, ArrayList> fileName_DeletedPoints = new HashMap<String, ArrayList>();//用于存储特定文件 删除的数据
    private HashMap<String, ArrayList> fileName_ChangedPoints = new HashMap<String, ArrayList>();//用于存储特定文件 改变的数据
    private HashMap<String, Double> fileName_TimeBoundary = new HashMap<String, Double>();//用于存储每个文件 时间分界点 
    private HashMap<String, ArrayList> tempVoltageList = new HashMap<String, ArrayList>();//临时存储所有文件对应的电压值
    private HashMap<String, ArrayList> tempTimeList = new HashMap<String, ArrayList>();//临时存储所有文件对应的时间
    private String currentFileName = "";//获得当前文件名
    private TEMShowingParaSetDialog tEMShowingParaSetDialog;//获得表格窗口对象
    private HashMap<String, ArrayList<XYSeries>> tempAmpSeries = new HashMap<String, ArrayList<XYSeries>>();//临时存储圆滑的幅值数据
    public static int recListIndex = 0;

    /**
     * 构造函数 和界面建立关系 为了让界面在图标鼠标事件处理的时候获得焦点
     *
     * @return
     */
    public HandlerVolt_TimeCharts getHandlerVolt_TimeCharts() {
        return handlerVolt_TimeCharts;
    }

    /**
     * *********************************************************获得设置对象参数方法**********************************************************
     *
     */
    public HashMap<String, Double> getFileName_TimeBoundary() {
        return fileName_TimeBoundary;
    }

    public void setFileName_TimeBoundary(HashMap<String, Double> fileName_TimeBoundary) {
        this.fileName_TimeBoundary = fileName_TimeBoundary;
    }

    public HashMap<String, ArrayList> getTempVoltageList() {
        return tempVoltageList;
    }

    public void setTempVoltageList(HashMap<String, ArrayList> tempVoltageList) {
        this.tempVoltageList = tempVoltageList;
    }

    public HashMap<String, ArrayList> getTempTimeList() {
        return tempTimeList;
    }

    public void setTempTimeList(HashMap<String, ArrayList> tempTimeList) {
        this.tempTimeList = tempTimeList;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public HashMap<String, ArrayList> getFileName_DeletedPoints() {
        return fileName_DeletedPoints;
    }

    public void setFileName_DeletedPoints(HashMap<String, ArrayList> fileName_DeletedPoints) {
        this.fileName_DeletedPoints = fileName_DeletedPoints;
    }

    public HashMap<String, ArrayList> getFileName_ChangedPoints() {
        return fileName_ChangedPoints;
    }

    public void setFileName_ChangedPoints(HashMap<String, ArrayList> fileName_ChangedPoints) {
        this.fileName_ChangedPoints = fileName_ChangedPoints;
    }
    //获得XYDataSet集合

    public XYSeriesCollection getXyseriescollection() {
        return xyseriescollection;
    }
    //设置XYDataSet集合

    public void setXyseriescollection(XYSeriesCollection xyseriescollection) {
        this.xyseriescollection = xyseriescollection;
    }

    //获得所有文件名
    public ArrayList<String> getFilesNameList() {
        return filesNameList;
    }
    //设置所有文件名

    public void setFilesNameList(ArrayList<String> filesNameList) {
        this.filesNameList = filesNameList;
    }

    /**
     * *********************************************************事件处理**********************************************************
     */
    /**
     * Creates new form TEMHandelChannelDataWin
     */
    public TEMHandelChannelDataWin(TEMShowingParaSetDialog tEMShowingParaSetDialog) {
        initComponents();
        this.tEMShowingParaSetDialog = tEMShowingParaSetDialog;
        //初始化图表
        handlerVolt_TimeCharts = new HandlerVolt_TimeCharts(this);//绘制图形的图表
        //增加单选按钮
        left_rightCutButtonGroup.add(rightRadioButton);
        left_rightCutButtonGroup.add(leftRadioButton);
        left_rightCutButtonGroup.add(deleteRadioButton);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        left_rightCutButtonGroup = new javax.swing.ButtonGroup();
        voltage_timeChartPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        rightRadioButton = new javax.swing.JRadioButton();
        leftRadioButton = new javax.swing.JRadioButton();
        deleteRadioButton = new javax.swing.JRadioButton();
        restorePrePointButton = new javax.swing.JButton();
        restoreCurrentAllPointButton = new javax.swing.JButton();
        restoreAllFilesButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        allFilesUnitCheckBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        lastPointButton = new javax.swing.JButton();
        nextPointButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        fiveSmoothPanel = new javax.swing.JPanel();
        fiveSmoothButton = new javax.swing.JButton();
        backFiveSmoothButton = new javax.swing.JButton();
        amp_phaseRadioButton = new javax.swing.JRadioButton();
        allFilesUnitCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        fileNameLabel = new javax.swing.JLabel();
        dataPointsLabel = new javax.swing.JLabel();
        filesAccountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("pic/geopen/GP.png")));
        setMinimumSize(new java.awt.Dimension(600, 500));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        voltage_timeChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "数据显示", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N
        voltage_timeChartPanel.setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "删除", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N

        rightRadioButton.setSelected(true);
        rightRadioButton.setText("删除左侧数据");
        rightRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightRadioButtonActionPerformed(evt);
            }
        });

        leftRadioButton.setText("删除右侧数据");
        leftRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftRadioButtonActionPerformed(evt);
            }
        });

        deleteRadioButton.setText("删除单点");
        deleteRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRadioButtonActionPerformed(evt);
            }
        });

        restorePrePointButton.setText("单点撤销");
        restorePrePointButton.setEnabled(false);
        restorePrePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restorePrePointButtonActionPerformed(evt);
            }
        });

        restoreCurrentAllPointButton.setText("当前撤销");
        restoreCurrentAllPointButton.setEnabled(false);
        restoreCurrentAllPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreCurrentAllPointButtonActionPerformed(evt);
            }
        });

        restoreAllFilesButton.setText("全部撤销");
        restoreAllFilesButton.setEnabled(false);
        restoreAllFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreAllFilesButtonActionPerformed(evt);
            }
        });

        saveButton.setText("删除");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("- - - - - - - -");

        allFilesUnitCheckBox.setText("所有文件");
        allFilesUnitCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allFilesUnitCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(restoreCurrentAllPointButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(leftRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rightRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(restoreAllFilesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(restorePrePointButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(allFilesUnitCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {restoreAllFilesButton, restoreCurrentAllPointButton, restorePrePointButton, saveButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(allFilesUnitCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leftRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(deleteRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(restorePrePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(restoreCurrentAllPointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(restoreAllFilesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {restorePrePointButton, saveButton});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "文件选择", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N

        lastPointButton.setText("上一个文件");
        lastPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastPointButtonActionPerformed(evt);
            }
        });

        nextPointButton.setText("下一个文件");
        nextPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPointButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastPointButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nextPointButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lastPointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextPointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lastPointButton, nextPointButton});

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "确定", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), new java.awt.Color(0, 0, 255))); // NOI18N

        cancelButton.setText("确定");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        fiveSmoothPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "数据滤波", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("宋体", 0, 12), java.awt.Color.blue)); // NOI18N

        fiveSmoothButton.setText("滤波");
        fiveSmoothButton.setMaximumSize(new java.awt.Dimension(105, 23));
        fiveSmoothButton.setMinimumSize(new java.awt.Dimension(105, 23));
        fiveSmoothButton.setPreferredSize(new java.awt.Dimension(105, 23));
        fiveSmoothButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fiveSmoothButtonActionPerformed(evt);
            }
        });

        backFiveSmoothButton.setText("<< 撤销");
        backFiveSmoothButton.setMaximumSize(new java.awt.Dimension(105, 23));
        backFiveSmoothButton.setMinimumSize(new java.awt.Dimension(105, 23));
        backFiveSmoothButton.setPreferredSize(new java.awt.Dimension(105, 23));
        backFiveSmoothButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backFiveSmoothButtonActionPerformed(evt);
            }
        });

        amp_phaseRadioButton.setSelected(true);
        amp_phaseRadioButton.setText("时间_电压");
        amp_phaseRadioButton.setEnabled(false);

        allFilesUnitCheckBox1.setSelected(true);
        allFilesUnitCheckBox1.setText("所有文件");
        allFilesUnitCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allFilesUnitCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fiveSmoothPanelLayout = new javax.swing.GroupLayout(fiveSmoothPanel);
        fiveSmoothPanel.setLayout(fiveSmoothPanelLayout);
        fiveSmoothPanelLayout.setHorizontalGroup(
            fiveSmoothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fiveSmoothPanelLayout.createSequentialGroup()
                .addComponent(allFilesUnitCheckBox1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(amp_phaseRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(backFiveSmoothButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fiveSmoothButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fiveSmoothPanelLayout.setVerticalGroup(
            fiveSmoothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fiveSmoothPanelLayout.createSequentialGroup()
                .addComponent(allFilesUnitCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(amp_phaseRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fiveSmoothButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(backFiveSmoothButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        fiveSmoothPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {backFiveSmoothButton, fiveSmoothButton});

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fiveSmoothPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fiveSmoothPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        fileNameLabel.setText("文件名：");
        fileNameLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        dataPointsLabel.setText("数据点：");
        dataPointsLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        filesAccountLabel.setText("文件总数：");
        filesAccountLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(filesAccountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(dataPointsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(fileNameLabel)
                .addComponent(filesAccountLabel)
                .addComponent(dataPointsLabel))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(voltage_timeChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(voltage_timeChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)))
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lastPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastPointButtonActionPerformed
        // TODO add your handling code here:
        //获得焦点
        requestFocus();
        //********************************弹出是否修改数据点的改变值*******************************************
        setChangeDataToOrigin();
        //********************************显示下一个文件*******************************************
        //清楚已存在数据
        if (xyseriescollection.getSeries().size() != 0 && recListIndex > 0) {
            xyseriescollection.removeAllSeries();
        }
        if (recListIndex > 0 && recListIndex < filesNameList.size()) {
            recListIndex--;
            String fileName = filesNameList.get(recListIndex);
            xyseriescollection.addSeries(extractVolt_Time(tempVoltageList.get(fileName), tempTimeList.get(fileName)));
            TEMProcessingProgramWin.setFixedRange(
                    voltage_timeChartPanel,
                    TEMIntegrationMethod.voltMin,
                    TEMIntegrationMethod.voltMax);
            clearAnnotation_Markers(fileName);
            if (fileName_TimeBoundary.get(fileName).doubleValue() == -1D) {
                //清除所有标记
            } else {
                //清除所有标记
                XYSeries ts = xyseriescollection.getSeries("时间/电压");//为了求得y值 进行标记
                for (int i = 0; i < ts.getItemCount(); i++) {
                    if (ts.getX(i).doubleValue() == fileName_TimeBoundary.get(fileName).doubleValue()) {
                        double x = ts.getX(i).doubleValue();
                        double y = ts.getY(i).doubleValue();
                        setDemoinMarkers(x, y);
                        break;
                    }
                }
            }
            //设定标题栏
            setTitle(fileName);
            //获得当前文件名
            currentFileName = fileName;
            //更新标签数字
            fileNameLabel.setText("文件名：" + fileName);
            dataPointsLabel.setText("数据点：" + tempVoltageList.get(fileName).size());
        } else if (recListIndex == 0) {
//            JOptionPane.showMessageDialog(this, "已到达第一个文件！");
            return;
        }
    }//GEN-LAST:event_lastPointButtonActionPerformed

    public void setDemoinMarkers(double x, double y) {
        if (!deleteRadioButton.isSelected()) {
            //绿色标记
            CircleFilledDrawer cd = handlerVolt_TimeCharts.getCd();
            XYPlot xyPlot = handlerVolt_TimeCharts.getChart().getXYPlot();
            xyPlot.clearAnnotations();
            handlerVolt_TimeCharts.setBestBid(new XYDrawableAnnotation(x, y, 11, 11, cd));
            XYAnnotation bestBid = handlerVolt_TimeCharts.getBestBid();//必须放在此位置
            xyPlot.addAnnotation(bestBid);
            //遮盖区域标记
            if (rightRadioButton.isSelected()) {
                handlerVolt_TimeCharts.setRight_leftDemonMinMax(handlerVolt_TimeCharts.getDataset().getSeries("时间/电压").getMinX());
                handlerVolt_TimeCharts.setIntervalmarker(new IntervalMarker(handlerVolt_TimeCharts.getRight_leftDemonMinMax(), x));
                handlerVolt_TimeCharts.getIntervalmarker().setLabelOffsetType(LengthAdjustmentType.EXPAND);
                handlerVolt_TimeCharts.getIntervalmarker().setPaint(new Color(200, 200, 255));
                handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(handlerVolt_TimeCharts.getIntervalmarker(), Layer.FOREGROUND);
            } else if (leftRadioButton.isSelected()) {
                handlerVolt_TimeCharts.setRight_leftDemonMinMax(handlerVolt_TimeCharts.getDataset().getSeries("时间/电压").getMaxX());
                handlerVolt_TimeCharts.setIntervalmarker(new IntervalMarker(x, handlerVolt_TimeCharts.getRight_leftDemonMinMax()));
                handlerVolt_TimeCharts.getIntervalmarker().setLabelOffsetType(LengthAdjustmentType.EXPAND);
                handlerVolt_TimeCharts.getIntervalmarker().setPaint(new Color(200, 200, 255));
                handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(handlerVolt_TimeCharts.getIntervalmarker(), Layer.FOREGROUND);
            }
            //绿色竖线界限
            ValueMarker valuemarker1 = new ValueMarker(x, Color.GREEN, new BasicStroke(2.0F));
            handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(valuemarker1, Layer.FOREGROUND);
        }
    }
    private void nextPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPointButtonActionPerformed
        // TODO add your handling code here:
        //获得焦点
        requestFocus();
        //********************************弹出是否修改数据点的改变值*******************************************
        setChangeDataToOrigin();
        //********************************显示下一个文件*******************************************
        //数据显示
        if (xyseriescollection.getSeries().size() != 0 && recListIndex < filesNameList.size() - 1) {
            xyseriescollection.removeAllSeries();
        }
        if (recListIndex >= 0 && recListIndex < filesNameList.size()) {//不大于文件列表数目
            //放在此位置是为了 避免两次都是index==0
            recListIndex++;
            if (recListIndex < filesNameList.size() - 1) {
                String fileName = filesNameList.get(recListIndex);
                xyseriescollection.addSeries(extractVolt_Time(tempVoltageList.get(fileName), tempTimeList.get(fileName)));
                //清除所有标记
                clearAnnotation_Markers(fileName);
                if (fileName_TimeBoundary.get(fileName).doubleValue() == -1D) {
                } else {
                    //添加标记 截取时间起始
                    XYSeries ts = xyseriescollection.getSeries("时间/电压");//为了求得y值 进行标记
                    for (int i = 0; i < ts.getItemCount(); i++) {
                        if (ts.getX(i).doubleValue() == fileName_TimeBoundary.get(fileName).doubleValue()) {
                            double x = ts.getX(i).doubleValue();
                            double y = ts.getY(i).doubleValue();
                            setDemoinMarkers(x, y);
                            break;
                        }
                    }
                }
                //设定标题栏
                setTitle(fileName);
                //获得当前文件名
                currentFileName = fileName;
                //更新标签数字
                fileNameLabel.setText("文件名：" + fileName);
                dataPointsLabel.setText("数据点：" + tempVoltageList.get(fileName).size());
            } else if (recListIndex == filesNameList.size() - 1) {
                String fileName = filesNameList.get(recListIndex);
//                ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
                xyseriescollection.addSeries(extractVolt_Time(tempVoltageList.get(fileName), tempTimeList.get(fileName)));
                //清除所有标记
                clearAnnotation_Markers(fileName);
                if (fileName_TimeBoundary.get(fileName).doubleValue() == -1D) {
                } else {
                    //添加标记 截取时间起始1
                    XYSeries ts = xyseriescollection.getSeries("时间/电压");//为了求得y值 进行标记
                    for (int i = 0; i < ts.getItemCount(); i++) {
                        if (ts.getX(i).doubleValue() == fileName_TimeBoundary.get(fileName).doubleValue()) {
                            double x = ts.getX(i).doubleValue();
                            double y = ts.getY(i).doubleValue();
                            setDemoinMarkers(x, y);
                            break;
                        }
                    }
                }
                //设定标题栏
                setTitle(fileName);
                //获得当前文件名
                currentFileName = fileName;
                //更新标签数字
                fileNameLabel.setText("文件名：" + fileName);
                dataPointsLabel.setText("数据点：" + tempVoltageList.get(fileName).size());
            } else {
                recListIndex = filesNameList.size() - 1;
//                JOptionPane.showMessageDialog(this, "已到达最后一个文件！");
                return;
            }
        }
    }//GEN-LAST:event_nextPointButtonActionPerformed
    public void setChangeDataToOrigin() {
        //改变数值点
        if (fileName_ChangedPoints != null) {
            String fileName = getCurrentFileName();
            XYSeries ts = ((XYSeriesCollection) handlerVolt_TimeCharts.chart.getXYPlot().getDataset()).getSeries("时间/电压");
            ArrayList posChangeList = fileName_ChangedPoints.get(fileName);
            int count = posChangeList.size();
            if (count != 0) {
                int choice = JOptionPane.showConfirmDialog(this, "是否确定修改拖动的数值？", "数据点拖动修改", JOptionPane.OK_OPTION);
                if (choice == 0) {
                } else {
                    tempTimeList.get(fileName).clear();
                    tempVoltageList.get(fileName).clear();
                    for (int j = 0; j < count; j++) {//先还原数据点
                        double x = (Double) ((ArrayList) posChangeList.get(j)).get(0);
                        double y = (Double) ((ArrayList) posChangeList.get(j)).get(1);
                        ts.updateByIndex((int) x, y);
                    }
                    for (int i = 0; i < ts.getItemCount(); i++) {//再重新赋值
                        tempTimeList.get(fileName).add(ts.getX(i));
                        tempVoltageList.get(fileName).add(ts.getY(i));
                    }
                }
            }
            fileName_ChangedPoints.get(fileName).clear();
        }
    }
    private void rightRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightRadioButtonActionPerformed
        // TODO add your handling code here:
        //获得焦点
        requestFocus();
        //设定还原数据按钮不可用
        setRestoreButtonEnable(false);
        //清除所有
        clearAnnotation_Markers();
        double x = fileName_TimeBoundary.get(currentFileName).doubleValue();
        double y = -1;
        if (rightRadioButton.isSelected() && x != -1D) {
            //分界时间点绿色  //绿色标记
            XYSeries ts = xyseriescollection.getSeries("时间/电压");//为了求得y值 进行标记
            for (int i = 0; i < ts.getItemCount(); i++) {
                if (ts.getX(i).doubleValue() == x) {
                    y = ts.getY(i).doubleValue();
                    break;
                }
            }
            CircleFilledDrawer cd = handlerVolt_TimeCharts.getCd();
            XYPlot xyPlot = handlerVolt_TimeCharts.getChart().getXYPlot();
            xyPlot.clearAnnotations();
            handlerVolt_TimeCharts.setBestBid(new XYDrawableAnnotation(x, y, 11, 11, cd));
            XYAnnotation bestBid = handlerVolt_TimeCharts.getBestBid();//必须放在此位置
            xyPlot.addAnnotation(bestBid);
            //绘制遮盖区域
            handlerVolt_TimeCharts.setRight_leftDemonMinMax(handlerVolt_TimeCharts.getDataset().getSeries("时间/电压").getMinX());
            handlerVolt_TimeCharts.setIntervalmarker(new IntervalMarker(handlerVolt_TimeCharts.getRight_leftDemonMinMax(), x));
            handlerVolt_TimeCharts.getIntervalmarker().setLabelOffsetType(LengthAdjustmentType.EXPAND);
            handlerVolt_TimeCharts.getIntervalmarker().setPaint(new Color(200, 200, 255));
            handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(handlerVolt_TimeCharts.getIntervalmarker(), Layer.FOREGROUND);
            //绿色竖线界限
            ValueMarker valuemarker1 = new ValueMarker(x, Color.GREEN, new BasicStroke(2.0F));
            handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(valuemarker1, Layer.FOREGROUND);
        }
    }//GEN-LAST:event_rightRadioButtonActionPerformed

    private void leftRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftRadioButtonActionPerformed
        // TODO add your handling code here:
        //获得焦点
        requestFocus();
        //设定还原数据按钮不可用
        setRestoreButtonEnable(false);
        //清除所有
        //设定还原数据按钮不可用
        clearAnnotation_Markers();
        handlerVolt_TimeCharts.getChart().getXYPlot().clearDomainMarkers();
        double x = fileName_TimeBoundary.get(currentFileName).doubleValue();
        double y = -1;
        if (leftRadioButton.isSelected() && x != -1) {
            //分界时间点绿色  //绿色标记
            XYSeries ts = xyseriescollection.getSeries("时间/电压");//为了求得y值 进行标记
            for (int i = 0; i < ts.getItemCount(); i++) {
                if (ts.getX(i).doubleValue() == x) {
                    y = ts.getY(i).doubleValue();
                    break;
                }
            }
            CircleFilledDrawer cd = handlerVolt_TimeCharts.getCd();
            XYPlot xyPlot = handlerVolt_TimeCharts.getChart().getXYPlot();
            xyPlot.clearAnnotations();
            handlerVolt_TimeCharts.setBestBid(new XYDrawableAnnotation(x, y, 11, 11, cd));
            XYAnnotation bestBid = handlerVolt_TimeCharts.getBestBid();//必须放在此位置
            xyPlot.addAnnotation(bestBid);
            //绘制遮盖区域
            handlerVolt_TimeCharts.setRight_leftDemonMinMax(handlerVolt_TimeCharts.getDataset().getSeries("时间/电压").getMaxX());
            handlerVolt_TimeCharts.setIntervalmarker(new IntervalMarker(x, handlerVolt_TimeCharts.getRight_leftDemonMinMax()));
            handlerVolt_TimeCharts.getIntervalmarker().setLabelOffsetType(LengthAdjustmentType.EXPAND);
            handlerVolt_TimeCharts.getIntervalmarker().setPaint(new Color(200, 200, 255));
            handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(handlerVolt_TimeCharts.getIntervalmarker(), Layer.FOREGROUND);
            //绿色竖线界限
            ValueMarker valuemarker1 = new ValueMarker(x, Color.GREEN, new BasicStroke(2.0F));
            handlerVolt_TimeCharts.getChart().getXYPlot().addDomainMarker(valuemarker1, Layer.FOREGROUND);
        }
    }//GEN-LAST:event_leftRadioButtonActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            clearAnnotation_Markers(currentFileName);
        }
    }//GEN-LAST:event_formKeyPressed
    /**
     * 清楚标记
     */
    public void clearAnnotation_Markers() {
        handlerVolt_TimeCharts.getChart().getXYPlot().clearDomainMarkers();
        handlerVolt_TimeCharts.getChart().getXYPlot().clearAnnotations();
        handlerVolt_TimeCharts.setX(-1);
    }

    /**
     * 清楚标记 特定文件
     */
    public void clearAnnotation_Markers(String fileName) {
        handlerVolt_TimeCharts.getChart().getXYPlot().clearDomainMarkers();
        handlerVolt_TimeCharts.getChart().getXYPlot().clearAnnotations();
        handlerVolt_TimeCharts.setX(-1);
        //清理
        fileName_TimeBoundary.put(fileName, -1D);
    }
    private void deleteRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRadioButtonActionPerformed
        // TODO add your handling code here:
        clearAnnotation_Markers();
        if (deleteRadioButton.isSelected()) {
            setRestoreButtonEnable(true);
        }
    }//GEN-LAST:event_deleteRadioButtonActionPerformed

    public void setRestoreButtonEnable(boolean b) {
        restorePrePointButton.setEnabled(b);
        restoreCurrentAllPointButton.setEnabled(b);
        restoreAllFilesButton.setEnabled(b);
    }
    private void restorePrePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restorePrePointButtonActionPerformed
        // TODO add your handling code here:
        if (fileName_DeletedPoints.size() != 0) {
            String fileName = currentFileName;
            int index = fileName_DeletedPoints.get(fileName).size() - 1;
            if (fileName_DeletedPoints.get(fileName).size() != 0) {
                double x = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(0);
                double y = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(1);
                tempTimeList.get(fileName).add(x);
                tempVoltageList.get(fileName).add(y);
                //从最后一个还原 然后删除最后一个
                fileName_DeletedPoints.get(fileName).remove(index);
                //更新当前的xyseriescollection
                XYSeries ts = xyseriescollection.getSeries("时间/电压");
                ts.addOrUpdate(x, y);
                //更新数据点数
                dataPointsLabel.setText("数据点：" + tempTimeList.get(fileName).size());
            }
        }
    }//GEN-LAST:event_restorePrePointButtonActionPerformed

    private void restoreCurrentAllPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreCurrentAllPointButtonActionPerformed
        // TODO add your handling code here:
        String fileName = currentFileName;
        int counts = fileName_DeletedPoints.get(fileName).size();
        if (fileName_DeletedPoints.get(fileName).size() != 0) {
            for (int i = 0; i < counts; i++) {
                int index = fileName_DeletedPoints.get(fileName).size() - 1;
                double x = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(0);
                double y = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(1);
                tempTimeList.get(fileName).add(x);
                tempVoltageList.get(fileName).add(y);
                //从最后一个还原 然后删除最后一个
                fileName_DeletedPoints.get(fileName).remove(index);
                //更新当前的xyseriescollection
                XYSeries ts = xyseriescollection.getSeries("时间/电压");
                ts.addOrUpdate(x, y);
            }
            //更新数据点数
            dataPointsLabel.setText("数据点：" + tempTimeList.get(fileName).size());
        }
    }//GEN-LAST:event_restoreCurrentAllPointButtonActionPerformed

    private void restoreAllFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreAllFilesButtonActionPerformed
        // TODO add your handling code here:
        int countFiles = fileName_DeletedPoints.size();
        if (countFiles != 0) {
            for (int i = 0; i < countFiles; i++) {
                String fileName = filesNameList.get(i);
                int counts = fileName_DeletedPoints.get(fileName).size();
                if (counts != 0) {
                    for (int j = 0; j < counts; j++) {
                        int index = fileName_DeletedPoints.get(fileName).size() - 1;
                        double x = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(0);
                        double y = (Double) ((ArrayList) fileName_DeletedPoints.get(fileName).get(index)).get(1);
                        tempTimeList.get(fileName).add(x);
                        tempVoltageList.get(fileName).add(y);
                        //从最后一个还原 然后删除最后一个
                        fileName_DeletedPoints.get(fileName).remove(index);
                        if (currentFileName.equalsIgnoreCase(fileName)) {
                            //更新当前的xyseriescollection
                            XYSeries ts = xyseriescollection.getSeries("时间/电压");
                            ts.addOrUpdate(x, y);
                            //更新数据点数
                            dataPointsLabel.setText("数据点：" + tempTimeList.get(fileName).size());
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_restoreAllFilesButtonActionPerformed

    private void allFilesUnitCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allFilesUnitCheckBoxActionPerformed
        // TODO add your handling code here:
        if (allFilesUnitCheckBox.isSelected()) {//选中时
            if (fileName_TimeBoundary.get(currentFileName).doubleValue() == -1D) {//当前文件没有选择时间界限时
                int count = JOptionPane.showConfirmDialog(this, "当前文件没有选择截取的时间界限，是否选择！", "选择时间界限", JOptionPane.OK_OPTION);
                if (count == 0) {
                    allFilesUnitCheckBox.setSelected(true);
                } else {
                    allFilesUnitCheckBox.setSelected(false);
                    return;
                }
            } else {//当当前文件存在时间界限时
                double x = fileName_TimeBoundary.get(currentFileName).doubleValue();//获得当前分界时间
                if (x != -1D) {
                    //为每个文件设置时间界限
                    int counts = fileName_TimeBoundary.size();
                    for (int i = 0; i < counts; i++) {
                        fileName_TimeBoundary.put(filesNameList.get(i), x);
                    }
                }
            }
        } else {//取消时
            int counts = fileName_TimeBoundary.size();
            for (int i = 0; i < counts; i++) {
                fileName_TimeBoundary.put(filesNameList.get(i), -1D);
            }
            //清理当前显示
            clearAnnotation_Markers();
        }
    }//GEN-LAST:event_allFilesUnitCheckBoxActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
        //*********时间界限处理***************************************************************
        int counts = filesNameList.size();//获得文件数
        //赋值给timeList 为了删除区域选点数据//删除处理
        for (int i = 0; i < counts; i++) {
            String fileName = filesNameList.get(i);
            double timeBoundary = fileName_TimeBoundary.get(fileName);//时间界限
            if (rightRadioButton.isSelected() && timeBoundary != -1) {//右截取
                int tempTimeCounts = tempTimeList.get(fileName).size();
                for (int j = 0; j < tempTimeCounts; j++) {
                    double x = (Double) tempTimeList.get(fileName).get(j);
                    if (x < timeBoundary) {
//                        System.out.println(j + "=====" + x + "/////" + timeBoundary);
                        tempTimeList.get(fileName).remove(j);
                        tempVoltageList.get(fileName).remove(j);
                        j = 0;
                        tempTimeCounts = tempTimeList.get(fileName).size();
                    }
                }
                //移除
                if (tempTimeList.get(fileName).size() != 0) {
                    tempTimeList.get(fileName).remove(0);
                    tempVoltageList.get(fileName).remove(0);
                }
            } else if (leftRadioButton.isSelected() && timeBoundary != -1) {//左截取
                int tempTimeCounts = tempTimeList.get(fileName).size();
                for (int j = 0; j < tempTimeCounts; j++) {
                    double x = (Double) tempTimeList.get(fileName).get(j);
                    if (x > timeBoundary) {
                        tempTimeList.get(fileName).remove(j);
                        tempVoltageList.get(fileName).remove(j);
                        j = 0;
                        tempTimeCounts = tempTimeList.get(fileName).size();
                    }
                }
            } else if (deleteRadioButton.isSelected()) {//当选择删除点
                int choice = JOptionPane.showConfirmDialog(this, "如果文件存在截取时间界限，是否对文件进行截取？默认截取右侧数据!", "截取选择", JOptionPane.OK_OPTION);
                if (choice == 0) {
                    rightRadioButton.setSelected(true);
                    saveButtonActionPerformed(evt);
                    return;
                } else {
                    return;
                }
            }
        }
//        //更新图表
        if (rightRadioButton.isSelected() || leftRadioButton.isSelected()) {
            dispose();
            tEMShowingParaSetDialog.editData();
        }
        //*********改变点处理***************************************************************
//        for (int i = 0; i < counts; i++) {
//            String fileName = filesNameList.get(i);
//            ArrayList posChangeList = fileName_ChangedPoints.get(fileName);
//            int count = posChangeList.size();
//            for (int j = 0; j < count; j++) {
//                double x = (Double) ((ArrayList) posChangeList.get(j)).get(0);
//                double y = (Double) ((ArrayList) posChangeList.get(j)).get(1);
//                int index = 0;
//                for (int m = 0; m < count; m++) {
//                    if ((Double) ((ArrayList) posChangeList.get(j)).get(0) == x) {
//                        index = m;
//                        //添加数值
//                        System.out.println(tempTimeList.get(fileName).get(index));
//                        System.out.println(tempVoltageList.get(fileName).get(index));
//                        tempTimeList.get(fileName).remove(index);
//                        tempVoltageList.get(fileName).remove(index);
//
//                        tempTimeList.get(fileName).add(x);
//                        tempVoltageList.get(fileName).add(y);
//                        System.out.println(x);
//                        System.out.println(y);
//                    }
//                }
//            }
//        }

//        dispose();//退出
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        //更改数据点数值
        setChangeDataToOrigin();
        dispose();//退出
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void fiveSmoothButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fiveSmoothButtonActionPerformed
        // TODO add your handling code here:
        fiveSmooth();
    }//GEN-LAST:event_fiveSmoothButtonActionPerformed
    public void fiveSmooth() {
        if (allFilesUnitCheckBox1.isSelected()) {//全部文件
            if (amp_phaseRadioButton.isSelected()) {
                Iterator i = tempVoltageList.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String fileName = entry.getKey().toString();
                    if (fileName.equalsIgnoreCase(getTitle())) {
                        fiveSmooth((ChartPanel) voltage_timeChartPanel.getComponent(0),
                                new XYSeries("时间/电压"),
                                tempAmpSeries,
                                getTitle());
                    } else {
                        ArrayList value = (ArrayList) entry.getValue();
                        ArrayList time = tempTimeList.get(fileName);
                        XYSeries timeVolt = new XYSeries("时间/电压");
                        for (int j = 0; j < value.size(); j++) {
                            timeVolt.add((Double) time.get(j), (Double) value.get(j));
                        }
                        fiveSmooth(timeVolt, tempAmpSeries, (String) fileName);
                    }
                }
            }
        } else {//单个文件
            if (amp_phaseRadioButton.isSelected()) {
                fiveSmooth((ChartPanel) voltage_timeChartPanel.getComponent(0),
                        new XYSeries("时间/电压"),
                        tempAmpSeries,
                        getTitle());
            }
        }
    }

    public void fiveSmooth(ChartPanel panel, XYSeries series, HashMap<String, ArrayList<XYSeries>> tempSeriesList, String fileName) {
        //临时保存
        ChartPanel chartPanel = panel;
        JFreeChart chart = chartPanel.getChart();
        XYPlot xYPlot = chart.getXYPlot();
        XYSeries xys = ((XYSeriesCollection) xYPlot.getDataset(0)).getSeries(0);
        for (int m = 0; m < xys.getItemCount(); m++) {
            series.add(xys.getX(m), xys.getY(m));
        }
        if (tempSeriesList.containsKey(fileName)) {
            tempSeriesList.get(fileName).add(series);//添加
        } else {
            ArrayList<XYSeries> al = new ArrayList<XYSeries>();
            al.add(series);
            tempSeriesList.put(fileName, al);//添加
        }
        //圆滑处理
        chart = chartPanel.getChart();
        xYPlot = chart.getXYPlot();
        //圆滑
        //更新
        Smooth(((XYSeriesCollection) xYPlot.getDataset(0)).getSeries(0));
        updateTimeVoltList(((XYSeriesCollection) xYPlot.getDataset(0)).getSeries(0), fileName);
    }

    public void updateTimeVoltList(XYSeries series, String fileName) {
        ArrayList<Double> volt = new ArrayList<Double>();
        ArrayList<Double> time = new ArrayList<Double>();
        for (int i = 0; i < series.getItemCount(); i++) {
            time.add(series.getX(i).doubleValue());
            volt.add(series.getY(i).doubleValue());
        }
        tempTimeList.put(fileName, time);
        tempVoltageList.put(fileName, volt);
        //更新TEMSourceData.integrationValue
        TEMSourceData.integrationValue.get(fileName).add(0, volt);
        TEMSourceData.integrationValue.get(fileName).remove(1);
        TEMSourceData.integrationValue.get(fileName).add(1, time);
        TEMSourceData.integrationValue.get(fileName).remove(2);
    }

    public void fiveSmooth(XYSeries series, HashMap<String, ArrayList<XYSeries>> tempSeriesList, String fileName) {
        //添加当前数据点
        XYSeries xys = new XYSeries(series.getKey());
        for (int m = 0; m < series.getItemCount(); m++) {
            xys.add(series.getX(m), series.getY(m));
        }
        if (tempSeriesList.containsKey(fileName)) {//判断是否包含
            tempSeriesList.get(fileName).add(xys);//添加
        } else {
            ArrayList<XYSeries> al = new ArrayList<XYSeries>();
            al.add(xys);
            tempSeriesList.put(fileName, al);//添加
        }
        //圆滑处理
        //更新
        Smooth(series);
        updateTimeVoltList(series, fileName);
    }

    public void Smooth(XYSeries series) {
        double[][] tv = series.toArray();
        double[] out = linearSmooth3(tv[1], tv[1].length);
        for (int i = 0; i < out.length; i++) {
            series.updateByIndex(i, out[i]);
        }
    }

    double[] linearSmooth5(double in[], int N) {
        int i;
        double out[] = new double[in.length];
        if (N < 5) {
            for (i = 0; i <= N - 1; i++) {
                out[i] = in[i];
            }
        } else {
            out[0] = (3.0 * in[0] + 2.0 * in[1] + in[2] - in[4]) / 5.0;
            out[1] = (4.0 * in[0] + 3.0 * in[1] + 2 * in[2] + in[3]) / 10.0;
            for (i = 2; i <= N - 3; i++) {
                out[i] = (in[i - 2] + in[i - 1] + in[i] + in[i + 1] + in[i + 2]) / 5.0;
            }
            out[N - 2] = (4.0 * in[N - 1] + 3.0 * in[N - 2] + 2 * in[N - 3] + in[N - 4]) / 10.0;
            out[N - 1] = (3.0 * in[N - 1] + 2.0 * in[N - 2] + in[N - 3] - in[N - 5]) / 5.0;
        }
        return out;
    }

    double[] linearSmooth7(double in[], int N) {
        int i;
        double out[] = new double[in.length];
        if (N < 7) {
            for (i = 0; i <= N - 1; i++) {
                out[i] = in[i];
            }
        } else {
            out[0] = (13.0 * in[0] + 10.0 * in[1] + 7.0 * in[2] + 4.0 * in[3]
                    + in[4] - 2.0 * in[5] - 5.0 * in[6]) / 28.0;

            out[1] = (5.0 * in[0] + 4.0 * in[1] + 3 * in[2] + 2 * in[3]
                    + in[4] - in[6]) / 14.0;

            out[2] = (7.0 * in[0] + 6.0 * in[1] + 5.0 * in[2] + 4.0 * in[3]
                    + 3.0 * in[4] + 2.0 * in[5] + in[6]) / 28.0;

            for (i = 3; i <= N - 4; i++) {
                out[i] = (in[i - 3] + in[i - 2] + in[i - 1] + in[i] + in[i + 1] + in[i + 2] + in[i + 3]) / 7.0;
            }

            out[N - 3] = (7.0 * in[N - 1] + 6.0 * in[N - 2] + 5.0 * in[N - 3]
                    + 4.0 * in[N - 4] + 3.0 * in[N - 5] + 2.0 * in[N - 6] + in[N - 7]) / 28.0;

            out[N - 2] = (5.0 * in[N - 1] + 4.0 * in[N - 2] + 3.0 * in[N - 3]
                    + 2.0 * in[N - 4] + in[N - 5] - in[N - 7]) / 14.0;

            out[N - 1] = (13.0 * in[N - 1] + 10.0 * in[N - 2] + 7.0 * in[N - 3]
                    + 4 * in[N - 4] + in[N - 5] - 2 * in[N - 6] - 5 * in[N - 7]) / 28.0;
        }
        return out;
    }

    public double[] linearSmooth3(double in[], int N) {
        int i;
        double out[] = new double[in.length];;
        if (N < 3) {
            for (i = 0; i <= N - 1; i++) {
                out[i] = in[i];
            }
        } else {

            out[0] = (5.0 * in[0] + 2.0 * in[1] - in[2]) / 6.0;
            if (out[0] <= 0) {
                out[0] = in[0];
            }
            for (i = 1; i <= N - 2; i++) {
                out[i] = (in[i - 1] + in[i] + in[i + 1]) / 3.0;
                if (out[i] <= 0) {
                    out[i] = out[i - 1] * 0.9;
                }
            }
            out[N - 1] = (5.0 * in[N - 1] + 2.0 * in[N - 2] - in[N - 3]) / 6.0;
            if (out[N - 1] <= 0) {
                out[N - 1] = out[N - 2] * 0.9;
            }
        }
        return out;
    }

    public XYSeries fiveSpotSmooth(XYSeries series) {
        double x[] = new double[4];
        double y[] = new double[4];
        double[] maxDepth_dds = new double[5];//三项式子中的系数 第四个是插值结果
        if (series.getItemCount() >= 5) {
            for (int i = 0; i < series.getItemCount(); i++) {
                if (i == 0) {
                    double x0 = series.getX(i).doubleValue();
                    double y0 = series.getY(i).doubleValue();
                    y[3] = series.getY(i + 2).doubleValue();
                    y[2] = series.getY(i + 1).doubleValue();
                    y[1] = y[3] - 3 * y[2] + 3 * y0;
                    y[0] = y[2] - 3 * y0 + 3 * y[1];
                    x[3] = series.getX(i + 2).doubleValue();
                    x[2] = series.getX(i + 1).doubleValue();
                    x[1] = x[3] - 3 * x[2] + 3 * x0;
                    x[0] = x[2] - 3 * x0 + 3 * x[1];
//                    x[1] = x[2] / 2;
//                    x[0] = x[1] / 2;
                    double interplotation = Interpolation.getValueAkima(x.length, x, y, x0, maxDepth_dds, -1);
                    if (String.valueOf(interplotation).equals("NaN") || interplotation <= 0) {
                        series.updateByIndex(i, (series.getY(i + 1).doubleValue()));
                    } else {
                        series.updateByIndex(i, interplotation);
                    }
//                    System.out.println(i + "," + x[0] + "," + x[1] + "," + x[2] + "," + x[3]);
//                    System.out.println(i + "," + y[0] + "," + y[1] + "," + y[2] + "," + y[3] + "------" + interplotation);
                } else if (i == 1) {
                    double x0 = series.getX(i - 1).doubleValue();
                    double y0 = series.getY(i - 1).doubleValue();
                    double x1 = series.getX(i).doubleValue();
                    double y1 = series.getY(i).doubleValue();
                    y[3] = series.getY(i + 2).doubleValue();
                    y[2] = series.getY(i + 1).doubleValue();
                    y[1] = y0;
                    y[0] = y[2] - 3 * y1 + 3 * y0;
                    x[3] = series.getX(i + 2).doubleValue();
                    x[2] = series.getX(i + 1).doubleValue();
                    x[1] = x0;
                    x[0] = x[2] - 3 * x1 + 3 * x0;
//                    x[0] = x[1] / 2;
                    double interplotation = Interpolation.getValueAkima(x.length, x, y, x1, maxDepth_dds, -1);
//                    System.out.println(i + "," + x[0] + "," + x[1] + "," + x[2] + "," + x[3]);
//                    System.out.println(i + "," + y[0] + "," + y[1] + "," + y[2] + "," + y[3] + "+++++"+ interplotation);
                    if (String.valueOf(interplotation).equals("NaN") || interplotation <= 0) {
                        series.updateByIndex(i, (series.getY(i + 1).doubleValue() / 2 + series.getY(i - 1).doubleValue() / 2));
                    } else {
                        series.updateByIndex(i, interplotation);
                    }
                } else if (i == series.getItemCount() - 2) {
                    double x0 = series.getX(i + 1).doubleValue();
                    double y0 = series.getY(i + 1).doubleValue();
                    double x1 = series.getX(i).doubleValue();
                    double y1 = series.getY(i).doubleValue();
                    y[0] = series.getY(i - 2).doubleValue();
                    y[1] = series.getY(i - 1).doubleValue();
                    y[2] = y0;
                    y[3] = 3 * y0 - 3 * series.getY(i).doubleValue() + series.getY(i - 1).doubleValue();

                    x[0] = series.getX(i - 2).doubleValue();
                    x[1] = series.getX(i - 1).doubleValue();
                    x[2] = x0;
                    x[3] = 3 * x0 - 3 * x1 + x[1];
                    double interplotation = Interpolation.getValueAkima(x.length, x, y, x1, maxDepth_dds, -1);
                    if (String.valueOf(interplotation).equals("NaN") || interplotation <= 0) {
//                        System.out.println(i + "NaN");
                        series.updateByIndex(i, (series.getY(i + 1).doubleValue() / 2 + series.getY(i - 1).doubleValue() / 2));
                    } else {
                        series.updateByIndex(i, interplotation);
                    }
                } else if (i == series.getItemCount() - 1) {
                    double x0 = series.getX(i).doubleValue();
                    double y0 = series.getY(i).doubleValue();
                    y[0] = series.getY(i - 2).doubleValue();
                    y[1] = series.getY(i - 1).doubleValue();
                    y[2] = 3 * y0 - 3 * y[1] + y[0];
                    y[3] = 3 * y[2] - 3 * y0 + y[1];

                    x[0] = series.getX(i - 2).doubleValue();
                    x[1] = series.getX(i - 1).doubleValue();
                    x[2] = 3 * x0 - 3 * x[1] + x[0];
                    x[3] = 3 * x[2] - 3 * x0 + x[1];
                    double interplotation = Interpolation.getValueAkima(x.length, x, y, x0, maxDepth_dds, -1);
                    if (String.valueOf(interplotation).equals("NaN") || interplotation <= 0) {
//                        System.out.println(i + "NaN");
                        series.updateByIndex(i, series.getY(i - 1));
                    } else {
                        series.updateByIndex(i, interplotation);
                    }
                } else {
                    double x0 = series.getX(i).doubleValue();
                    y[3] = series.getY(i + 2).doubleValue();
                    y[2] = series.getY(i + 1).doubleValue();
                    y[1] = series.getY(i - 1).doubleValue();
                    y[0] = series.getY(i - 2).doubleValue();

                    x[3] = series.getX(i + 2).doubleValue();
                    x[2] = series.getX(i + 1).doubleValue();
                    x[1] = series.getX(i - 1).doubleValue();
                    x[0] = series.getX(i - 2).doubleValue();
                    double interplotation = Interpolation.getValueAkima(x.length, x, y, x0, maxDepth_dds, -1);
                    if (String.valueOf(interplotation).equals("NaN") || interplotation <= 0) {
//                        System.out.println(i + "NaN");
                        series.updateByIndex(i, (series.getY(i + 1).doubleValue() / 2 + series.getY(i - 1).doubleValue() / 2));
                    } else {
                        series.updateByIndex(i, interplotation);
                    }
                }
            }
//            System.out.println(series.getItems());
        }
        return series;
    }
    private void backFiveSmoothButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backFiveSmoothButtonActionPerformed
        // TODO add your handling code here:
        if (allFilesUnitCheckBox1.isSelected()) {//全部文件
            if (amp_phaseRadioButton.isSelected()) {
                Iterator i = tempVoltageList.entrySet().iterator();//这个是不修改的
                while (i.hasNext()) {
                    Entry entry = (Entry) i.next();
                    String fileName = entry.getKey().toString();
                    if (tempAmpSeries.get(fileName) != null && tempAmpSeries.get(fileName).size() != 0) {
                        ArrayList<XYSeries> al = tempAmpSeries.get(fileName);
                        XYSeries series = al.get(al.size() - 1);
                        updateTimeVoltList(series, fileName);
//                        entry.setValue(series);
                        if (getTitle().equalsIgnoreCase(fileName)) {
                            backFiveSmooth((ChartPanel) voltage_timeChartPanel.getComponent(0), series, getTitle());
                        }
                        al.remove(al.size() - 1);
                    }
                }
            }
        } else {//单个文件
            if (amp_phaseRadioButton.isSelected()) {
                if (tempAmpSeries.get(getTitle()) != null && tempAmpSeries.get(getTitle()).size() != 0) {
                    ArrayList<XYSeries> al = tempAmpSeries.get(getTitle());
                    XYSeries series = al.get(al.size() - 1);
                    updateTimeVoltList(series, getTitle());
                    backFiveSmooth((ChartPanel) voltage_timeChartPanel.getComponent(0), series, getTitle());
                    al.remove(al.size() - 1);
                }
            }
        }
    }//GEN-LAST:event_backFiveSmoothButtonActionPerformed
    public void backFiveSmooth(ChartPanel panel, XYSeries series, String fileName) {
        //临时保存
        ChartPanel chartPanel = panel;
        JFreeChart chart = chartPanel.getChart();
        final XYPlot xYPlot = chart.getXYPlot();
        ((XYSeriesCollection) xYPlot.getDataset(0)).removeAllSeries();
        ((XYSeriesCollection) xYPlot.getDataset(0)).addSeries(series);
    }
    private void allFilesUnitCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allFilesUnitCheckBox1ActionPerformed
        // TODO add your handling code here:
        //        if (allFilesUnitCheckBox.isSelected()) {//选中时
        //            if (fileName_TimeBoundary.get(currentFileName).doubleValue() == -1D) {//当前文件没有选择时间界限时
        //                int count = JOptionPane.showConfirmDialog(this, "当前文件没有选择截取的时间界限，是否选择！", "选择时间界限", JOptionPane.OK_OPTION);
        //                if (count == 0) {
        //                    allFilesUnitCheckBox.setSelected(true);
        //                } else {
        //                    allFilesUnitCheckBox.setSelected(false);
        //                    return;
        //                }
        //            } else {//当当前文件存在时间界限时
        //                double x = fileName_TimeBoundary.get(currentFileName).doubleValue();//获得当前分界时间
        //                if (x != -1D) {
        //                    //为每个文件设置时间界限
        //                    int counts = fileName_TimeBoundary.size();
        //                    for (int i = 0; i < counts; i++) {
        //                        fileName_TimeBoundary.put(filesNameList.get(i), x);
        //                    }
        //                }
        //            }
        //        } else {//取消时
        //            int counts = fileName_TimeBoundary.size();
        //            for (int i = 0; i < counts; i++) {
        //                fileName_TimeBoundary.put(filesNameList.get(i), -1D);
        //            }
        //            //清理当前显示
        //            clearAnnotation_Markers();
        //        }
    }//GEN-LAST:event_allFilesUnitCheckBox1ActionPerformed
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
        updateVolTim(voltage, timeMid);
        int counts = voltage.size();
        XYSeries volt_time = new XYSeries("时间/电压");
        for (int i = 0; i < counts; i++) {
            double timeM = (Double) timeMid.get(i);
            double vol = (Double) voltage.get(i);
            volt_time.add(timeM, vol);
        }
        return volt_time;
    }
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox allFilesUnitCheckBox;
    public javax.swing.JCheckBox allFilesUnitCheckBox1;
    private javax.swing.JRadioButton amp_phaseRadioButton;
    private javax.swing.JButton backFiveSmoothButton;
    private javax.swing.JButton cancelButton;
    public javax.swing.JLabel dataPointsLabel;
    public static javax.swing.JRadioButton deleteRadioButton;
    public javax.swing.JLabel fileNameLabel;
    public javax.swing.JLabel filesAccountLabel;
    private javax.swing.JButton fiveSmoothButton;
    private javax.swing.JPanel fiveSmoothPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton lastPointButton;
    public static javax.swing.JRadioButton leftRadioButton;
    private javax.swing.ButtonGroup left_rightCutButtonGroup;
    private javax.swing.JButton nextPointButton;
    private javax.swing.JButton restoreAllFilesButton;
    private javax.swing.JButton restoreCurrentAllPointButton;
    private javax.swing.JButton restorePrePointButton;
    public static javax.swing.JRadioButton rightRadioButton;
    private javax.swing.JButton saveButton;
    public javax.swing.JPanel voltage_timeChartPanel;
    // End of variables declaration//GEN-END:variables
}
