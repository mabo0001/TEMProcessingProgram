/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.geopen;

import handler.geopen.TEMSourceData;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author Administrator
 */
public class TEMChartPanle extends JPanel implements ChartProgressListener, ChartMouseListener, MouseMotionListener {

    //主界面
    private TEMProcessingProgramWin frame;
    public ChartPanel chartPanel;
    private JFreeChart chart;
    public static XYSeries series1 = new XYSeries("测点坐标", false);
    public static XYSeries series2 = new XYSeries("Point", false);
    public static XYSeries series3 = new XYSeries("Line", false);
    public static XYSeries series4 = new XYSeries("坐标：", false);
    public static XYSeries series5 = new XYSeries("已选测线", false);
    private int recCount = 0;//记录个数使得鼠标点击的值为当前值<2
    public double x = -1;//坐标值
    public double y = -1;
    //只记录一次 解决第一次点击没有marker问题 选择点选择的时候为true 在主界面修改
    public boolean onlyOnce = true;//只选择一次 只在程序开始的时候 用true
//    private HashMap<Double, XYAnnotation> markers = new HashMap<Double, XYAnnotation>();//添加标记 用用于删除标记

    public TEMChartPanle(TEMProcessingProgramWin frame, String array) {
        super(new BorderLayout());
        this.frame = frame;
        chart = createChart(array);
        chart.addProgressListener(this);
        chartPanel = new ChartPanel(chart);
//        chartPanel.setDomainZoomable(true);
//        chartPanel.setRangeZoomable(true);
        chartPanel.setMaximumDrawWidth(10000);
        chartPanel.setMinimumDrawWidth(100);
        chartPanel.setMaximumDrawHeight(10000);
        chartPanel.setMinimumDrawHeight(100);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMouseZoomable(true);
        chartPanel.addChartMouseListener(this);
//        chartPanel.setZoomTriggerDistance(500);//拖拽多长 产生缩小放大还原
//        //更改坐标标准
//        SquaredXYPlot plot = (SquaredXYPlot) chart.getPlot();
//        if (chartPanel.getWidth() > chartPanel.getHeight()) {
//            plot.setSquaredToRange(true);
//        } else {
//            plot.setSquaredToRange(false);
//        }
//        if (series1.getMinY() == series1.getMaxY()) {
//        }
        addPopupMenu(chartPanel);//增加popupmenu
        javax.swing.border.CompoundBorder compoundborder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder());
        chartPanel.setBorder(compoundborder);
        add(chartPanel);
    }

    /**
     * 增加右键菜单
     *
     * @param chartPanel
     */
    private void addPopupMenu(final ChartPanel chartPanel) {

        Font font = new Font("宋体", Font.PLAIN, 12);
        JPopupMenu menu = new JPopupMenu();
        final JCheckBoxMenuItem LineChoiceItem = new JCheckBoxMenuItem(" 线选择");
        LineChoiceItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/line.png"))); // NOI18N
        final JCheckBoxMenuItem pointChoiceItem = new JCheckBoxMenuItem(" 点选择");
        pointChoiceItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/point.png"))); // NOI18N
        JMenuItem deleteItem = new JMenuItem(" 删除测点");
        deleteItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/delete16.png"))); // NOI18N
        JMenuItem okItem = new JMenuItem(" 确  定");
        okItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/ok16.png"))); // NOI18N
        JMenuItem cancelItem = new JMenuItem(" 取  消");
        cancelItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic/geopen/cancel16.png"))); // NOI18N
        pointChoiceItem.setFont(font);
        LineChoiceItem.setFont(font);
        okItem.setFont(font);
        cancelItem.setFont(font);
        //添加响应
        pointChoiceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pointChoiceItem.isSelected()) {
                    cancleAction();
                    JFreeChart jfreechart = chartPanel.getChart();
                    XYPlot xyplot = (XYPlot) jfreechart.getPlot();
                    series2.clear();
                    series3.clear();
                    series4.clear();
                    series5.clear();
                    xyplot.setDomainCrosshairValue(0);//设定值还原 防止划线时 会记录上次的值
                    xyplot.setRangeCrosshairValue(0);
                    xyplot.clearAnnotations();
                    TEMProcessingProgramWin.flagPointOrLine = 2;
                    LineChoiceItem.setSelected(false);
                } else {
                    cancleAction();
                }
            }
        });
        LineChoiceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (LineChoiceItem.isSelected()) {
                    cancleAction();
                    JFreeChart jfreechart = chartPanel.getChart();
                    XYPlot xyplot = (XYPlot) jfreechart.getPlot();
                    series2.clear();
                    series3.clear();
                    series4.clear();
                    series5.clear();
                    xyplot.setDomainCrosshairValue(0);//设定值还原 防止划线时 会记录上次的值
                    xyplot.setRangeCrosshairValue(0);
                    xyplot.clearAnnotations();
                    TEMProcessingProgramWin.flagPointOrLine = 1;
                    pointChoiceItem.setSelected(false);
                } else {
                    cancleAction();
                }
            }
        });
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (series2.getItemCount() == 0 && series3.getItemCount() == 0 && series4.getItemCount() == 1) {
                    DefaultMutableTreeNode node = searchNode(frame.fileName);
                    if (node != null) {
                        DefaultTreeModel m_model = new DefaultTreeModel(frame.root);
                        TreeNode[] nodes = m_model.getPathToRoot(node);
                        TreePath path = new TreePath(nodes);
                        frame.SelectedDataTree.scrollPathToVisible(path);
                        frame.SelectedDataTree.setSelectionPath(path);
                        frame.updateArray();//更新数据
                    }
                }
            }
        });
        okItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //自定义测线的设置对话框
                if (series2.getItemCount() == 1 || series3.getItemCount() == 1) {
                    JOptionPane.showMessageDialog(frame, "必须选定至少两个数据点！");
                    return;
                }
                if (series2.getItemCount() > 1 || series3.getItemCount() > 1) {//只有在选择好了测线的时候才能弹出
                    LineNameToTreeDialog lineName = new LineNameToTreeDialog(frame, true);
                    lineName.pack();
                    lineName.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "没有选择测线，请选择测线！");
                    return;
                }
                series2.clear();
                series3.clear();
            }
        });
        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancleAction();
                pointChoiceItem.setSelected(false);
                LineChoiceItem.setSelected(false);
            }
        });
        menu.add(LineChoiceItem);
        menu.add(pointChoiceItem);
        menu.addSeparator();
        menu.add(deleteItem);
        menu.addSeparator();
        menu.add(okItem);
        menu.add(cancelItem);
        chartPanel.setPopupMenu(menu);
    }

    public void cancleAction() {
        JFreeChart jfreechart = chartPanel.getChart();
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        series2.clear();
        series3.clear();
        series4.clear();
        series5.clear();
        x = -1;
        y = -1;
        //点选
        xyplot.setDomainCrosshairValue(0);//设定值还原 防止划线时 会记录上次的值
        xyplot.setRangeCrosshairValue(0);
        //清楚所选的点
        xyplot.clearAnnotations();
        TEMProcessingProgramWin.flagPointOrLine = 0;
    }

    public DefaultMutableTreeNode searchNode(String nodeStr) {
        DefaultMutableTreeNode node = null;
        Enumeration e = frame.root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (nodeStr.equals(node.getUserObject().toString())) {
                return node;
            }
        }
        return null;
    }

    private JFreeChart createChart(String array) {
        XYDataset xydataset = createDataset("");//内边框数据
//        //***********************************************等比例*****************************************************************//
        NumberAxis rangeAx = new NumberAxis("Y ( m )");
        NumberAxis domainAx = new NumberAxis("X ( m )");
//        rangeAx.setTickUnit(new NumberTickUnit(0.5));
//        domainAx.setTickUnit(new NumberTickUnit(0.5));
        //create an ErrorbarRenderer
        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
        //don't connect the dots
        //show the points
        r.setBaseShapesVisible(false);
        //create a SquaredXYPlot with above data
        SquaredXYPlot squarePlot = new SquaredXYPlot(xydataset, domainAx, rangeAx, r);
        squarePlot.setOrientation(PlotOrientation.VERTICAL);

//
//        XYPlot squarePlot = new XYPlot(null, domainAx, rangeAx, null) {
//            private static final long serialVersionUID = 1L;
//            @Override
//            public void axisChanged(AxisChangeEvent event) {
//                squareOffGraph();
//                super.axisChanged(event);
//            }
//
//            @Override
//            public void datasetChanged(DatasetChangeEvent event) {
//                squareOffGraph();
//                super.datasetChanged(event);
//            }
//        };




        //define x-axis, and square y-axis to it true是以y轴为主
        squarePlot.setSquaredToRange(false);
        //connect plot and renderer
        r.setPlot(squarePlot);
        r.addChangeListener(squarePlot);
        //create the actual chart
        JFreeChart chart = new JFreeChart("TEM测点坐标", JFreeChart.DEFAULT_TITLE_FONT, squarePlot, false);
        //****************************************************************************************************************//
//        JFreeChart chart = ChartFactory.createXYLineChart("TEM测点坐标", "X ( m )", "Y ( m )", xydataset, PlotOrientation.VERTICAL, true, true, false);
        //设定标题
        TextTitle title = chart.getTitle();
        title.setFont(new Font("楷体_GB2312", Font.BOLD, 18));
        title.setPaint(Color.BLUE);
//        LegendTitle legend = chart.getLegend();
//        legend.setItemFont(new Font("宋体", Font.ITALIC, 14));
        XYPlot xyplot = (XYPlot) chart.getPlot();
        chart.setBackgroundPaint(Color.white);
        chart.removeLegend();//移除下方颜色标签
        //边框大小类别设定
        java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double(-4D, -4D, 8D, 8D);
        java.awt.geom.Rectangle2D.Double double2 = new java.awt.geom.Rectangle2D.Double(-8D, -8D, 16D, 16D);
        java.awt.geom.Rectangle2D.Double double3 = new java.awt.geom.Rectangle2D.Double(-10D, -10D, 20D, 20D);//用于标记选定边框
        java.awt.geom.Rectangle2D.Double double4 = new java.awt.geom.Rectangle2D.Double(-5D, 5D, 10D, 10D);//用于标记选定边框
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //设定可见不可见
        xylineandshaperenderer.setBaseItemLabelsVisible(false);
        xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());//弹出数值
        xylineandshaperenderer.setBaseShapesVisible(true);

        xylineandshaperenderer.setSeriesShape(0, double1);
        xylineandshaperenderer.setSeriesPaint(0, Color.red);
        xylineandshaperenderer.setUseFillPaint(true);//设定为不填充
        xylineandshaperenderer.setSeriesLinesVisible(0, false);

        //Make the shape bigger... 
//        Shape shape = xylineandshaperenderer.getSeriesShape(0);
//        AffineTransform resize = new AffineTransform();
//        double shapeMult = 5; //Making this to 1 or bigger made it way too big 
//        double xFactor = shape.getBounds().width * shapeMult;
//        double yFactor = shape.getBounds().height * shapeMult;
//        resize.scale(xFactor, yFactor);
//        Shape rescaled = resize.createTransformedShape(shape);
//        Shape shape1 = new GeneralPath(rescaled);
//        xylineandshaperenderer.setSeriesShape(0, shape1);


        //点选边框
        xylineandshaperenderer.setSeriesShape(1, double3);
        xylineandshaperenderer.setSeriesPaint(1, Color.BLUE);//测点选线 颜色
        xylineandshaperenderer.setSeriesShapesVisible(1, true);//测点选线
//        xylineandshaperenderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));//边框宽度
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.0f));//测点选线 宽度
        xylineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.0f));//测点选线 宽度
        //线选边框
        xylineandshaperenderer.setSeriesShape(2, double3);
        xylineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.0f));//测点选线 宽度
        xylineandshaperenderer.setSeriesPaint(2, Color.GREEN);//测点选线 颜色
        xylineandshaperenderer.setSeriesShapesVisible(2, true);//自定义测线
        //显示单个已保存的测线 边框 series5
        xylineandshaperenderer.setSeriesShape(4, double3);
        xylineandshaperenderer.setSeriesStroke(4, new BasicStroke(2.0f));//测点选线 宽度
        xylineandshaperenderer.setSeriesPaint(4, Color.CYAN);//测点选线 颜色
        xylineandshaperenderer.setSeriesShapesVisible(2, true);//自定义测线
        //单击选定
        xylineandshaperenderer.setSeriesShape(3, double3);
        xylineandshaperenderer.setSeriesOutlineStroke(3, new BasicStroke(2.0f));
        xylineandshaperenderer.setSeriesStroke(3, new BasicStroke(2.0f));//测点选线 宽度
        xylineandshaperenderer.setSeriesPaint(3, Color.RED);//测点选线 颜色
        xylineandshaperenderer.setSeriesShapesVisible(3, true);//自定义测线

        //外边框
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        setArrayTyoe(xylineandshaperenderer1, xyplot, xydataset, double2, double4);//设定装置类型
        xylineandshaperenderer1.setSeriesPaint(0, Color.RED);//边框颜色
        xylineandshaperenderer1.setSeriesFillPaint(0, null);
        xylineandshaperenderer1.setUseFillPaint(true);//设定为不填充
        xyplot.setRenderer(1, xylineandshaperenderer1);//设定外边框的渲染
        xylineandshaperenderer1.setSeriesLinesVisible(0, false);//设定可见不可见

//        xylineandshaperenderer1.setSeriesPaint(1, Color.BLUE);//测点选线 颜色
//        xylineandshaperenderer1.setSeriesShapesVisible(1, false);//测点选线
//        xylineandshaperenderer1.setSeriesPaint(2, Color.GREEN);//测点选线 颜色
//        xylineandshaperenderer1.setSeriesShapesVisible(2, false);//自定义测线
        //设定背景
        xyplot.setBackgroundPaint(Color.WHITE);
//        xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));//设定坐标轴偏移
        xyplot.setDomainGridlinesVisible(false);
        xyplot.setRangeGridlinesVisible(false);
//        xyplot.setDomainGridlinePaint(Color.lightGray);//纵向线
//        xyplot.setDomainGridlineStroke(new BasicStroke(0.1f));
//        xyplot.setRangeGridlinePaint(Color.lightGray);
//        xyplot.setRangeGridlineStroke(new BasicStroke(0.1f));
        xyplot.setDomainCrosshairVisible(false);
        xyplot.setDomainCrosshairLockedOnData(true);
        xyplot.setRangeCrosshairVisible(false);
        xyplot.setRangeCrosshairLockedOnData(true);
        xyplot.setDomainZeroBaselineVisible(false);
        xyplot.setRangeZeroBaselineVisible(false);
        //ctrl 拖动
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        //等比例尺

        //坐标轴只显示数字
        NumberAxis numberaxisX = (NumberAxis) xyplot.getDomainAxis();
        NumberAxis numberaxisY = (NumberAxis) xyplot.getRangeAxis();
        numberaxisX.setAutoRangeIncludesZero(true);
        numberaxisX.setAxisLineVisible(false);
//        numberaxisX.setTickMarksVisible(false);
        numberaxisX.setTickMarkPaint(Color.LIGHT_GRAY);
//        numberaxisX.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
//        numberaxisX.setTickMarkInsideLength(1f);
        numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 10));
        numberaxisX.setLabelPaint(Color.BLUE);
        numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));


        //设定x轴和y轴的坐标轴显示样式
        final DecimalFormat format = new DecimalFormat("#");
        format.setMaximumFractionDigits(2);//消除逗号并且设定最大精度
        numberaxisX.setNumberFormatOverride(format);//去掉逗号
        numberaxisY.setNumberFormatOverride(format);//去掉逗号
//        numberaxisY.setNumberFormatOverride(new NumberFormat() {//等坐标长度
//            @Override
//            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
//                StringBuffer formattedValue = new StringBuffer();
////                    DecimalFormat format = new DecimalFormat("#");
////                    format.setMaximumFractionDigits(10);//消除逗号并且设定最大精度
////                    double tempvalue = number;
//                double tempvalue = Double.parseDouble(format.format(number));
//                String strValue = String.valueOf(tempvalue);
//                if (strValue.length() < 8) {
//                    for (int i = 0; i <= 8 - strValue.length(); i++) {
//                        formattedValue = formattedValue.append("  ");
//                    }
//                }
//                formattedValue = formattedValue.append(tempvalue);
//                return formattedValue;
//            }
//
//            @Override
//            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
//                return null;
//            }
//
//            @Override
//            public Number parse(String source, ParsePosition parsePosition) {
//                return null;
//            }
//        });
        numberaxisY.setAxisLineVisible(false);
//        numberaxisY.setTickMarksVisible(false);
        numberaxisY.setTickMarkPaint(Color.LIGHT_GRAY);
//        numberaxisY.setTickMarkInsideLength(1f);
//        numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
        numberaxisY.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        numberaxisY.setLabelPaint(Color.BLUE);
        numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//        numberaxisX.setStandardTickUnits(NumberAxis.createStandardTickUnits());//设定x坐标为整数
//        numberaxisY.setStandardTickUnits(NumberAxis.createStandardTickUnits());//设定x坐标为整数
        //设定轴的现实位数
//        NumberFormat numformatter = NumberFormat.getInstance(); // 创建一个数字格式格式对象  
//        numformatter.setMaximumFractionDigits(1);   // 设置数值小数点后最多2位  
//        numformatter.setMinimumFractionDigits(1);   // 设置数值小数点后最少2位  
//        numberaxisY.setTickUnit(new NumberTickUnit(1, numformatter));
//        numberaxisX.setTickUnit(new NumberTickUnit(1, numformatter));
//        numberaxisY.setTickUnit(new NumberTickUnit(0.5));
//        numberaxisX.setTickUnit(new NumberTickUnit(0.5));
        return chart;
    }

    /**
     * 设定装置类型
     *
     * @param xylineandshaperenderer1
     * @param xyplot
     * @param xydataset
     * @param double2
     * @param double4
     */
    public void setArrayTyoe(XYLineAndShapeRenderer xylineandshaperenderer1, XYPlot xyplot, XYDataset xydataset, java.awt.geom.Rectangle2D.Double double2, java.awt.geom.Rectangle2D.Double double4) {
        if (TEMSourceData.Array[0].toString().equalsIgnoreCase("中心回线")) {
//            xyplot.setOutlinePaint(Color.black);
//            xyplot.setOutlineStroke(new BasicStroke(1.0f));
            xyplot.setDataset(1, xydataset);//数据和内边框的数据相同
            xylineandshaperenderer1.setSeriesShape(0, double2);
//            NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
//            NumberAxis y = (NumberAxis) xyplot.getRangeAxis();
//            if (series1.getMinX() != 0 && series1.getMaxX() != 0) {
//                if (series1.getMinX() < 0 && series1.getMaxX() < 0) {
//                    x.setRange(series1.getMinX() * 2, 0);
//                } else if (series1.getMaxX() > 0) {
//                    if (series1.getMinX() == series1.getMaxX()) {
//                        x.setRange(0, series1.getMinX() * 2);
//                        System.out.println(11111);
//                    } else {
//                        x.setRange(series1.getMinX(), series1.getMaxX());
//                    }
//                }
//            }
//            NumberAxis y = (NumberAxis) xyplot.getRangeAxis();
//            if (series1.getMinY() != 0 && series1.getMaxY() != 0) {
//                if (series1.getMinY() < 0 && series1.getMaxY() < 0) {
//                    y.setRange(series1.getMinY() * 2, 0);
//                } else if (series1.getMaxY() > 0) {
//                    if (series1.getMinY() == series1.getMaxY()) {
//                        y.setRange(0, series1.getMinY() * 2);
//                    } else {
//                        y.setRange(series1.getMinY(), series1.getMaxY());
//                    }
//                }
//            }
            //设定显示范围
            NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
            NumberAxis y = (NumberAxis) xyplot.getRangeAxis();
//            if (series1.getMinX() != 0 && series1.getMaxX() != 0) {
//                if (series1.getMinX() < 0 && series1.getMaxX() < 0) {
//                    x.setRange(series1.getMinX() * 2, 0);
//                } else if (series1.getMaxX() > 0) {
//                    if (series1.getMinX() == series1.getMaxX()) {
//                        x.setRange(0, series1.getMinX() * 2);
//                    } else {
//                        x.setRange(series1.getMinX(), series1.getMaxX());
//                    }
//                }
//            }
//            if (series1.getMinY() != 0 && series1.getMaxY() != 0) {
//                if (series1.getMinY() < 0 && series1.getMaxY() < 0) {
//                    y.setRange(series1.getMinY() * 2, 0);
//                } else if (series1.getMaxY() > 0) {
//                    if (series1.getMinY() == series1.getMaxY()) {
//                        y.setRange(0, series1.getMinY() * 2);
//                    } else {
//                        y.setRange(series1.getMinY(), series1.getMaxY());
//                    }
//                }
//            }
//            squareOffGraph(x, y);
            x.setUpperBound(series1.getMaxX());
            x.setLowerBound(series1.getMinX());
            y.setUpperBound(series1.getMaxY());
            y.setLowerBound(series1.getMinY());
            x.setFixedAutoRange(series1.getMaxX() - series1.getMinX());
            y.setFixedAutoRange(series1.getMaxY() - series1.getMinY());
        } else if (TEMSourceData.Array[0].toString().equalsIgnoreCase("重叠回线")) {
//            xyplot.setOutlinePaint(Color.black);
//            xyplot.setOutlineStroke(new BasicStroke(1.0f));
            xyplot.setDataset(1, xydataset);//数据和内边框的数据相同
//            xylineandshaperenderer1.setSeriesShape(0, double4);//添加外部相邻的一个线框
            NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
            NumberAxis y = (NumberAxis) xyplot.getRangeAxis();
            x.setLowerBound(series1.getMinX());
            x.setUpperBound(series1.getMaxX());
            y.setLowerBound(series1.getMinY());
            y.setUpperBound(series1.getMaxY());
            x.setFixedAutoRange(series1.getMaxX() - series1.getMinX());
            y.setFixedAutoRange(series1.getMaxY() - series1.getMinY());
        } else if (TEMSourceData.Array[0].toString().equalsIgnoreCase("大定源")) {
//            xyplot.setOutlinePaint(Color.RED);
//            xyplot.setOutlineStroke(new BasicStroke(2.0f));
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
            double startX1 = (Double) TEMSourceData.trCenterX[0] - (Double) TEMSourceData.trLength[0] / 2;
            double startY1 = (Double) TEMSourceData.trCenterY[0] - (Double) TEMSourceData.trWidth[0] / 2;
            double endX1 = (Double) TEMSourceData.trCenterX[0] - (Double) TEMSourceData.trLength[0] / 2;
            double endY1 = (Double) TEMSourceData.trCenterY[0] + (Double) TEMSourceData.trWidth[0] / 2;
            double endX2 = (Double) TEMSourceData.trCenterX[0] + (Double) TEMSourceData.trLength[0] / 2;
            double endY2 = (Double) TEMSourceData.trCenterY[0] + (Double) TEMSourceData.trWidth[0] / 2;
            double startX2 = (Double) TEMSourceData.trCenterX[0] + (Double) TEMSourceData.trLength[0] / 2;
            double startY2 = (Double) TEMSourceData.trCenterY[0] - (Double) TEMSourceData.trWidth[0] / 2;
            XYPolygonAnnotation xypolygonannotation = new XYPolygonAnnotation(new double[]{
                startX1, startY1, endX1, endY1, endX2, endY2, startX2, startY2
            }, new BasicStroke(2.0f), Color.MAGENTA, null);
            xylineandshaperenderer.addAnnotation(xypolygonannotation, Layer.BACKGROUND);
            //设定显示范围
            NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
            NumberAxis y = (NumberAxis) xyplot.getRangeAxis();
//            if ((Double) TEMSourceData.trCenterX[0] < 0 && (Double) TEMSourceData.trCenterY[0] > 0) {
//                x.setRangeType(RangeType.NEGATIVE);
//                y.setRangeType(RangeType.POSITIVE);
////                x.setInverted(true);
////                y.setInverted(false);
//            } else if ((Double) TEMSourceData.trCenterX[0] > 0 && (Double) TEMSourceData.trCenterY[0] < 0) {
//                x.setRangeType(RangeType.POSITIVE);
//                y.setRangeType(RangeType.NEGATIVE);
////                x.setInverted(false);
////                y.setInverted(true);
//            } else if ((Double) TEMSourceData.trCenterX[0] < 0 && (Double) TEMSourceData.trCenterY[0] < 0) {
//                x.setRangeType(RangeType.NEGATIVE);
//                y.setRangeType(RangeType.NEGATIVE);
////                x.setInverted(true);
////                y.setInverted(true);
//            } else {
//                x.setRangeType(RangeType.POSITIVE);
//                y.setRangeType(RangeType.POSITIVE);
////                x.setInverted(false);
////                y.setInverted(false);
//            }
//            x.setAutoRangeMinimumSize((Double) TEMSourceData.trWidth[0]);
//            y.setAutoRangeMinimumSize((Double) TEMSourceData.trLength[0]);
//            xyplot.setDataset(1, xydataset);//数据和内边框的数据相同
//            x.setRange(startX1, startX2);
//            y.setRange(startY1, endY1);
//            x.setFixedAutoRange((Double) TEMSourceData.trLength[0]);//防止只有一个坐标
//            x.setAutoRangeMinimumSize((Double) TEMSourceData.trLength[0]);
//            xypolygonannotation.setToolTipText("大定源");
//            x.setFixedAutoRange(Math.abs(startX2) + 100);//防止只有一个坐标
            //设定显示范围
//            x.setLowerBound(series1.getMinX());
//            x.setUpperBound(series1.getMaxX());
//            y.setLowerBound(series1.getMinY());
//            y.setUpperBound(series1.getMaxY());
//            x.setFixedAutoRange((series1.getMaxX() - series1.getMinX()));
//            y.setFixedAutoRange(series1.getMaxY() - series1.getMinY());
//            x.setUpperBound((Double) TEMSourceData.trCenterX[0] + (Double) TEMSourceData.trLength[0] / 2);
//            x.setLowerBound((Double) TEMSourceData.trCenterX[0] - (Double) TEMSourceData.trLength[0] / 2);
//            y.setUpperBound((Double) TEMSourceData.trCenterY[0] + (Double) TEMSourceData.trWidth[0] / 2);
//            y.setLowerBound((Double) TEMSourceData.trCenterY[0] - (Double) TEMSourceData.trWidth[0] / 2);
//            if (frame.paraDialog.outBourndary == false) {
//                x.setUpperBound(series1.getMaxX());
//                x.setLowerBound(series1.getMinX());
//                y.setUpperBound(series1.getMaxY());
//                y.setLowerBound(series1.getMinY());
//                x.setFixedAutoRange(series1.getMaxX() - series1.getMinX());
//                y.setFixedAutoRange(series1.getMaxY() - series1.getMinY());
//            } else {
//            }
//            x.setRange(new Range(startX1, startX2));
//            y.setRange(new Range(startY1, endY1));
//            x.setUpperBound(startX2);
//            x.setLowerBound(startX1);
//            y.setUpperBound(endY1);
//            y.setLowerBound(startY1);
//            x.setFixedAutoRange(startX2 - startX1);
//            y.setFixedAutoRange(endY1 - startY1);
//            System.out.println((Double) TEMSourceData.trLength[0]);
//            System.out.println(series1.getMaxX() - series1.getMinX());
//            x.setRange(new Range(series1.getMinX(), series1.getMaxX()));
//            y.setRange(new Range(series1.getMinY(), series1.getMaxY()));

            x.setUpperBound(series1.getMaxX());
            x.setLowerBound(series1.getMinX());
            y.setUpperBound(series1.getMaxY());
            y.setLowerBound(series1.getMinY());
            x.setFixedAutoRange(series1.getMaxX() - series1.getMinX());
            y.setFixedAutoRange(series1.getMaxY() - series1.getMinY());

        }
    }

    public void setCrossHairIsEnbale(boolean isAble, XYPlot xyplot) {
        if (isAble == true) {
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairLockedOnData(true);
            xyplot.setRangeCrosshairVisible(true);
            xyplot.setRangeCrosshairLockedOnData(true);
            xyplot.setDomainZeroBaselineVisible(true);
            xyplot.setRangeZeroBaselineVisible(true);
        } else {
            xyplot.setDomainCrosshairVisible(false);
            xyplot.setDomainCrosshairLockedOnData(false);
            xyplot.setRangeCrosshairVisible(false);
            xyplot.setRangeCrosshairLockedOnData(false);
            xyplot.setDomainZeroBaselineVisible(false);
            xyplot.setRangeZeroBaselineVisible(false);
        }
    }

    private XYDataset createDataset(String str) {
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(series1);
        xyseriescollection.addSeries(series2);
        xyseriescollection.addSeries(series3);
        xyseriescollection.addSeries(series4);
        xyseriescollection.addSeries(series5);
        return xyseriescollection;
    }

    public void chartProgress(ChartProgressEvent chartprogressevent) {//recCount 小于2 为了禁止不断的循环
        if (chartPanel != null && recCount < 2) {
            JFreeChart jfreechart = chartPanel.getChart();
            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
            x = xyplot.getDomainCrosshairValue();
            y = xyplot.getRangeCrosshairValue();
            if (TEMProcessingProgramWin.flagPointOrLine == 2) {//点选
                series4.clear();
                if (recCount == 1) {//鼠标点击一次
                    int count = series2.getItemCount();//必须这么设置 series会自动改变 所以要把此值固定
                    for (int i = 0; i < count; i++) {
                        if (x == series2.getX(i).doubleValue() && y == series2.getY(i).doubleValue()) {
                            series2.remove(i);
                            recCount = 3;//必须设定reccount才能跳出来
                            return;
//                            //移除标记
//                            Double key = x * 1.0001 + y;//防止纵横相加相等的情况
//                            Double key1 = x * 1.0001 + y + 0.0000001;
//                            XYDrawableAnnotation xyAnnotation = (XYDrawableAnnotation) markers.get(key);
//                            XYDrawableAnnotation xyAnnotation1 = (XYDrawableAnnotation) markers.get(key1);
//                            if (xyAnnotation != null) {
//                                xyplot.removeAnnotation(xyAnnotation);
//                            }
//                            if (xyAnnotation1 != null) {
//                                xyplot.removeAnnotation(xyAnnotation1);
//                            }
                        }
                    }
//                    final CircleDrawer cd = new CircleDrawer(Color.BLUE, new BasicStroke(1.0f), null);
//                    final XYDrawableAnnotation bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
//                    xyplot.addAnnotation(bestBid);
//                    Double xyMarker = x * 1.0001 + y;
//                    markers.put(xyMarker, bestBid);
                    if (onlyOnce == false) {
                        series2.add(x, y);//增加数据
                    }
                    if (onlyOnce == true) {
//                    final CircleDrawer cd = new CircleDrawer(Color.BLUE, new BasicStroke(1.0f), null);
//                    final XYAnnotation bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
//                    xyplot.addAnnotation(bestBid);
//                    Double xyMarker = x * 1.0001 + y + 0.0000001;//第一个点
//                    markers.put(xyMarker, bestBid);
                        onlyOnce = false;
                    }
                    //显示文件名
                    setFrameLableText(x, y);
                }
                recCount++;
            } else if (TEMProcessingProgramWin.flagPointOrLine == 1) {//线选
//                final CircleDrawer cd = new CircleDrawer(Color.GREEN, new BasicStroke(1.0f), null);
//                final XYAnnotation bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
//                xyplot.addAnnotation(bestBid);
                series4.clear();
                if (recCount == 1) {//鼠标点击一次
                    int count = series3.getItemCount();//必须这么设置 series会自动改变 所以要把此值固定
                    for (int i = 0; i < count; i++) {
                        if (x == series3.getX(i).doubleValue() && y == series3.getY(i).doubleValue()) {
                            series3.remove(i);
                            recCount = 3;//必须设定reccount才能跳出来
                            return;
                        }
                    }
                    if (onlyOnce == false) {
                        if (count == 2) {
                            series3.remove(0);
                        }
                        series3.add(x, y);//增加数据
                    }
                    if (onlyOnce == true) {
                        onlyOnce = false;
                    }
                }
                recCount++;
            } else if (TEMProcessingProgramWin.flagPointOrLine == 0) {//单击
                if (recCount == 1) {//鼠标点击一次
                    series4.clear();
                    if (onlyOnce == false) {
                        series4.add(x, y);//增加数据
                        setFrameLableText(x, y);
                    }
                    if (onlyOnce == true) {
                        onlyOnce = false;
                    }
                }
                recCount++;
            }
        } else {
            return;
        }
    }

    public void setFrameLableText(double x, double y) {
        ArrayList<Double> xy = new ArrayList<Double>();
        xy.add(x);
        xy.add(y);
        frame.fileName = TEMSourceData.xy_fileName.get(xy);//fileName 必须为frame.fileName 因为在单点反演中需要
        frame.fileNameLabel.setText("当前文件名：" + frame.fileName);//显示数据点数据
        frame.posLabel.setText("当前数据点坐标：" + "( x = " + x + " , y = " + y + " )");//显示数据点数据
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        int clickCount = event.getTrigger().getClickCount();
        if (clickCount == 1) {
            frame.requestFocus();//获得焦点 响应键盘
            recCount = 0;//用changprogressevent
//            if (TEMProcessingProgramWin.flagPointOrLine == false) {//只能选两个点
//                series3.clear();
////                JFreeChart jfreechart = chartPanel.getChart();
////                XYPlot xyplot = (XYPlot) jfreechart.getPlot();
////                xyplot.clearAnnotations();
//            }
        } else if (clickCount == 2) {//获得单个点的信息 
            //只存储在series2中
            TEMProcessingProgramWin.flagPointOrLine = 0;
            if (TEMProcessingProgramWin.flagPointOrLine == 0 && series4.getItemCount() != 0) {
                List temp = series4.getItems();
                double x = series4.getX(temp.size() - 1).doubleValue();
                double y = series4.getY(temp.size() - 1).doubleValue();
                ArrayList<Double> xy = new ArrayList<Double>();
                xy.add(x);
                xy.add(y);
                frame.fileName = TEMSourceData.xy_fileName.get(xy);//fileName 必须为frame.fileName 因为在单点反演中需要
                frame.showingSingleData(frame.fileName);//显示数据点数据
            }
            //获得点的信息
            if (series2.getItemCount() != 0) {
                TEMProcessingProgramWin.flagPointOrLine = 2;
                series2.clear();
            }
            if (series3.getItemCount() != 0) {
                TEMProcessingProgramWin.flagPointOrLine = 1;
                series3.clear();
            }
//            JFreeChart jfreechart = chartPanel.getChart();
//            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
//            xyplot.clearAnnotations();
        }
//        XYPlot xyplot = (XYPlot) chartPanel.getChart().getPlot();
//        int i = event.getTrigger().getX();
//        int j = event.getTrigger().getY();
//        Point2D point2d = chartPanel.translateScreenToJava2D(new Point(i, j));
//        ChartRenderingInfo chartrenderinginfo = chartPanel.getChartRenderingInfo();
//        java.awt.geom.Rectangle2D rectangle2d = chartrenderinginfo.getPlotInfo().getDataArea();
//        double d = xyplot.getDomainAxis().java2DToValue(point2d.getX(), rectangle2d, xyplot.getDomainAxisEdge());
//        double d1 = xyplot.getRangeAxis().java2DToValue(point2d.getY(), rectangle2d, xyplot.getRangeAxisEdge());
//        ValueAxis valueaxis = xyplot.getDomainAxis();
//        ValueAxis valueaxis1 = xyplot.getRangeAxis();
//        double d2 = valueaxis.valueToJava2D(d, rectangle2d, xyplot.getDomainAxisEdge());
//        double d3 = valueaxis1.valueToJava2D(d1, rectangle2d, xyplot.getRangeAxisEdge());
//        Point point = chartPanel.translateJava2DToScreen(new java.awt.geom.Point2D.Double(d2, d3));
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}
