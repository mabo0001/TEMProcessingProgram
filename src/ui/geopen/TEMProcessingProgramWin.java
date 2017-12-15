/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import handler.geopen.TEMData;
import handler.geopen.TEMGeoPenFileFilter;
import handler.geopen.TEMImageFileFilter;
import handler.geopen.TEMSetTableRowColor;
import handler.geopen.TEMSourceData;
import handler.geopen.TEMUSFFileFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import positionChart.geopen.InstallData;
import positionChart.geopen.OriginalDataChart;

/**
 * @author Administrator 2014.4.11********
 */
public class TEMProcessingProgramWin extends JFrame {
    //默认节点 如需增加测点测线 需添加到对应的节点中

    public DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeData(DBObjectType.ROOT, "TEM数据导航"));
    public DefaultMutableTreeNode pointNode = new DefaultMutableTreeNode(new NodeData(DBObjectType.FOLDERP, "测点"));
    public DefaultMutableTreeNode lineNode = new DefaultMutableTreeNode(new NodeData(DBObjectType.FOLDERL, "自定义测线"));
    public static String fileName = null;//获得单个文件名
    public static String lineName = null;//获得单个测线名
    //数据处理类
    public TEMData temData;
    //表面板参数
    public TEMChartPanle TEMChart_Panel = null;
    public static int flagPointOrLine = 0;//true代表点选 false线选 0代表什么都不选 1代表线选 2代表点选
    //参数设置对话框
    public TEMShowingParaSetDialog paraDialog;

    /**
     * Creates new form TEMProcessingProgramWin
     */
    public TEMProcessingProgramWin() {

        initComponents();
        dataVisualTabbedPane.remove(1);
        paraDialog = new TEMShowingParaSetDialog(this, true);
        //进度条不可见
        waitingLineProgressBar.setVisible(false);
        originalScrollPane.getVerticalScrollBar().setUnitIncrement(50);
        //坐标显示
//        TEMSourceData.Array = new Object[1];
//        TEMSourceData.Array[0] = "中心回线";
//        TEMChart_Panel = new TEMChartPanle(this, TEMSourceData.Array[0].toString());
        //定义几个初始节点默认
        root.add(pointNode);
        root.add(lineNode);
        SelectedDataTree.setModel(new DefaultTreeModel(root));
        SelectedDataTree.setCellRenderer(new MyTreeRender());
        SelectedDataTree.setShowsRootHandles(true);//设定折叠显示
        SelectedDataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        //增加鼠标监听
        MouseListener mouseListner = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
                if (e.getClickCount() == 2) {
                    try {// 点击不到节点会有异常弹出 捕获
                        if (!nodeParent.getParent().toString().equalsIgnoreCase("TEM数据导航") && e.getButton() == MouseEvent.BUTTON1) {
                            TreePath treePath = SelectedDataTree.getPathForLocation(e.getX(), e.getY());
                            Object[] paths = treePath.getParentPath().getPath();//Jtree的格式[根节点名,节点名]
                            if (treePath.getPathCount() == 3 && paths[1].toString().trim().equalsIgnoreCase("测点")) {//测点节点
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
                                fileName = node.getPath()[2].toString().trim().toUpperCase();//获得文件名
                                //判定fileName的index
                                for (int i = 0; i < TEMSourceData.filesName.length; i++) {
                                    if (fileName.equalsIgnoreCase(TEMSourceData.filesName[i])) {
                                        TEMTime_Resis_VoltWin.recIndexFileNames = i;
                                        break;
                                    }
                                }
                                //显示单个数据点信息
                                showingSingleData(fileName);
                            } else if (treePath.getPathCount() == 3 && paths[1].toString().trim().equalsIgnoreCase("自定义测线")) {
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
                                lineName = node.getPath()[2].toString().trim();//获得自定义测线名
                                showingLineDatas(lineName);
                            }
                        }
                    } catch (Exception ee) {
                    }
                } else if (e.getClickCount() == 1) {//标记测点和测线位置
                    try {// 点击不到节点会有异常弹出 捕获
                        //显示文件名
                        TreePath treePath = SelectedDataTree.getPathForLocation(e.getX(), e.getY());
                        Object[] pathsAll = treePath.getPath();//Jtree的格式[根节点名,节点名]
//                        Object[] paths = treePath.getParentPath().getPath();//Jtree的格式[根节点名,节点名]
//                        System.out.println(paths.length);
//                        System.out.println(pathsAll[1].toString().trim());
                        if (pathsAll[1].toString().trim().equalsIgnoreCase("测点") && pathsAll.length == 2) {
                            //删除
                            if (e.getButton() == MouseEvent.BUTTON3) {
                                saveUSFMenuItem.setVisible(true);
                                deleteNodeMenuItem.setVisible(false);
                                deleteNodePopupMenu.show(SelectedDataTree, e.getX(), e.getY());
                            }
                        }
                        if (pathsAll.length == 3 && pathsAll[1].toString().trim().equalsIgnoreCase("测点")) {//测点节点
                            clearSeries();//清除标记的点
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
                            fileName = node.getPath()[2].toString().trim();//获得文件名
                            int index = 0;
                            for (int i = 0; i < TEMSourceData.filesName.length; i++) {
                                if (TEMSourceData.filesName[i].equalsIgnoreCase(fileName)) {
                                    index = i;
                                    break;
                                }
                            }
                            double x = Double.parseDouble(TEMSourceData.rCenterX[index].toString());
                            double y = Double.parseDouble(TEMSourceData.rCenterY[index].toString());
                            TEMChartPanle.series4.add(x, y);
                            setFrameLableText(x, y);//设定label显示
//                            //删除
                            if (e.getButton() == MouseEvent.BUTTON3) {
                                saveUSFMenuItem.setVisible(false);
                                deleteNodeMenuItem.setVisible(true);
                                deleteNodePopupMenu.show(SelectedDataTree, e.getX(), e.getY());
                            }
                        } else if (pathsAll.length == 3 && pathsAll[1].toString().trim().equalsIgnoreCase("自定义测线")) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
                            lineName = node.getPath()[2].toString().trim();//获得文件名
                            fileNameLabel.setText("当前自定义测线名：" + lineName);
                            //删除
                            if (e.getButton() == MouseEvent.BUTTON3) {
                                saveUSFMenuItem.setVisible(true);
                                deleteNodeMenuItem.setVisible(true);
                                deleteNodePopupMenu.show(SelectedDataTree, e.getX(), e.getY());
                            }
                            //突出显示
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                if (TEMSourceData.lineName_XYList.size() != 0) {
                                    clearMarksSeries();//清除标记线的点
                                    ArrayList points = TEMSourceData.lineName_XYList.get(lineName);
                                    int counts = points.size();
                                    for (int i = 0; i < counts; i++) {
                                        ArrayList pos = (ArrayList) points.get(i);
                                        double x = Double.parseDouble(pos.get(0).toString());
                                        double y = Double.parseDouble(pos.get(1).toString());
                                        TEMChartPanle.series5.add(x, y);
                                    }
                                }
                            }
                        }
                    } catch (Exception ee) {
                    }
                }
            }
        };
        SelectedDataTree.addMouseListener(mouseListner);
        //参数设置表格数据头部分的鼠标监听
        //增加表格标题鼠标监听
        paraDialog.addHeadTableMouseListner(TEMShowingParaSetDialog.pointsParasTable, paraDialog);
    }

    public void setFrameLableText(double x, double y) {
        ArrayList<Double> xy = new ArrayList<Double>();
        xy.add(x);
        xy.add(y);
        String fileName = TEMSourceData.xy_fileName.get(xy);//fileName 必须为frame.fileName 因为在单点反演中需要
        if (fileName != null) {
            fileNameLabel.setText("当前文件名：" + fileName);//显示数据点数据
        } else {
            fileNameLabel.setText("当前文件名：" + this.fileName);//显示数据点数据
        }
        posLabel.setText("当前数据点坐标：" + "( x = " + x + " , y = " + y + " )");//显示数据点数据
    }

    public void clearSeries() {
        TEMChartPanle.series2.clear();
        TEMChartPanle.series3.clear();
        TEMChartPanle.series4.clear();
    }

    public void clearMarksSeries() {
        TEMChartPanle.series5.clear();
    }

    /**
     * 显示测线数据
     *
     * @param lineName
     */
    public void showingLineDatas(final String lineName) {
        if (TEMSourceData.integrationValue.size() != 0) {
            waitingLineProgressBar.setVisible(true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TEMSourceData.integrationValue.size() != 0) {//只有存在积分数据 才能弹出
                    try {
                        final TEMLine_Time_Resis_VolWin lineWin = new TEMLine_Time_Resis_VolWin(TEMProcessingProgramWin.this);//创建视窗
                        HashSet<Double> allTimePoints = new HashSet();//所有时间点 用于抽道时间确定
                        ArrayList linePoints = new ArrayList();//必须是Arraylist
                        linePoints = (ArrayList) TEMSourceData.lineName_XYList.get(lineName);
                        String fileName = "";
                        int maxTimePoints = -1;
                        for (int i = 0; i < linePoints.size(); i++) {
                            fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
                            ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
                            if (i == 0) {//以积分完的电压值点数为准
                                maxTimePoints = ((ArrayList) voltage_time.get(1)).size();
                            }
//                            else if (maxTimePoints < ((ArrayList) voltage_time.get(1)).size()) {
//                                maxTimePoints = ((ArrayList) voltage_time.get(1)).size();
//                            }
                            //添加时间点数据
//                            for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {
                            for (int j = 0; j < ((ArrayList) voltage_time.get(1)).size(); j++) {
                                allTimePoints.add((Double) ((ArrayList) voltage_time.get(1)).get(j));
                            }
                        }
                        for (int i = 0; i < linePoints.size(); i++) {
                            fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
                            ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
                            XYSeriesCollection xyseriescollection = new XYSeriesCollection();
                            TEMTime_Resis_VoltWin time_resis_rolt = new TEMTime_Resis_VoltWin(xyseriescollection);
                            //添加电阻率时间 默认显示
                            xyseriescollection.addSeries(time_resis_rolt.extractResis_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
                                    (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5), (ArrayList) voltage_time.get(6)));
                            lineWin.definedLinePanel.add(time_resis_rolt.createDemoPanel(fileName, "Time( ms )", "Resistivity( ohm.m )", "RT", xyseriescollection, true));
                        }
                        //添加时间电压抽道曲线
                        DecimalFormat foramt = new DecimalFormat("0.000");
                        Iterator iterTime = allTimePoints.iterator();
                        ArrayList<Double> timeList = new ArrayList<Double>();
                        while (iterTime.hasNext()) {//同一时间下的电压值抽取
                            double time = (Double) (iterTime.next());
                            timeList.add(time);
                        }
                        Collections.sort(timeList);//排序
                        XYSeries[] timesSeries = new XYSeries[maxTimePoints];//添加时间曲线的条数 应该和测线中 时间点最多的数据个数相同
                        InstallData[] timeOptions = new InstallData[maxTimePoints];//用于存储时间jcheckbox
                        for (int m = 0; m < timeList.size(); m++) {
                            double time = timeList.get(m);
                            XYSeries singleSeries = new XYSeries(foramt.format(time));
                            for (int i = 0; i < linePoints.size(); i++) {
                                fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
                                ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
                                for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {//应根据积分的电压值的个数 来定义总个数F
                                    double singleTime = (Double) ((ArrayList) voltage_time.get(1)).get(j);
                                    if (singleTime == time) {
                                        double value = (Double) ((ArrayList) voltage_time.get(0)).get(j);
                                        if (value <= 0) {
                                            singleSeries.add(i + 1, null);
                                        } else {
                                            singleSeries.add(i + 1, value);
                                        }
                                        break;
                                    }
                                }
                            }
                            timesSeries[m] = singleSeries;
//                            System.out.println(m + "," + timesSeries[m].getItemCount());
                            //定义时间格式
                            timeOptions[m] = new InstallData(foramt.format(time));
                            timeOptions[m].setSelected(true);//设定为选择
                        }
                        lineWin.time_volt_channelsPanel.add(lineWin.channels_Time_VoltChart.createDemoPanel(timesSeries));//抽到数据赋值给图表
                        lineWin.timeList.setListData(timeOptions);//为timeList添加项目
                        //可视化
                        lineWin.pack();
                        ShowScope.setScope(lineWin);
                        lineWin.setTitle("自定义测线" + lineName);
                        lineWin.setVisible(true);
                        //设定不可见
                        waitingLineProgressBar.setVisible(false);
                        //设定右边的边框不可显示
                        lineWin.channel_time_voltSplitPane.setDividerLocation(lineWin.channel_time_voltSplitPane.getWidth());
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "自定义测线无法生成,请重新选择或重新启动程序！");
                        waitingLineProgressBar.setVisible(false);
                        deleteNodeMenuItemActionPerformed(null);
                        return;
                    }
                } else {
                    int count = JOptionPane.showConfirmDialog(TEMProcessingProgramWin.this, "数据没有视窗积分，是否先进行积分处理？", "积分选择", JOptionPane.OK_OPTION);
                    if (count == 0) {
                        pointsParasButtonActionPerformed(null);
                    }
                    return;
                }
            }
        }).start();
//        if (TEMSourceData.integrationValue.size() != 0) {//只有存在积分数据 才能弹出
//            ArrayList linePoints = new ArrayList();//必须是Arraylist
//            linePoints = (ArrayList) TEMSourceData.lineName_XYList.get(lineName);
//            String fileName = "";
//            final TEMLine_Time_Resis_VolWin lineWin = new TEMLine_Time_Resis_VolWin(TEMProcessingProgramWin.this);//创建视窗
//            HashSet<Double> allTimePoints = new HashSet();//所有时间点 用于抽道时间确定
//            int maxTimePoints = -1;
//            for (int i = 0; i < linePoints.size(); i++) {
//                fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
//                ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
//                if (i == 0) {//以积分完的电压值点数为准
//                    maxTimePoints = ((ArrayList) voltage_time.get(0)).size();
//                } else if (maxTimePoints < ((ArrayList) voltage_time.get(1)).size()) {
//                    maxTimePoints = ((ArrayList) voltage_time.get(0)).size();
//                }
//                //添加时间点数据
//                for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {
//                    allTimePoints.add((Double) ((ArrayList) voltage_time.get(1)).get(j));
//                }
//            }
//            for (int i = 0; i < linePoints.size(); i++) {
//                fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
//                ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
//                XYSeriesCollection xyseriescollection = new XYSeriesCollection();
//                TEMTime_Resis_VoltWin time_resis_rolt = new TEMTime_Resis_VoltWin(xyseriescollection);
//                //添加电阻率时间 默认显示
//                xyseriescollection.addSeries(time_resis_rolt.extractResis_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1),
//                        (ArrayList) voltage_time.get(2), (ArrayList) voltage_time.get(3), (ArrayList) voltage_time.get(4), (ArrayList) voltage_time.get(5)));
//                lineWin.definedLinePanel.add(time_resis_rolt.createDemoPanel(fileName, "Time( ms )", "Resistivity( ohm.m )", "RT", xyseriescollection, true));
//            }
//            //添加时间抽道曲线
//            DecimalFormat foramt = new DecimalFormat("0.00000000");
//            Iterator iterTime = allTimePoints.iterator();
//            int counts = 0;
//            XYSeries[] timesSeries = new XYSeries[maxTimePoints];//添加时间曲线的条数 应该和测线中 时间点最多的数据个数相同
//            InstallData[] timeOptions = new InstallData[maxTimePoints];//用于存储时间jcheckbox
//            while (iterTime.hasNext()) {//同一时间下的电压值抽取
//                double time = (Double) (iterTime.next());
//                XYSeries singleSeries = new XYSeries(foramt.format(time));
//                for (int i = 0; i < linePoints.size(); i++) {
//                    fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
//                    ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
//                    for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {//应根据积分的电压值的个数 来定义总个数
//                        double singleTime = (Double) ((ArrayList) voltage_time.get(1)).get(j);
//                        if (singleTime == time) {
//                            singleSeries.add(i + 1, (Double) ((ArrayList) voltage_time.get(0)).get(j));
//                            break;
//                        }
//                    }
//                }
//                timesSeries[counts] = singleSeries;
//                //定义时间格式
//                timeOptions[counts] = new InstallData(foramt.format(time));
//                timeOptions[counts].setSelected(true);//设定为选择
//                counts++;
////                System.out.println(iterTime.next() + "====" + counts);
//            }
//            lineWin.time_volt_channelsPanel.add(lineWin.channels_Time_VoltChart.createDemoPanel(timesSeries));//抽到数据赋值给图表
//            lineWin.timeList.setListData(timeOptions);//为timeList添加项目
//            //可视化
//            lineWin.pack();
//            ShowScope.setScope(lineWin);
//            lineWin.setTitle("自定义测线" + lineName);
//            lineWin.setVisible(true);
//            //设定右边的边框不可显示
//            lineWin.channel_time_voltSplitPane.setDividerLocation(lineWin.channel_time_voltSplitPane.getWidth());
//        } else {
//            int count = JOptionPane.showConfirmDialog(TEMProcessingProgramWin.this, "数据没有视窗积分，是否先进行积分处理？", "积分选择", JOptionPane.OK_OPTION);
//            if (count == 0) {
//                pointsParasButtonActionPerformed(null);
//            }
//            return;
//        }
    }

    /**
     * 显示单个数据
     *
     * @param fileName
     */
    public void showingSingleData(String fileName) {
        if (TEMSourceData.integrationValue.size() != 0) {//只有存在积分数据 才能弹出
            TEMTime_Resis_VoltWin time_resis_rolt = new TEMTime_Resis_VoltWin(null);
            ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);
            time_resis_rolt.xyseriescollection0.addSeries(time_resis_rolt.extractVolt_Time((ArrayList) voltage_time.get(0), (ArrayList) voltage_time.get(1)));
            time_resis_rolt.resis_vol_timeSplitPane.getBottomComponent().setVisible(false);//没有反演之前不可见
            time_resis_rolt.resis_depthSplitPane.getRightComponent().setVisible(false);
            time_resis_rolt.resis_vol_timeSplitPane.setOneTouchExpandable(false);//设定不可divide
            time_resis_rolt.resis_depthSplitPane.setOneTouchExpandable(false);//设定不可divide
            time_resis_rolt.setTitle(fileName);
            time_resis_rolt.pack();
            ShowScope.setScope(time_resis_rolt);
            time_resis_rolt.setVisible(true);
        } else {
            int count = JOptionPane.showConfirmDialog(TEMProcessingProgramWin.this, "数据没有视窗积分，是否先进行积分处理？", "积分选择", JOptionPane.OK_OPTION);
            if (count == 0) {
                pointsParasButtonActionPerformed(null);
            }
            return;
        }
    }

    /**
     * 完全展开一棵树或关闭一棵树
     *
     * @param tree JTree
     * @param parent 父节点
     * @param expand true 表示展开，false 表示关闭
     */
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() > 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPositionPopUp = new javax.swing.JPopupMenu();
        indermindedProgressBarDia = new javax.swing.JDialog(this,true);
        waitingProgressBar = new javax.swing.JProgressBar();
        deleteNodePopupMenu = new javax.swing.JPopupMenu();
        deleteNodeMenuItem = new javax.swing.JMenuItem();
        saveUSFMenuItem = new javax.swing.JMenuItem();
        GroundOrAirDia = new javax.swing.JDialog();
        groundButton = new javax.swing.JButton();
        flyButton = new javax.swing.JButton();
        ToolBar = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        fileOpenButt = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        saveFileButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        saveImageButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        GPSButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        originalDataButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pointsParasButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        editButton = new javax.swing.JButton();
        waitingLineProgressBar = new javax.swing.JProgressBar();
        HandlerPanel = new javax.swing.JPanel();
        HandlerSplitPane1 = new javax.swing.JSplitPane();
        dataVisualTabbedPane = new javax.swing.JTabbedPane();
        pointsPositionPanel = new javax.swing.JPanel();
        originalScrollPane = new javax.swing.JScrollPane();
        originalDataPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        SelectedDataTree = new javax.swing.JTree();
        totalFilesLabel = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        posLabel = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        fileMenuItem = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        saveGPTMMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        savePicMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        pointsParasMenuItem = new javax.swing.JMenuItem();
        editMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        GPSMenuItem = new javax.swing.JMenuItem();
        originalDataMenuItem = new javax.swing.JMenuItem();
        AboutMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        indermindedProgressBarDia.setUndecorated(true);
        indermindedProgressBarDia.getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        waitingProgressBar.setIndeterminate(true);
        indermindedProgressBarDia.getContentPane().add(waitingProgressBar);

        deleteNodeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/delete16.png"))); // NOI18N
        deleteNodeMenuItem.setText("删除");
        deleteNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNodeMenuItemActionPerformed(evt);
            }
        });
        deleteNodePopupMenu.add(deleteNodeMenuItem);

        saveUSFMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/save16.png"))); // NOI18N
        saveUSFMenuItem.setText("保存为.usf");
        saveUSFMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveUSFMenuItemActionPerformed(evt);
            }
        });
        deleteNodePopupMenu.add(saveUSFMenuItem);

        GroundOrAirDia.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        GroundOrAirDia.setTitle("采集形式");
        GroundOrAirDia.setAlwaysOnTop(true);
        GroundOrAirDia.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("pic/geopen/GP.png")));
        GroundOrAirDia.setModal(true);

        groundButton.setText("地面采集");
        groundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groundButtonActionPerformed(evt);
            }
        });

        flyButton.setText("飞行采集");
        flyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout GroundOrAirDiaLayout = new javax.swing.GroupLayout(GroundOrAirDia.getContentPane());
        GroundOrAirDia.getContentPane().setLayout(GroundOrAirDiaLayout);
        GroundOrAirDiaLayout.setHorizontalGroup(
            GroundOrAirDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GroundOrAirDiaLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(groundButton)
                .addGap(18, 18, 18)
                .addComponent(flyButton)
                .addGap(5, 5, 5))
        );
        GroundOrAirDiaLayout.setVerticalGroup(
            GroundOrAirDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GroundOrAirDiaLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(GroundOrAirDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groundButton, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(flyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("GeoPen－TEM瞬变电磁处理软件");
        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("pic/geopen/GP.png")));
        setMinimumSize(new java.awt.Dimension(562, 301));
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

        ToolBar.setFloatable(false);
        ToolBar.setRollover(true);

        jLabel2.setText(" ");
        ToolBar.add(jLabel2);

        fileOpenButt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/openFile28.png"))); // NOI18N
        fileOpenButt.setToolTipText("打开");
        fileOpenButt.setFocusable(false);
        fileOpenButt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileOpenButt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fileOpenButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileOpenButtActionPerformed(evt);
            }
        });
        ToolBar.add(fileOpenButt);

        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("|");
        ToolBar.add(jLabel7);

        saveFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/savegptm28.png"))); // NOI18N
        saveFileButton.setToolTipText("保存");
        saveFileButton.setFocusable(false);
        saveFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveFileButton.setMaximumSize(new java.awt.Dimension(39, 39));
        saveFileButton.setMinimumSize(new java.awt.Dimension(39, 39));
        saveFileButton.setPreferredSize(new java.awt.Dimension(39, 39));
        saveFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileButtonActionPerformed(evt);
            }
        });
        ToolBar.add(saveFileButton);

        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("|");
        ToolBar.add(jLabel5);

        saveImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/pictrue28.png"))); // NOI18N
        saveImageButton.setToolTipText("图片");
        saveImageButton.setFocusable(false);
        saveImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageButtonActionPerformed(evt);
            }
        });
        ToolBar.add(saveImageButton);

        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("|");
        ToolBar.add(jLabel6);

        GPSButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/GPS28.png"))); // NOI18N
        GPSButton.setToolTipText("采样参数及GPS");
        GPSButton.setFocusable(false);
        GPSButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        GPSButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        GPSButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GPSButtonActionPerformed(evt);
            }
        });
        ToolBar.add(GPSButton);

        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("|");
        ToolBar.add(jLabel1);

        originalDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/original28.png"))); // NOI18N
        originalDataButton.setToolTipText("源数据显示");
        originalDataButton.setFocusable(false);
        originalDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        originalDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        originalDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originalDataButtonActionPerformed(evt);
            }
        });
        ToolBar.add(originalDataButton);

        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("|");
        ToolBar.add(jLabel3);

        pointsParasButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/table28.png"))); // NOI18N
        pointsParasButton.setToolTipText("参数设置");
        pointsParasButton.setFocusable(false);
        pointsParasButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pointsParasButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pointsParasButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointsParasButtonActionPerformed(evt);
            }
        });
        ToolBar.add(pointsParasButton);

        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("|");
        ToolBar.add(jLabel8);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/edit28.png"))); // NOI18N
        editButton.setToolTipText("编辑数据");
        editButton.setFocusable(false);
        editButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        ToolBar.add(editButton);

        waitingLineProgressBar.setIndeterminate(true);
        waitingLineProgressBar.setMaximumSize(new java.awt.Dimension(200, 19));
        waitingLineProgressBar.setMinimumSize(new java.awt.Dimension(200, 19));
        waitingLineProgressBar.setPreferredSize(new java.awt.Dimension(200, 19));
        waitingLineProgressBar.setString("自定义测线生成中...");
        waitingLineProgressBar.setStringPainted(true);
        ToolBar.add(waitingLineProgressBar);

        HandlerPanel.setLayout(new java.awt.GridLayout(1, 0));

        HandlerSplitPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        HandlerSplitPane1.setDividerLocation(210);
        HandlerSplitPane1.setDividerSize(6);
        HandlerSplitPane1.setOneTouchExpandable(true);

        dataVisualTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dataVisualTabbedPaneStateChanged(evt);
            }
        });

        pointsPositionPanel.setBackground(new java.awt.Color(255, 255, 255));
        pointsPositionPanel.setLayout(new java.awt.GridLayout(1, 0));
        dataVisualTabbedPane.addTab("TEM布置图", pointsPositionPanel);

        originalDataPanel.setBackground(new java.awt.Color(255, 255, 255));
        originalDataPanel.setLayout(new java.awt.GridLayout(0, 1));
        originalScrollPane.setViewportView(originalDataPanel);

        dataVisualTabbedPane.addTab("源数据显示", originalScrollPane);

        HandlerSplitPane1.setRightComponent(dataVisualTabbedPane);

        SelectedDataTree.setModel(null);
        jScrollPane1.setViewportView(SelectedDataTree);

        HandlerSplitPane1.setLeftComponent(jScrollPane1);

        HandlerPanel.add(HandlerSplitPane1);

        totalFilesLabel.setFont(new java.awt.Font("新宋体", 0, 12)); // NOI18N
        totalFilesLabel.setText("文件总数：");
        totalFilesLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        fileNameLabel.setFont(new java.awt.Font("新宋体", 0, 12)); // NOI18N
        fileNameLabel.setText("当前文件名：");
        fileNameLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        posLabel.setFont(new java.awt.Font("新宋体", 0, 12)); // NOI18N
        posLabel.setText("数据点坐标：");
        posLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_BOTTOM, new java.awt.Font("宋体", 0, 1))); // NOI18N

        fileMenuItem.setMnemonic(KeyEvent.VK_F);
        fileMenuItem.setText("文件(F)");

        openFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/openFile16.png"))); // NOI18N
        openFileMenuItem.setText("打开(O)");
        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileMenuItemActionPerformed(evt);
            }
        });
        fileMenuItem.add(openFileMenuItem);

        saveGPTMMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveGPTMMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/savegptm16.png"))); // NOI18N
        saveGPTMMenuItem.setText("保存(S)");
        saveGPTMMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGPTMMenuItemActionPerformed(evt);
            }
        });
        fileMenuItem.add(saveGPTMMenuItem);
        fileMenuItem.add(jSeparator1);

        savePicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        savePicMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/pictrue16.png"))); // NOI18N
        savePicMenuItem.setText("图片(P)");
        savePicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePicMenuItemActionPerformed(evt);
            }
        });
        fileMenuItem.add(savePicMenuItem);
        fileMenuItem.add(jSeparator2);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/exit.png"))); // NOI18N
        exitMenuItem.setText("退出(E)");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenuItem.add(exitMenuItem);

        MenuBar.add(fileMenuItem);

        EditMenu.setMnemonic(KeyEvent.VK_E);
        EditMenu.setText("编辑(E)");

        pointsParasMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        pointsParasMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/table16.png"))); // NOI18N
        pointsParasMenuItem.setText("参数设置(H)");
        pointsParasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointsParasMenuItemActionPerformed(evt);
            }
        });
        EditMenu.add(pointsParasMenuItem);

        editMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        editMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/edit16.png"))); // NOI18N
        editMenuItem.setText("数据编辑(D)");
        editMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuItemActionPerformed(evt);
            }
        });
        EditMenu.add(editMenuItem);

        MenuBar.add(EditMenu);

        jMenu2.setMnemonic(KeyEvent.VK_I);
        jMenu2.setText("信息(I)");

        GPSMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        GPSMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/GPS16.png"))); // NOI18N
        GPSMenuItem.setText("采集参数及GPS");
        GPSMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GPSMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(GPSMenuItem);

        originalDataMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        originalDataMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/original16.png"))); // NOI18N
        originalDataMenuItem.setText("源数据显示(O)");
        originalDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originalDataMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(originalDataMenuItem);

        MenuBar.add(jMenu2);

        AboutMenu.setMnemonic(KeyEvent.VK_A);
        AboutMenu.setText("关于(A)");

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/about16.png"))); // NOI18N
        aboutMenuItem.setText("软件");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        AboutMenu.add(aboutMenuItem);

        MenuBar.add(AboutMenu);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(HandlerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(totalFilesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(posLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(HandlerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalFilesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(posLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileOpenButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileOpenButtActionPerformed
        // TODO add your handling code here:
//        JOptionPane.showConfirmDialog(this, "弹出！！！");
        //选择 飞行模式还是地面模式
        GroundOrAirDia.pack();
        GroundOrAirDia.setVisible(true);
        //打开文件
        fileOpen();
    }//GEN-LAST:event_fileOpenButtActionPerformed
    public void updateTree() {
        if (temData.files != null) {//当选择了文件时
            //更新树列表
            if (pointNode.getChildCount() != 0) {//如果测点数不是零 移除之前的文件
                pointNode.removeAllChildren();
            }
            if (lineNode.getChildCount() != 0) {//如果自定义测线数不是零 移除之前的文件
                lineNode.removeAllChildren();
            }
            for (int i = 0; i < temData.files.length; i++) {
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(new NodeData(DBObjectType.PONIT, temData.files[i].getName()));
                pointNode.add(tempNode);
            }
            //设定文件数
            totalFilesLabel.setText("文件总数：" + pointNode.getChildCount());
        } else {
            return;
        }
        if (TEMSourceData.lineName.size() != 0) {
            for (int i = 0; i < TEMSourceData.lineName.size(); i++) {
                DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(new NodeData(DBObjectType.LINE, TEMSourceData.lineName.get(i).toString()));
                lineNode.add(tempNode);
            }
        }
        //展开树
        if (lineNode.getChildCount() != 0) {
            SelectedDataTree.expandRow(2);//自动展开测点节点自定义测线
        } else {
            SelectedDataTree.collapseRow(2);//自动展开测点节点自定义测线
        }
        if (pointNode.getChildCount() != 0) {
            SelectedDataTree.expandRow(1);//自动展开测点节点
        } else {
            SelectedDataTree.collapseRow(1);//自动展开测点节点
        }
        SelectedDataTree.updateUI();//更新
    }

    public void fileOpen() {
        temData = new TEMData(TEMProcessingProgramWin.this);//初始化读取tem数据类
        temData.openFileButtAction();//打开文件选择对话框 并获得文件 包含清楚文件之前打开的信息
        updateTree();
    }
    private void pointsParasButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointsParasButtonActionPerformed
        // TODO add your handling code here:
        initialTable();
        paraDialog.setVisible(true);
    }//GEN-LAST:event_pointsParasButtonActionPerformed
    public void initialTable() {
        if (pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
            clearTable(TEMShowingParaSetDialog.pointsParasTable);
            ShowScope.setLocation(paraDialog);
            //判定读取的文件类型
            if (temData.flagFileType == 0) {//tm
                paraDialog.chooseTimeWinCheckBox.setEnabled(true);
                paraDialog.chooseTimeWinCheckBox.setSelected(true);
            } else if (temData.flagFileType == 1) {//gptm
                if (TEMSourceData.integrationValue.size() == 0) {//代表没有积分
                    paraDialog.chooseTimeWinCheckBox.setEnabled(true);
                    paraDialog.chooseTimeWinCheckBox.setSelected(false);
                }
//                else if (TEMSourceData.integrationValue.size() != 0) {
//                    paraDialog.chooseTimeWinCheckBox.setEnabled(false);
//                    paraDialog.chooseTimeWinCheckBox.setSelected(false);
//                }
            }
            paraDialog.pack();
            //设定最后一列为jcheckbox
            int counts = pointNode.getChildCount();
            Object[] strs = new Object[20];
            DefaultTableModel tableModel = ((DefaultTableModel) paraDialog.pointsParasTable.getModel());
            for (int i = 0; i < counts; i++) {
                strs[0] = i + 1 + "";
                strs[1] = TEMSourceData.filesName[i];
                strs[2] = TEMSourceData.trLength[i];
                strs[3] = TEMSourceData.trWidth[i];
                strs[4] = TEMSourceData.trCenterX[i];
                strs[5] = TEMSourceData.trCenterY[i];
                strs[6] = TEMSourceData.trAngle[i];
                strs[7] = TEMSourceData.trTurns[i];
                strs[8] = TEMSourceData.rCenterX[i];
                strs[9] = TEMSourceData.rCenterY[i];
                strs[10] = TEMSourceData.rCenterZ[i];
                strs[11] = TEMSourceData.longtitude[i];
                strs[12] = TEMSourceData.latitude[i];
                strs[13] = TEMSourceData.rArea[i];
                strs[14] = TEMSourceData.Array[i];
                strs[15] = TEMSourceData.turnOffTime[i];
                strs[16] = TEMSourceData.current[i];
                strs[17] = TEMSourceData.firstChannel[i];
                strs[18] = TEMSourceData.secondChannel[i];
                strs[19] = TEMSourceData.thirdChannel[i];
                tableModel.addRow(strs);
            }
            setTableCenter(paraDialog.pointsParasTable);//设定表格居中
            int[] columnIndex = new int[]{paraDialog.columm6, paraDialog.columm15};
            fitTableColumns(paraDialog.pointsParasTable, columnIndex);//自动调节列宽
            TableColumnModel tcm = paraDialog.pointsParasTable.getColumnModel();
            tcm.getColumn(17).setCellRenderer(new CWCheckBoxRenderer());
            tcm.getColumn(17).setCellEditor(new CheckBoxCellEditor());
            tcm.getColumn(18).setCellRenderer(new CWCheckBoxRenderer());
            tcm.getColumn(18).setCellEditor(new CheckBoxCellEditor());
            tcm.getColumn(19).setCellRenderer(new CWCheckBoxRenderer());
            tcm.getColumn(19).setCellEditor(new CheckBoxCellEditor());
            //选定通道 更新
            for (int i = 0; i < counts; i++) {
                if (paraDialog.selectC[0] == false) {
                    TEMShowingParaSetDialog.pointsParasTable.setValueAt(false, i, 17);
                }
                if (paraDialog.selectC[1] == false) {
                    TEMShowingParaSetDialog.pointsParasTable.setValueAt(false, i, 18);
                }
                if (paraDialog.selectC[2] == false) {
                    TEMShowingParaSetDialog.pointsParasTable.setValueAt(false, i, 19);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "无测点数据文件，请先读取文件！");
            return;
        }
    }

    public void clearTable(JTable table) {
        for (int index = table.getModel().getRowCount() - 1; index >= 0; index--) {
            ((DefaultTableModel) table.getModel()).removeRow(index);
        }
    }

    public void setEnable() {
        paraDialog.pos_integrationButton.setEnabled(false);
    }
    private void GPSButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GPSButtonActionPerformed
        // TODO add your handling code here:
        if (pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
            setGPSInforTableValue();
        } else {
            JOptionPane.showMessageDialog(this, "无测点数据文件，请先读取文件！");
            return;
        }
    }//GEN-LAST:event_GPSButtonActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        if (TEMChart_Panel != null) {
            JFreeChart jfreechart = TEMChart_Panel.chartPanel.getChart();
            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
            if (evt.getKeyCode() == KeyEvent.VK_C) {
                //清楚所选的点
            } else {
                TEMChart_Panel.chartPanel.setMouseZoomable(true);
            }
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                TEMChart_Panel.series2.clear();
                TEMChart_Panel.series3.clear();
                TEMChart_Panel.series4.clear();
                TEMChart_Panel.x = -1;
                TEMChart_Panel.y = -1;
                //点选
                xyplot.setDomainCrosshairValue(0);//设定值还原 防止划线时 会记录上次的值
                xyplot.setRangeCrosshairValue(0);
                //清楚所选的点
                xyplot.clearAnnotations();
                flagPointOrLine = 0;//什么都不选
            }
            if (evt.getKeyCode() == KeyEvent.VK_P) {
                TEMChart_Panel.series2.clear();//选线选择时应将不同的选线数据清零
                TEMChart_Panel.series3.clear();//选线选择时应将不同的选线数据清零
                TEMChart_Panel.series4.clear();//选线选择时应将不同的选线数据清零
                xyplot.setDomainCrosshairValue(0);//设定值还原 防止划线时 会记录上次的值
                xyplot.setRangeCrosshairValue(0);
                xyplot.clearAnnotations();
                flagPointOrLine = 2;
            } else if (evt.getKeyCode() == KeyEvent.VK_L) {
                TEMChart_Panel.series2.clear();//选线选择时应将不同的选线数据清零
                TEMChart_Panel.series3.clear();//选线选择时应将不同的选线数据清零
                TEMChart_Panel.series4.clear();//选线选择时应将不同的选线数据清零
                xyplot.setDomainCrosshairValue(0);//设定值还原
                xyplot.setRangeCrosshairValue(0);
                xyplot.clearAnnotations();
                flagPointOrLine = 1;
            }
        }
    }//GEN-LAST:event_formKeyPressed

    private void saveImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageButtonActionPerformed
        // TODO add your handling code here:
        saveImage(TEMChart_Panel);
    }//GEN-LAST:event_saveImageButtonActionPerformed
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
    private void openFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileMenuItemActionPerformed
        // TODO add your handling code here:
        fileOpenButtActionPerformed(evt);
    }//GEN-LAST:event_openFileMenuItemActionPerformed

    private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileButtonActionPerformed
        if (pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
            try {
                // TODO add your handling code here:
                saveFile();
            } catch (IOException ex) {
                Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "无测点数据文件，请先读取文件！");
            return;
        }
    }//GEN-LAST:event_saveFileButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        // TODO add your handling code here:
        AboutGeopen about = new AboutGeopen(this, true);
        about.pack();
        about.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void saveGPTMMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGPTMMenuItemActionPerformed
        // TODO add your handling code here:
        saveFileButtonActionPerformed(evt);
    }//GEN-LAST:event_saveGPTMMenuItemActionPerformed

    private void pointsParasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointsParasMenuItemActionPerformed
        // TODO add your handling code here:
        pointsParasButtonActionPerformed(evt);
    }//GEN-LAST:event_pointsParasMenuItemActionPerformed

    private void savePicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePicMenuItemActionPerformed
        // TODO add your handling code here:
        saveImageButtonActionPerformed(evt);
    }//GEN-LAST:event_savePicMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        // TODO add your handling code here:
        exitSystem();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    public void exitSystem() {
        if (pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
            int choice = JOptionPane.showConfirmDialog(null, "是否确认退出程序并保存处理的数据文件？", "退出", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == 0) {
                try {
                    // TODO add your handling code here:
                    saveFile();
                } catch (IOException ex) {
                    Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            } else if (choice == 1) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }
    private void GPSMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GPSMenuItemActionPerformed
        // TODO add your handling code here:
        GPSButtonActionPerformed(evt);
    }//GEN-LAST:event_GPSMenuItemActionPerformed

    private void dataVisualTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dataVisualTabbedPaneStateChanged
        // TODO add your handling code here:
        //通道数据
        if (dataVisualTabbedPane.getSelectedIndex() == 0 && originalDataPanel.getComponents().length != 0) {
            dataVisualTabbedPane.remove(1);
        }
    }//GEN-LAST:event_dataVisualTabbedPaneStateChanged

    public void drawOriginalData() {
        //通道数据
        double sampletRate = 0;
        if (TEMSourceData.temData != null) {
            //清除
            originalDataPanel.removeAll();
            dataVisualTabbedPane.add(originalScrollPane);
            dataVisualTabbedPane.add(originalScrollPane);//添加源数据显示
            dataVisualTabbedPane.setTitleAt(1, "源数据显示");
            dataVisualTabbedPane.setSelectedIndex(1);
            //赋值
            int counts = TEMSourceData.temData.length;
            double dividend = 1;
//            double dividend = Math.pow(2, 23) / 5;
            for (int i = 0; i < counts; i++) {//文件个数
                if (TEMSourceData.fundfrequency[i] == 4) {
                    sampletRate = 1D / 15000;
                } else if (TEMSourceData.fundfrequency[i] < 4) {
                    sampletRate = 1D / 30000;
                } else if (TEMSourceData.fundfrequency[i] > 4) {
                    sampletRate = 1D / 1000000;
                }
                int countsJ = TEMSourceData.temData[i].length;
                int countsM = TEMSourceData.temData[i][0].length;
                OriginalDataChart odc = new OriginalDataChart(TEMSourceData.filesName[i], countsJ);
                for (int m = 0; m < countsM; m++) {
                    double time = sampletRate * (m + 1) * 1000;
                    if (countsJ == 1) {
                        odc.xyseries1.add(time, TEMSourceData.temData[i][0][m] / dividend);
//                        System.out.println(TEMSourceData.temData[i][0][m]+"-----"+TEMSourceData.temData[i][0][m] / dividend);
                    } else if (countsJ == 2) {
                        odc.xyseries1.add(time, TEMSourceData.temData[i][0][m] / dividend);
                        odc.xyseries2.add(time, TEMSourceData.temData[i][1][m] / dividend);
                    } else if (countsJ == 3) {
                        odc.xyseries1.add(time, TEMSourceData.temData[i][0][m] / dividend);
                        odc.xyseries2.add(time, TEMSourceData.temData[i][1][m] / dividend);
                        odc.xyseries3.add(time, TEMSourceData.temData[i][2][m] / dividend);
                    }

                }
                originalDataPanel.add(odc.createDemoPanel());
            }
        }
    }
    private void originalDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originalDataMenuItemActionPerformed
        // TODO add your handling code here:
        originalDataButtonActionPerformed(evt);
    }//GEN-LAST:event_originalDataMenuItemActionPerformed

    private void originalDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originalDataButtonActionPerformed
        // TODO add your handling code here:
        //通道数据
        if (pointNode.getChildCount() != 0) {//如果测点节点下的文件数不为零才能弹出 坐标设置窗口
            drawOriginalData();
        } else {
            JOptionPane.showMessageDialog(this, "无测点数据文件，请先读取文件！");
            return;
        }
    }//GEN-LAST:event_originalDataButtonActionPerformed

    private void editMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuItemActionPerformed
        // TODO add your handling code here:
        editButtonActionPerformed(evt);
    }//GEN-LAST:event_editMenuItemActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        paraDialog.editData();
    }//GEN-LAST:event_editButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        exitMenuItemActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void deleteNodeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNodeMenuItemActionPerformed
        // TODO add your handling code here:
        updateArray();
    }//GEN-LAST:event_deleteNodeMenuItemActionPerformed

    private void saveUSFMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveUSFMenuItemActionPerformed
        try {
            // TODO add your handling code here
            saveFile(new TEMUSFFileFilter());
        } catch (IOException ex) {
            Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TEMProcessingProgramWin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveUSFMenuItemActionPerformed

    private void groundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groundButtonActionPerformed
        // TODO add your handling code here:
        TEMData.flagGF = 0;
        GroundOrAirDia.setVisible(false);
    }//GEN-LAST:event_groundButtonActionPerformed

    private void flyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flyButtonActionPerformed
        // TODO add your handling code here:
        TEMData.flagGF = 1;

        GroundOrAirDia.setVisible(false);
    }//GEN-LAST:event_flyButtonActionPerformed

    public void saveFile(TEMUSFFileFilter geopenFileFilter) throws IOException, ClassNotFoundException {
//        TEMGeoPenFileFilter geopenFileFilter = new TEMGeoPenFileFilter();
        if (geopenFileFilter.showSaveDialog(this) == TEMUSFFileFilter.APPROVE_OPTION) {
            // 获取当前路径
            File currentDirectory = geopenFileFilter.getCurrentDirectory();
            // 获取文件名
            String fileName = geopenFileFilter.getSelectedFile().getName();
            String[] readFile = fileName.split("[.]");
            // 获取后缀名
            String suf = geopenFileFilter.getSuf();
            // 组合保存路径
            String savePath = currentDirectory + "\\" + readFile[0] + "."
                    + suf;
            File geopenFile = new File(savePath);//监测是否有重复的
            if (geopenFile.exists()) {
                int count = JOptionPane.showConfirmDialog(this, "文件已存在,是否覆盖？", "文件存在", JOptionPane.OK_OPTION);
                if (count == 0) {
                    try {
                        saveUSFFormat(geopenFile);
                    } catch (Exception ie) {
                    }
                } else {
                    saveFile();
                }
            } else {
                saveUSFFormat(geopenFile);
            }
        }
    }

    public void sUSF(FileWriter fw, int counts, ArrayList linePoints, HashMap<ArrayList, String> xy_fileName) throws IOException {
        String seperator = "";
        int length1 = 16;
        int length2 = 10;
        String format1 = "%1$-" + length1 + "s" + seperator;
        String format2 = "%1$-" + length2 + "s" + seperator;
        DecimalFormat decimalFormat = new DecimalFormat("0.0000000E00");
        DecimalFormat format = new DecimalFormat("0.0");
        for (int i = 0; i < counts; i++) {
            if (xy_fileName == null) {
                fileName = TEMSourceData.filesName[i];
            } else {
                fileName = xy_fileName.get(linePoints.get(i));
            }
            ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);//需要的参数
            initialTable();
            ArrayList<String> infor = findFileInformation(paraDialog.pointsParasTable, fileName);//0
            //装置类型
            if (infor.get(paraDialog.columm14).equals("中心回线")) {
                fw.write("/ARRAY: CENTRAL LOOP TEM");
            } else if (infor.get(paraDialog.columm14).equals("大定源")) {
                fw.write("/ARRAY: FIXED LOOP TEM");
            } else if (infor.get(paraDialog.columm14).equals("重叠回线")) {
                fw.write("/ARRAY: COINCIDENT LOOP TEM");
            }
            fw.write("\r\n");
            //方位角
            fw.write("/AZIMUTH:       " + Double.parseDouble(infor.get(paraDialog.columm6)));
            fw.write("\r\n");
            //日期
            try {
                fw.write("/DATE:     " + fileName.substring(5, 13));
            } catch (Exception e) {
                fw.write("/DATE:     " + "");
            }
            fw.write("\r\n");
            //时间
            try {
                fw.write("/DAYTIME:     " + fileName.substring(13, 19));
            } catch (Exception e) {
                fw.write("/DAYTIME:     " + "");
            }
            fw.write("\r\n");
            //线圈位置 大定源
            if (infor.get(paraDialog.columm14).equals("大定源")) {
                String x = format.format(Double.parseDouble(infor.get(paraDialog.columm4)));
                String y = format.format(Double.parseDouble(infor.get(paraDialog.columm5)));
//                    System.out.println(x);
                if (x.length() >= 7) {
                    x = x.substring(x.length() - 7, x.length());
                }
                if (y.length() >= 7) {
                    y = y.substring(y.length() - 7, y.length());
                }
                fw.write("/COIL_LOCATION:  " + x + ",  " + y);
            } else {
                fw.write("/COIL_LOCATION:       0.0000000,       0.0000000");
            }
            fw.write("\r\n");
            //发射线框边长
            fw.write("/LOOP_SIZE:      " + Double.parseDouble(infor.get(paraDialog.columm2)) + "," + Double.parseDouble(infor.get(paraDialog.columm3)));
            fw.write("\r\n");
            //数据分段数
            fw.write("/SWEEPS:            " + 1);
            fw.write("\r\n");
            //接收有效面积
            fw.write("/COIL_SIZE:     " + infor.get(paraDialog.columm13));
            fw.write("\r\n");
            //电流
            fw.write("/CURRENT:      " + infor.get(paraDialog.columm16));
            fw.write("\r\n");
            //发射线框匝数
            fw.write("/LOOP_TURNS:            " + (int) Double.parseDouble(infor.get(paraDialog.columm7)));
            fw.write("\r\n");
            //关断时间
            fw.write("/RAMP_TIME:       " + infor.get(paraDialog.columm15));
            fw.write("\r\n");
            //频率
            int index = getIndex(TEMSourceData.filesName, fileName);
            fw.write("/FREQUENCY:       " + changeFundFre(TEMSourceData.fundfrequency[index]).replace("Hz", ""));//转为字符基频TEMSourceData.fundfrequency[index]);
            fw.write("\r\n");
            //延迟
            fw.write("/TIME_DELAY:      0.0");
            fw.write("\r\n");
            //测点位置
            String x = format.format(Double.parseDouble(infor.get(paraDialog.columm8)));
            String y = format.format(Double.parseDouble(infor.get(paraDialog.columm9)));
            String z = format.format(Double.parseDouble(infor.get(paraDialog.columm10)));
            int subL = 7;
            if (x.length() >= subL) {
                x = x.substring(x.length() - subL, x.length());
            }
            if (y.length() >= subL) {
                y = y.substring(y.length() - subL, y.length());
            }
            if (z.length() >= subL) {
                z = z.substring(z.length() - subL, z.length());
            }
            fw.write("/LOCATION:  " + x + ",  " + y + ",  " + z);
            fw.write("\r\n");
            //数据点个数
            fw.write("/POINTS:           " + ((ArrayList) voltage_time.get(0)).size());
            fw.write("\r\n");
            //测线名
            fw.write("/SOUNDING_NAME: " + fileName);
//                fw.write("/SOUNDING_NAME: " + lineName);
            fw.write("\r\n");
            //编号
            fw.write("/SOUNDING_NUMBER: " + (i + 1));
            fw.write("\r\n");
            //长度单位
            fw.write("/LENGTH_UNITS: M");
            fw.write("\r\n");
            //电压单位
            fw.write("/VOLTAGE_UNITS: V/AM2");
            fw.write("\r\n");
            //结束符
            fw.write("/END");
            fw.write("\r\n");
            //表头
            fw.write(String.format(format2, "INDEX,"));
            fw.write(String.format(format2, "TIME,"));
            fw.write(String.format(format2, "WIDTH,"));
            fw.write(String.format(format2, "VOLTAGE,"));
            fw.write(String.format(format2, "ERROR_BAR,"));
            fw.write(String.format(format2, "MASK"));
            fw.write("\r\n");
            for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {
//                    System.out.println( ((ArrayList) voltage_time.get(1)).get(j));
                if (j == ((ArrayList) voltage_time.get(0)).size() - 1) {
                    fw.write(String.format(format2, (j + 1) + ",")
                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(1)).get(j) / 1000) + ",")
                            //                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(7)).get(j) / 1000) + ",")
                            + String.format(format1, decimalFormat.format(((Double) ((ArrayList) voltage_time.get(1)).get(j) - (Double) ((ArrayList) voltage_time.get(1)).get(j - 1)) / 1000) + ",")
                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(0)).get(j) / 1000) + ",")
                            + 0 + ","
                            + 1);
                } else {
                    fw.write(String.format(format2, (j + 1) + ",")
                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(1)).get(j) / 1000) + ",")
                            //                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(7)).get(j) / 1000) + ",")
                            + String.format(format1, decimalFormat.format(((Double) ((ArrayList) voltage_time.get(1)).get(j + 1) - (Double) ((ArrayList) voltage_time.get(1)).get(j)) / 1000) + ",")
                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(0)).get(j) / 1000) + ",")
                            + 0 + ","
                            + 1);
                }
                fw.write("\r\n");
            }
            fw.write("/END");
            fw.write("\r\n");
        }
        fw.close();
    }

    public void saveUSFFormat(File file) throws IOException {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
        if (node.getPath().length == 2) {//导出全部测点
            lineName = null;
        } else if (node.getPath().length == 3) {//=3 导出侧线
            lineName = node.getPath()[2].toString().trim();//获得自定义测线名
        } else {
            return;
        }
//        System.out.println(node.getPath().length + "++++");
        if (TEMSourceData.integrationValue.size() != 0) {//只有存在积分数据 才能弹出
            String fileName = "";
            int counts = 0;
            ArrayList linePoints = new ArrayList();
            if (lineName == null) {
                counts = TEMSourceData.filesName.length;
            } else {
                linePoints = (ArrayList) TEMSourceData.lineName_XYList.get(lineName);
                counts = TEMSourceData.filesName.length;
            }
            //写入文件头
            FileWriter fw = new FileWriter(file);
            fw.write("//USF: Universal Sounding Format");
            fw.write("\r\n");
            fw.write("//SOUNDINGS: " + counts);
            fw.write("\r\n");
            fw.write("//END");
            fw.write("\r\n");
            fw.write("\r\n");
            if (lineName == null) {
                sUSF(fw, counts, null, null);
            } else {
                sUSF(fw, counts, linePoints, TEMSourceData.xy_fileName);
                lineName = null;
            }
//            for (int i = 0; i < linePoints.size(); i++) {
//                if (lineName == null) {
//                } else {
//                    fileName = TEMSourceData.xy_fileName.get(linePoints.get(i));
//                }
//                ArrayList voltage_time = TEMSourceData.integrationValue.get(fileName);//需要的参数
//                initialTable();
//                ArrayList<String> infor = findFileInformation(paraDialog.pointsParasTable, fileName);//0
//                //装置类型
////                System.out.println(infor + "," + infor.size());
//                if (infor.get(paraDialog.columm14).equals("中心回线")) {
//                    fw.write("/ARRAY: CENTRAL LOOP TEM");
//                } else if (infor.get(paraDialog.columm14).equals("大定源")) {
//                    fw.write("/ARRAY: FIXED LOOP TEM");
//                } else if (infor.get(paraDialog.columm14).equals("重叠回线")) {
//                    fw.write("/ARRAY: COINCIDENT LOOP TEM");
//                }
//                fw.write("\r\n");
//                //方位角
//                fw.write("/AZIMUTH:       " + Double.parseDouble(infor.get(paraDialog.columm6)));
//                fw.write("\r\n");
//                //日期
//                try {
//                    fw.write("/DATE:     " + fileName.substring(5, 13));
//                } catch (Exception e) {
//                    fw.write("/DATE:     " + "");
//                }
//                fw.write("\r\n");
//                //时间
//                try {
//                    fw.write("/DAYTIME:     " + fileName.substring(13, 19));
//                } catch (Exception e) {
//                    fw.write("/DAYTIME:     " + "");
//                }
//                fw.write("\r\n");
//                //线圈位置 大定源
//                if (infor.get(paraDialog.columm14).equals("大定源")) {
//                    String x = format.format(Double.parseDouble(infor.get(paraDialog.columm4)));
//                    String y = format.format(Double.parseDouble(infor.get(paraDialog.columm5)));
////                    System.out.println(x);
//                    if (x.length() >= 7) {
//                        x = x.substring(x.length() - 7, x.length());
//                    }
//                    if (y.length() >= 7) {
//                        y = y.substring(y.length() - 7, y.length());
//                    }
//                    fw.write("/COIL_LOCATION:  " + x + ",  " + y);
//                } else {
//                    fw.write("/COIL_LOCATION:       0.0000000,       0.0000000");
//                }
//                fw.write("\r\n");
//                //发射线框边长
//                fw.write("/LOOP_SIZE:      " + Double.parseDouble(infor.get(paraDialog.columm2)) + "," + Double.parseDouble(infor.get(paraDialog.columm3)));
//                fw.write("\r\n");
//                //数据分段数
//                fw.write("/SWEEPS:            " + 1);
//                fw.write("\r\n");
//                //接收有效面积
//                fw.write("/COIL_SIZE:     " + infor.get(paraDialog.columm13));
//                fw.write("\r\n");
//                //电流
//                fw.write("/CURRENT:      " + infor.get(paraDialog.columm16));
//                fw.write("\r\n");
//                //发射线框匝数
//                fw.write("/LOOP_TURNS:            " + (int) Double.parseDouble(infor.get(paraDialog.columm7)));
//                fw.write("\r\n");
//                //关断时间
//                fw.write("/RAMP_TIME:       " + infor.get(paraDialog.columm15));
//                fw.write("\r\n");
//                //频率
//                int index = getIndex(TEMSourceData.filesName, fileName);
//                fw.write("/FREQUENCY:       " + changeFundFre(TEMSourceData.fundfrequency[index]).replace("Hz", ""));//转为字符基频TEMSourceData.fundfrequency[index]);
//                fw.write("\r\n");
//                //延迟
//                fw.write("/TIME_DELAY:      0.0");
//                fw.write("\r\n");
//                //测点位置
//                String x = format.format(Double.parseDouble(infor.get(paraDialog.columm8)));
//                String y = format.format(Double.parseDouble(infor.get(paraDialog.columm9)));
//                String z = format.format(Double.parseDouble(infor.get(paraDialog.columm10)));
//                if (x.length() >= 7) {
//                    x = x.substring(x.length() - 7, x.length());
//                }
//                if (y.length() >= 7) {
//                    y = y.substring(y.length() - 7, y.length());
//                }
//                if (z.length() >= 7) {
//                    z = z.substring(z.length() - 7, z.length());
//                }
//                fw.write("/LOCATION:  " + x + ",  " + y + ",  " + z);
//                fw.write("\r\n");
//                //数据点个数
//                fw.write("/POINTS:           " + ((ArrayList) voltage_time.get(0)).size());
//                fw.write("\r\n");
//                //测线名
//                fw.write("/SOUNDING_NAME: " + fileName);
////                fw.write("/SOUNDING_NAME: " + lineName);
//                fw.write("\r\n");
//                //编号
//                fw.write("/SOUNDING_NUMBER: " + (i + 1));
//                fw.write("\r\n");
//                //长度单位
//                fw.write("/LENGTH_UNITS: M");
//                fw.write("\r\n");
//                //电压单位
//                fw.write("/VOLTAGE_UNITS: V/AM2");
//                fw.write("\r\n");
//                //结束符
//                fw.write("/END");
//                fw.write("\r\n");
//                //表头
//                fw.write(String.format(format2, "INDEX,"));
//                fw.write(String.format(format2, "TIME,"));
//                fw.write(String.format(format2, "WIDTH,"));
//                fw.write(String.format(format2, "VOLTAGE,"));
//                fw.write(String.format(format2, "ERROR_BAR,"));
//                fw.write(String.format(format2, "MASK"));
//                fw.write("\r\n");
//                for (int j = 0; j < ((ArrayList) voltage_time.get(0)).size(); j++) {
////                    System.out.println( ((ArrayList) voltage_time.get(1)).get(j));
//                    if (j == ((ArrayList) voltage_time.get(0)).size() - 1) {
//                        fw.write(String.format(format2, (j + 1) + ",")
//                                + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(1)).get(j) / 1000) + ",")
//                                //                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(7)).get(j) / 1000) + ",")
//                                + String.format(format1, decimalFormat.format(((Double) ((ArrayList) voltage_time.get(1)).get(j) - (Double) ((ArrayList) voltage_time.get(1)).get(j - 1)) / 1000) + ",")
//                                + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(0)).get(j) / 1000) + ",")
//                                + 0 + ","
//                                + 1);
//                    } else {
//                        fw.write(String.format(format2, (j + 1) + ",")
//                                + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(1)).get(j) / 1000) + ",")
//                                //                            + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(7)).get(j) / 1000) + ",")
//                                + String.format(format1, decimalFormat.format(((Double) ((ArrayList) voltage_time.get(1)).get(j + 1) - (Double) ((ArrayList) voltage_time.get(1)).get(j)) / 1000) + ",")
//                                + String.format(format1, decimalFormat.format((Double) ((ArrayList) voltage_time.get(0)).get(j) / 1000) + ",")
//                                + 0 + ","
//                                + 1);
//                    }
//                    fw.write("\r\n");
//                }
//                fw.write("/END");
//                fw.write("\r\n");
//            }

        }
    }

    public int getIndex(String[] filesName, String fileName) {
        for (int i = 0; i < filesName.length; i++) {
            if (fileName.equals(filesName[i])) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<String> findFileInformation(JTable table, String fileName) {
        ArrayList<String> infor = new ArrayList<String>();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (fileName.equals(table.getValueAt(i, 1).toString())) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    if (table.getValueAt(i, j) != null) {//避免只采集一道 其他道为null
                        infor.add(table.getValueAt(i, j).toString());
                    } else {
                        infor.add("null");
                    }
                }
                break;
            }
        }
        return infor;
    }

    public void updateArray() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) SelectedDataTree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getParent() != null) {
            DefaultTreeModel model = (DefaultTreeModel) SelectedDataTree.getModel();
            model.removeNodeFromParent(selectedNode);
            //处理数据
            String str = selectedNode.getRoot().toString();
            double x = 0;//坐标
            double y = 0;//坐标
            if (str.trim().toUpperCase().endsWith(".TM") || str.trim().toUpperCase().endsWith(".CTM")) {
                int index = 0;
                if (pointNode.getChildCount() >= 0) {
                    for (int i = 0; i < TEMSourceData.filesName.length; i++) {
                        if (TEMSourceData.filesName[i].equals(str)) {
                            index = i;
                            if (TEMSourceData.rCenterX.length >= 0) {
                                if (TEMSourceData.filesName.length == 1) {
                                    x = Double.parseDouble(TEMSourceData.rCenterX[0].toString());
                                    y = Double.parseDouble(TEMSourceData.rCenterY[0].toString());
                                } else if (TEMSourceData.filesName.length > 1) {
                                    if (TEMSourceData.rCenterX[0] != TEMSourceData.rCenterX[1]) {
                                        x = Double.parseDouble(TEMSourceData.rCenterX[index].toString());
                                        y = Double.parseDouble(TEMSourceData.rCenterY[index].toString());
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                //数组移除
                remove(index);
                //设定文件数
                totalFilesLabel.setText("文件总数：" + pointNode.getChildCount());
                //移除集合
                if (TEMChartPanle.series1.getItemCount() > 0) {
                    for (int i = 0; i < TEMChartPanle.series1.getItemCount(); i++) {
                        if (TEMChartPanle.series1.getX(i).doubleValue() == x && TEMChartPanle.series1.getY(i).doubleValue() == y) {
                            TEMChartPanle.series1.remove(i);
                        }
                    }
//                    System.out.println(TEMChartPanle.series1.getX(0) + "   1" + TEMChartPanle.series1.getY(0));
                }
                if (TEMChartPanle.series2.getItemCount() > 0) {
                    for (int i = 0; i < TEMChartPanle.series2.getItemCount(); i++) {
                        if (TEMChartPanle.series2.getX(i).doubleValue() == x && TEMChartPanle.series2.getY(i).doubleValue() == y) {
                            TEMChartPanle.series2.remove(i);
                        }
                    }
                }
                if (TEMChartPanle.series3.getItemCount() > 0) {
                    for (int i = 0; i < TEMChartPanle.series3.getItemCount(); i++) {
                        if (TEMChartPanle.series3.getX(i).doubleValue() == x && TEMChartPanle.series3.getY(i).doubleValue() == y) {
                            TEMChartPanle.series3.remove(i);
                        }
                    }
                }
                if (TEMChartPanle.series5.getItemCount() > 0) {
                    for (int i = 0; i < TEMChartPanle.series5.getItemCount(); i++) {
                        if (TEMChartPanle.series5.getX(i).doubleValue() == x && TEMChartPanle.series5.getY(i).doubleValue() == y) {
                            TEMChartPanle.series5.remove(i);
                        }
                    }
                }
                if (TEMChartPanle.series4.getItemCount() > 0) {
                    TEMChartPanle.series4.remove(0);
                }
                TEMSourceData.z_fileName.remove(str);
                TEMSourceData.integrationValue.remove(str);
                TEMSourceData.inversionValue.remove(str);
                TEMSourceData.xy_fileName.remove(str);
                //如若选定了测线 在测线中移除
                ArrayList<Double> deletePos = new ArrayList<Double>();
                deletePos.add(x);
                deletePos.add(y);
                if (lineNode.getChildCount() != 0) {
                    int counts = lineNode.getChildCount();
                    for (int i = 0; i < counts; i++) {
                        String lineName = lineNode.getChildAt(i).toString();
                        ArrayList<Double> xyList = TEMSourceData.lineName_XYList.get(lineName);
                        xyList.remove(deletePos);
                        //当测线只有一个点的时候 移除
                        if (TEMSourceData.lineName_XYList.get(lineName).size() == 1) {
                            int indexDelet = TEMSourceData.lineName.indexOf(lineName);
                            lineNode.remove(indexDelet);
                            TEMSourceData.lineName_XYList.remove(lineName);
                            TEMSourceData.lineName.remove(lineName);
                            clearMarksSeries();//删除已经显示的测线
                            SelectedDataTree.updateUI();//更新
                            counts = lineNode.getChildCount();
                            i = 0;
                            continue;
                        }
                    }
                }
                //无数据时初始化界面
                if (pointNode.getChildCount() == 0) {
                    temData.initalParameters();
                }
            } else {
                TEMSourceData.xy_fileName.remove(lineName);
                TEMSourceData.lineName.remove(lineName);
                clearMarksSeries();//删除已经显示的测线
            }
        }
    }

    public void remove(int index) {
        int counts = TEMSourceData.filesName.length - 1;
        String[] filesName = new String[counts];//文件名
        String[] workPlace = new String[counts];//工作地点
        String[] latitude = new String[counts];//纬度
        String[] longtitude = new String[counts];//经度
        String[] status = new String[counts];//定位状态
        String[] time = new String[counts];//采样时间
        int[] gain = new int[counts];//增益
        int[] channels = new int[counts];//道数
        int[] fundfrequency = new int[counts];//基频
        int[] superposition = new int[counts];//叠加次数
        //坐标设置参数
        Object[] trWidth = new Object[counts];//Tx边长X(m)
        Object[] trLength = new Object[counts];//Tx边长Y(m)
        Object[] trCenterX = new Object[counts];
        Object[] trCenterY = new Object[counts];
        Object[] trAngle = new Object[counts];
        Object[] trTurns = new Object[counts];
        Object[] rCenterX = new Object[counts];
        Object[] rCenterY = new Object[counts];
        Object[] rCenterZ = new Object[counts];
        Object[] rLongtitude = new Object[counts];
        Object[] rLatitude = new Object[counts];
        Object[] rArea = new Object[counts];
        Object[] Array = new Object[counts];
        Object[] turnOffTime = new Object[counts];
        Object[] current = new Object[counts];
        Object[] firstChannel = new Object[counts];
        Object[] secondChannel = new Object[counts];
        Object[] thirdChannel = new Object[counts];
        //更新数据
        double[][][] temData = new double[counts][TEMSourceData.temData[0].length][TEMSourceData.temData[0][0].length];
        int rec = 0;
        for (int i = 0; i < counts + 1; i++) {
            if (i != index) {
                filesName[rec] = TEMSourceData.filesName[i];
                workPlace[rec] = TEMSourceData.workPlace[i];
                latitude[rec] = TEMSourceData.latitude[i];
                longtitude[rec] = TEMSourceData.longtitude[i];
                status[rec] = TEMSourceData.status[i];
                time[rec] = TEMSourceData.time[i];
                gain[rec] = TEMSourceData.gain[i];
                channels[rec] = TEMSourceData.channels[i];
                fundfrequency[rec] = TEMSourceData.fundfrequency[i];
                superposition[rec] = TEMSourceData.superposition[i];
                //坐标设置参数
                trWidth[rec] = TEMSourceData.trWidth[i];//Tx边长X(m)
                trLength[rec] = TEMSourceData.trLength[i];//Tx边长Y(m)
                trCenterX[rec] = TEMSourceData.trCenterX[i];
                trCenterY[rec] = TEMSourceData.trCenterY[i];
                trAngle[rec] = TEMSourceData.trAngle[i];
                trTurns[rec] = TEMSourceData.trTurns[i];
                rCenterX[rec] = TEMSourceData.rCenterX[i];
                rCenterY[rec] = TEMSourceData.rCenterY[i];
                rCenterZ[rec] = TEMSourceData.rCenterZ[i];
                rLongtitude[rec] = TEMSourceData.rLongtitude[i];
                rLatitude[rec] = TEMSourceData.rLatitude[i];
                rArea[rec] = TEMSourceData.rArea[i];
                Array[rec] = TEMSourceData.Array[i];
                turnOffTime[rec] = TEMSourceData.turnOffTime[i];
                current[rec] = TEMSourceData.current[i];
                firstChannel[rec] = TEMSourceData.firstChannel[i];
                secondChannel[rec] = TEMSourceData.secondChannel[i];
                thirdChannel[rec] = TEMSourceData.thirdChannel[i];
                temData[rec] = TEMSourceData.temData[i];
                rec++;
            }
        }
        //更新
        TEMSourceData.filesName = filesName;//文件名
        TEMSourceData.workPlace = workPlace;//工作地点
        TEMSourceData.latitude = latitude;//纬度
        TEMSourceData.longtitude = longtitude;//经度
        TEMSourceData.status = status;//定位状态
        TEMSourceData.time = time;//采样时间
        TEMSourceData.gain = gain;//增益
        TEMSourceData.channels = channels;//道数
        TEMSourceData.fundfrequency = fundfrequency;//基频
        TEMSourceData.superposition = superposition;//叠加次数
        //初始化坐标设置参数
        TEMSourceData.trWidth = trWidth;//Tx边长X(m)
        TEMSourceData.trLength = trLength;//Tx边长Y(m)
        TEMSourceData.trCenterX = trCenterX;
        TEMSourceData.trCenterY = trCenterY;
        TEMSourceData.trAngle = trAngle;
        TEMSourceData.trTurns = trTurns;
        TEMSourceData.rCenterX = rCenterX;
        TEMSourceData.rCenterY = rCenterY;
        TEMSourceData.rCenterZ = rCenterZ;
        TEMSourceData.rLongtitude = rLongtitude;
        TEMSourceData.rLatitude = rLatitude;
        TEMSourceData.rArea = rArea;
        TEMSourceData.Array = Array;
        TEMSourceData.turnOffTime = turnOffTime;
        TEMSourceData.current = current;
        TEMSourceData.firstChannel = firstChannel;//第一道
        TEMSourceData.secondChannel = secondChannel;//第二道
        TEMSourceData.thirdChannel = thirdChannel;//第二道
        TEMSourceData.temData = temData;
//        System.out.println(TEMSourceData.filesName.length);
    }

    public void saveFile() throws IOException, ClassNotFoundException {
        TEMGeoPenFileFilter geopenFileFilter = new TEMGeoPenFileFilter();
        if (geopenFileFilter.showSaveDialog(this) == TEMImageFileFilter.APPROVE_OPTION) {
            // 获取当前路径
            File currentDirectory = geopenFileFilter.getCurrentDirectory();
            // 获取文件名
            String fileName = geopenFileFilter.getSelectedFile().getName();
            String[] readFile = fileName.split("[.]");
            // 获取后缀名
            String suf = geopenFileFilter.getSuf();
            // 组合保存路径
            String savePath = currentDirectory + "\\" + readFile[0] + "."
                    + suf;
            File geopenFile = new File(savePath);//监测是否有重复的
            if (geopenFile.exists()) {
                int count = JOptionPane.showConfirmDialog(this, "文件已存在,是否覆盖？", "文件存在", JOptionPane.OK_OPTION);
                if (count == 0) {
                    try {
                        // 将图片写到保存路径
                        saveObject(geopenFile);
                    } catch (Exception ie) {
                    }
                } else {
                    saveFile();
                }
            } else {
                saveObject(geopenFile);
            }
        }
    }

    /**
     * 保存字节的格式
     *
     * @param geopenFile
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void saveObject(File geopenFile) throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream(geopenFile);
        ObjectOutputStream ous = new ObjectOutputStream(fos);
        ous.writeObject(TEMSourceData.filesName);
        ous.writeObject(TEMSourceData.workPlace);
        ous.writeObject(TEMSourceData.latitude);
        ous.writeObject(TEMSourceData.longtitude);
        ous.writeObject(TEMSourceData.status);
        ous.writeObject(TEMSourceData.time);
        ous.writeObject(TEMSourceData.gain);
        ous.writeObject(TEMSourceData.channels);
        ous.writeObject(TEMSourceData.fundfrequency);
        ous.writeObject(TEMSourceData.superposition);
        ous.writeObject(TEMSourceData.trWidth);
        ous.writeObject(TEMSourceData.trLength);
        ous.writeObject(TEMSourceData.trCenterX);
        ous.writeObject(TEMSourceData.trCenterY);
        ous.writeObject(TEMSourceData.trAngle);
        ous.writeObject(TEMSourceData.trTurns);
        ous.writeObject(TEMSourceData.rCenterX);
        ous.writeObject(TEMSourceData.rCenterY);
        ous.writeObject(TEMSourceData.rCenterZ);
        ous.writeObject(TEMSourceData.rLongtitude);
        ous.writeObject(TEMSourceData.rLatitude);
        ous.writeObject(TEMSourceData.rArea);
        ous.writeObject(TEMSourceData.Array);
        ous.writeObject(TEMSourceData.turnOffTime);
        ous.writeObject(TEMSourceData.current);
        ous.writeObject(TEMSourceData.firstChannel);
        ous.writeObject(TEMSourceData.secondChannel);
        ous.writeObject(TEMSourceData.thirdChannel);
        ous.writeObject(TEMSourceData.z_fileName);
        ous.writeObject(TEMSourceData.xy_fileName);
        ous.writeObject(TEMSourceData.integrationValue);
        ous.writeObject(TEMSourceData.lineName_XYList);
        ous.writeObject(TEMSourceData.lineName);
        ous.writeObject(TEMSourceData.temData);
        ous.close();
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

    public void setGPSInforTableValue() {
        TEMGpsInforDialog gpsDialog = new TEMGpsInforDialog(this, true);
        gpsDialog.pack();
        if (TEMSourceData.workPlace != null) {
            //添加数据
            int row = TEMSourceData.workPlace.length;
            int column = 11;
            String[] strs = new String[11];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    if (j == 0) {
                        strs[j] = i + 1 + "";
                    } else if (j == 1) {
                        strs[j] = TEMSourceData.filesName[i];
                    } else if (j == 2) {
                        strs[j] = TEMSourceData.workPlace[i];
                    } else if (j == 3) {
                        strs[j] = TEMSourceData.time[i];
                    } else if (j == 4) {
                        strs[j] = TEMSourceData.longtitude[i];
                    } else if (j == 5) {
                        strs[j] = TEMSourceData.latitude[i];
                    } else if (j == 6) {
                        strs[j] = TEMSourceData.gain[i] + "";
                    } else if (j == 7) {
                        strs[j] = TEMSourceData.channels[i] + "";
                    } else if (j == 8) {
                        strs[j] = changeFundFre(TEMSourceData.fundfrequency[i]);//转为字符基频
                    } else if (j == 9) {
                        strs[j] = TEMSourceData.superposition[i] + "";
                    } else if (j == 10) {
                        strs[j] = TEMSourceData.status[i];
                    }
                }
                ((DefaultTableModel) gpsDialog.gpsInforTable.getModel()).addRow(strs);
            }
            TEMSetTableRowColor.makeFace(gpsDialog.gpsInforTable);//调整行颜色
            fitTableColumns(gpsDialog.gpsInforTable);//调整列宽 默认关断时间列不可以见
            setTableCenter(gpsDialog.gpsInforTable);//居中显示
            gpsDialog.setVisible(true);
        }
    }

    /**
     * 基频转换
     *
     * @param fund
     * @return
     */
    public String changeFundFre(int fund) {
        String fundFre = null;
        switch (fund) {
            case 1:
                fundFre = "25Hz";
                break;
            case 2:
                fundFre = "12.5Hz";
                break;
            case 3:
                fundFre = "6.25Hz";
                break;
            case 4:
                fundFre = "3.125Hz";
                break;
            case 5:
                fundFre = "25Hz 高";
                break;
            case 6:
                fundFre = "50Hz 高";
                break;
            case 7:
                fundFre = "100Hz 高";
                break;
        }
        return fundFre;
    }

    /**
     * 根据表格内容自动调整列宽度
     *
     * @param table
     */
    public void fitTableColumns(JTable myTable, int[] columnIndex) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            for (int i = 0; i < columnIndex.length; i++) {
                if (columnIndex[i] == col) {
                    column.setResizable(false);
                    column.setPreferredWidth(-1);
                    column.setMaxWidth(-1);
                    column.setMinWidth(-1);
                } else {
                    int width = (int) header.getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
                    for (int row = 0; row < rowCount; row++) {
                        int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                        width = Math.max(width, preferedWidth);
                    }
                    header.setResizingColumn(column); // 此行很重要          
                    column.setWidth(width + myTable.getIntercellSpacing().width + 15);
                }
            }
        }
    }

    /**
     * 根据表格内容自动调整列宽度
     *
     * @param table
     */
    public void fitTableColumns(JTable myTable) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration columns = myTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) header.getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要          
            column.setWidth(width + myTable.getIntercellSpacing().width + 15);
        }
    }

    public void setTableCenter(JTable table) {
        // 设置table表头居中
        DefaultTableCellRenderer thr = (DefaultTableCellRenderer) (table.getTableHeader().getDefaultRenderer());
        thr.setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setDefaultRenderer(thr);
        DefaultTableCellRenderer cell;//设置单元格居中
        for (int m = 0; m < table.getRowCount(); m++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                if (j < table.getColumnCount() - 4) {
                    cell = (DefaultTableCellRenderer) table.getCellRenderer(m, j);
                    cell.setHorizontalAlignment(JLabel.CENTER);
                }
//                if (j == 15 || j == 16 || j == 17) {//增加参数需要修改
////                    String s = table.getValueAt(m, j).toString();
////                    if (s.equals("1") || s.equals("2")) {
////                        table.getColumn("定位状态").setCellRenderer(new DownloadProgressBar(1));
////                    } else {
////                        table.getColumn("定位状态").setCellRenderer(new DownloadProgressBar(0));
////                    }
////                    TEMTableRenderer c = (TEMTableRenderer) table.getCellRenderer(m, j);
////                    c.setHorizontalAlignment(JLabel.CENTER);
//                } else {
//                    cell = (DefaultTableCellRenderer) table.getCellRenderer(m, j);
//                    cell.setHorizontalAlignment(JLabel.CENTER);
//                }
            }
        }
    }
    public static String[] DEFAULT_FONT = new String[]{
        "Table.font",
        "TableHeader.font",
        "CheckBox.font",
        "Tree.font",
        "Viewport.font",
        "ProgressBar.font",
        "RadioButtonMenuItem.font",
        "ToolBar.font",
        "ColorChooser.font",
        "ToggleButton.font",
        "Panel.font",
        "TextArea.font",
        "Menu.font",
        "TableHeader.font",
        "TextField.font",
        "OptionPane.font",
        "MenuBar.font",
        "Button.font",
        "Label.font",
        "PasswordField.font",
        "ScrollPane.font",
        "MenuItem.font",
        "ToolTip.font",
        "List.font",
        "EditorPane.font",
        "Table.font",
        "TabbedPane.font",
        "RadioButton.font",
        "CheckBoxMenuItem.font",
        "TextPane.font",
        "PopupMenu.font",
        "TitledBorder.font",
        "ComboBox.font"
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

//        try {
//            // 调整默认字体
//            for (int i = 0; i < DEFAULT_FONT.length; i++) {
//                UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 12));
//            }
//            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
//            //设置此开关量为false即表示关闭之，BeautyEye LNF中默认是true
//            BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
//            BeautyEyeLNFHelper.launchBeautyEyeLNF();
//            UIManager.put("RootPane.setupButtonVisible", false);//设置功能屏蔽
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        } catch (Exception ex) {
////            Logger.getLogger(TEMAcquisitionProMain.class.getName()).log(Level.SEVERE, null, ex);
//        }



        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // 启动加密狗线程
//                File file = new File("temmonitor.dll");//查找temmonitor.dll文件
//                if (!file.exists()) {
//                    JOptionPane.showMessageDialog(null, "没有检测到temmonitor.dll文件，程序以破坏请重新安装！", "TEM检测", JOptionPane.ERROR_MESSAGE);
//                    System.exit(0);
//                }
//                TEMDogTest.startRun();
                //可视化
                TEMProcessingProgramWin frame = new TEMProcessingProgramWin();
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);//最大化显示awt
                frame.setVisible(true);
                frame.requestFocus();//必须放在这个位置
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu AboutMenu;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JButton GPSButton;
    private javax.swing.JMenuItem GPSMenuItem;
    private javax.swing.JDialog GroundOrAirDia;
    private javax.swing.JPanel HandlerPanel;
    private javax.swing.JSplitPane HandlerSplitPane1;
    private javax.swing.JMenuBar MenuBar;
    public javax.swing.JTree SelectedDataTree;
    private javax.swing.JToolBar ToolBar;
    private javax.swing.JMenuItem aboutMenuItem;
    public javax.swing.JTabbedPane dataVisualTabbedPane;
    private javax.swing.JMenuItem deleteNodeMenuItem;
    private javax.swing.JPopupMenu deleteNodePopupMenu;
    public javax.swing.JButton editButton;
    public javax.swing.JMenuItem editMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenuItem;
    public javax.swing.JLabel fileNameLabel;
    private javax.swing.JButton fileOpenButt;
    private javax.swing.JButton flyButton;
    private javax.swing.JButton groundButton;
    public javax.swing.JDialog indermindedProgressBarDia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JButton originalDataButton;
    private javax.swing.JMenuItem originalDataMenuItem;
    public javax.swing.JPanel originalDataPanel;
    public javax.swing.JScrollPane originalScrollPane;
    private javax.swing.JButton pointsParasButton;
    private javax.swing.JMenuItem pointsParasMenuItem;
    public javax.swing.JPanel pointsPositionPanel;
    public javax.swing.JLabel posLabel;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JMenuItem saveGPTMMenuItem;
    private javax.swing.JButton saveImageButton;
    private javax.swing.JMenuItem savePicMenuItem;
    private javax.swing.JMenuItem saveUSFMenuItem;
    public static javax.swing.JPopupMenu setPositionPopUp;
    public javax.swing.JLabel totalFilesLabel;
    private javax.swing.JProgressBar waitingLineProgressBar;
    private javax.swing.JProgressBar waitingProgressBar;
    // End of variables declaration//GEN-END:variables
}
/*-------------------------------------自定义了JTree------------------------------------------------------------*/

class MyTreeRender extends DefaultTreeCellRenderer {

    ImageIcon rootIcon = new ImageIcon(getClass().getResource("/pic/geopen/home.png"));
    ImageIcon folderPIcon = new ImageIcon(getClass().getResource("/pic/geopen/folderP.png"));
    ImageIcon folderLIcon = new ImageIcon(getClass().getResource("/pic/geopen/folderL.png"));
    ImageIcon pointIcon = new ImageIcon(getClass().getResource("/pic/geopen/point.png"));
    ImageIcon lineIcon = new ImageIcon(getClass().getResource("/pic/geopen/line.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        NodeData data = (NodeData) node.getUserObject();
        ImageIcon icon = null;
        switch (data.nodeType) {
            case DBObjectType.ROOT:
                icon = rootIcon;
                break;
            case DBObjectType.FOLDERP:
                icon = folderPIcon;
                break;
            case DBObjectType.FOLDERL:
                icon = folderLIcon;
                break;
            case DBObjectType.PONIT:
                icon = pointIcon;
                break;
            case DBObjectType.LINE:
                icon = lineIcon;
                break;
        }
        this.setIcon(icon);
        return this;
    }
}

/**
 * 封装不同的节点类型
 *
 * @author Administrator
 */
class NodeData {

    public int nodeType;
    public String nodeData;

    public NodeData(int nodeType, String nodeData) {
        this.nodeType = nodeType;
        this.nodeData = nodeData;
    }

    public String toString() {
        return nodeData;
    }
}

/**
 * 接口节点类型常量
 *
 * @author Administrator
 */
interface DBObjectType {

    int ROOT = 0;
    int FOLDERP = 1;
    int FOLDERL = 2;
    int PONIT = 3;
    int LINE = 4;
}
/*-------------------------------------为了让JCheckBox居中 继承了AbstractCellEditor---------------------------------------------------------------*/

class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {

    protected JCheckBox checkBox;

    public CheckBoxCellEditor() {
        checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setBackground(Color.white);
    }

    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {
        if (value instanceof Boolean) {
            checkBox.setSelected(((Boolean) value).booleanValue());
        } else {
            return null;
        }
//        Component c = table.getDefaultRenderer(String.class).getTableCellRendererComponent(table, value, isSelected, false, row, column);
//        if (c != null) {
//            checkBox.setBackground(c.getBackground());
//        }

        return checkBox;
    }

    public Object getCellEditorValue() {
        return Boolean.valueOf(checkBox.isSelected());
    }
}

class CWCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

    Border border = new EmptyBorder(1, 2, 1, 2);

    public CWCheckBoxRenderer() {
        super();
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        if (value instanceof Boolean) {
            setSelected(((Boolean) value).booleanValue());
            setEnabled(table.isCellEditable(row, column));
//            if (column == 17) {
//                table.setRowSelectionAllowed(true);
//                table.setColumnSelectionAllowed(true);
//            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
        } else {
            setSelected(false);
            setEnabled(false);
            //设定背景
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        return this;
    }
}
