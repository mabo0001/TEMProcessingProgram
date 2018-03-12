/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import ui.geopen.TEMHandelChannelDataWin;

/**
 * 处理所有测点数据的电压时间曲线值
 *
 * @author Administrator
 */
public class HandlerVolt_TimeCharts implements ChartProgressListener, ChartMouseListener, MouseListener, MouseMotionListener, KeyListener {

    private TEMHandelChannelDataWin win;//为了获得焦点 监听键盘
    public XYItemEntity xyItemEntity;//获得鼠标点击时的数据点
    private ChartRenderingInfo info = null;
    public JFreeChart chart;
    private DefinedChartPanel chartPanel;
    private XYSeriesCollection dataset;//获得当前显示的的数据集合
    private double x = -1;//存储时间点
    private double right_leftDemonMinMax = -1;//区域标记的最大最小值 根据左右截取值不同
    private final CircleFilledDrawer cd = new CircleFilledDrawer(Color.GREEN, new BasicStroke(1.0f), null);//标记数据点的图形样式
    private XYAnnotation bestBid = null;//标记数据点
    private IntervalMarker intervalmarker;//区域标记
    private double value = 0;//单点修改的值
//    private double tempValue = 0;//临时单点修改的值
    private ChartMouseEvent cme;

    public HandlerVolt_TimeCharts(TEMHandelChannelDataWin win) {
        this.win = win;
    }

    /**
     * * *****************获得设置参数****************
     */
    public IntervalMarker getIntervalmarker() {
        return intervalmarker;
    }

    public void setIntervalmarker(IntervalMarker intervalmarker) {
        this.intervalmarker = intervalmarker;
    }
    //获得chart

    public XYSeriesCollection getDataset() {
        return dataset;
    }

    public void setDataset(XYSeriesCollection dataset) {
        this.dataset = dataset;
    }

    public double getRight_leftDemonMinMax() {
        return right_leftDemonMinMax;
    }

    public void setRight_leftDemonMinMax(double right_leftDemonMinMax) {
        this.right_leftDemonMinMax = right_leftDemonMinMax;
    }

    public JFreeChart getChart() {
        return chart;
    }
    //设定bestBid值

    public void setBestBid(XYAnnotation bestBid) {
        this.bestBid = bestBid;
    }
    //获得bestBid

    public XYAnnotation getBestBid() {
        return bestBid;
    }
    //获得cd图形样式

    public CircleFilledDrawer getCd() {
        return cd;
    }
    //获得x值

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public JPanel createDemoPanel(String title, String xLabel, String yLabel, XYDataset dataset) {
        this.dataset = (XYSeriesCollection) dataset;
        JFreeChart chart = createChart(title, xLabel, yLabel, dataset);
        chartPanel = new DefinedChartPanel(chart);
        this.info = chartPanel.getChartRenderingInfo();
        //增加属性
//        chartPanel.setDomainZoomable(true);
//        chartPanel.setRangeZoomable(true);
        chartPanel.addChartMouseListener(this);
        chartPanel.addMouseListener(this);
        chartPanel.addMouseMotionListener(this);
        chartPanel.setPopupMenu(null);
        //设定图标最小值
//        chartPanel.setPreferredSize(new Dimension(200, 200));
//        chartPanel.setMouseWheelEnabled(true);
        return chartPanel;
    }

    public JFreeChart createChart(String titleStr, String xLabel, String yLabel, XYDataset dataset) {
        chart = ChartFactory.createScatterPlot(
                titleStr,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        //设定标题
        TextTitle title = chart.getTitle();
        title.setFont(new Font("楷体_GB2312", Font.BOLD, 16));
        title.setPaint(Color.RED);
        chart.removeLegend();//移除下方颜色标签
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-4D, -4D, 8D, 8D);
        XYPlot xyplot = (XYPlot) chart.getPlot();

        //设定可见不可见
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setBaseItemLabelsVisible(false);
        xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());//弹出数值
        xylineandshaperenderer.setBaseShapesVisible(true);
        xylineandshaperenderer.setSeriesShape(0, double1);
        xylineandshaperenderer.setSeriesLinesVisible(0, true);
        xylineandshaperenderer.setSeriesPaint(0, Color.BLUE);
        xylineandshaperenderer.setUseFillPaint(false);//填充
        //显示样式
        StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.#############"),
                new java.text.DecimalFormat("0.####################"));
//        StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
//                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.000E00"),
//                new java.text.DecimalFormat("0.000E00"));
        xylineandshaperenderer.setBaseToolTipGenerator(ttg);

        //坐标轴
        TEMXLogAxis xAxis = new TEMXLogAxis(xLabel);
        LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
        yAxis.setAutoRangeMinimumSize(1E-20);//自定义最小值 最小的设定
//        xAxis.setAutoRangeMinimumSize(1E-20);//自定义最小值 最小的设定
//        yAxis.setBase(100);
//        LogFormat format = new LogFormat(yAxis.getBase(), "111", "222", true);//自定义坐标轴的显示
//        yAxis.setNumberFormatOverride(format);
//        System.out.println(yAxis.getSmallestValue() + "****");
//        yAxis.setAutoRange(true);
        xyplot.setDomainAxis(xAxis);
        xyplot.setRangeAxis(yAxis);
        //坐标轴只显示数字
        TEMXLogAxis numberaxisX = (TEMXLogAxis) xyplot.getDomainAxis();
        LogarithmicAxis numberaxisY = (LogarithmicAxis) xyplot.getRangeAxis();
        numberaxisX.setAxisLineVisible(false);
        numberaxisX.setTickMarksVisible(false);
        numberaxisX.setLabelPaint(Color.BLUE);
        numberaxisY.setAxisLineVisible(false);
        numberaxisY.setTickMarksVisible(false);
        numberaxisY.setLabelPaint(Color.BLUE);
//        final NumberAxis domainAxis = new LogarithmicAxis("X");
//        final NumberAxis rangeAxis = new LogarithmicAxis("Y");
////        rangeAxis.
//        xyplot.setDomainAxis(domainAxis);
//        xyplot.setRangeAxis(rangeAxis);
//        final CircleDrawer cd = new CircleDrawer(Color.BLACK, new BasicStroke(1.0f), null);
//        final XYAnnotation pointer = new XYDrawableAnnotation(17.3, 453.2, 11, 11, cd);
//        xyplot.addAnnotation(pointer);
//        XYPointerAnnotation pointer = new XYPointerAnnotation(
//                "Best Bid", 17.3, 453.2, 3.0 * Math.PI / 4.0);
//        pointer.setTipRadius(10.0);
//        pointer.setBaseRadius(35.0);
//        pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
//        pointer.setPaint(Color.blue);
//        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_CENTER);
        //设定背景
        xyplot.setBackgroundPaint(Color.WHITE);
        xyplot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
        xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);//纵向线
        xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
//        xyplot.setDomainCrosshairVisible(true);
//        xyplot.setDomainCrosshairStroke(new BasicStroke(1));
//        xyplot.setDomainCrosshairPaint(Color.BLUE);
        xyplot.setDomainCrosshairLockedOnData(true);
//        xyplot.setRangeCrosshairVisible(true);
//        xyplot.setRangeCrosshairPaint(Color.BLUE);//设定颜色
        xyplot.setRangeCrosshairLockedOnData(true);
//        xyplot.setDomainZeroBaselineVisible(true);
//        xyplot.setRangeZeroBaselineVisible(true);
        xyplot.setDomainPannable(true);
        xyplot.setRangePannable(true);
        return chart;
    }

    @Override
    public void chartProgress(ChartProgressEvent cpe) {
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        //获得监听
        win.requestFocus();
        //数据点处理
        if (event.getTrigger().getClickCount() == 1 && event.getTrigger().getButton() == MouseEvent.BUTTON1) {//选择一点 为了拖动
            //标记时间点
            XYPlot xyplot = (XYPlot) chart.getPlot();
            x = xyplot.getDomainCrosshairValue();
            //放在try块中可避免弹出异常提示，目前还没有找到得到XYItemEntity对象的好办法
//            try {
//                xyItemEntity = (XYItemEntity) event.getEntity();
//                int seriesIndex = xyItemEntity.getSeriesIndex();
//                int itemIndex = xyItemEntity.getItem();
//                XYSeriesCollection tsc = (XYSeriesCollection) xyItemEntity.getDataset();
//                XYSeries ts = tsc.getSeries(seriesIndex);
//                value = ts.getY(itemIndex).doubleValue();
//            } catch (Exception ee) {
//            }
//            if (xyItemEntity == null) {
//                return;
//            }
        } else if (event.getTrigger().getClickCount() == 2 && !TEMHandelChannelDataWin.deleteRadioButton.isSelected() && event.getTrigger().getButton() == MouseEvent.BUTTON1) {//选择一点为了定左右截取的时间
            if (win.allFilesUnitCheckBox.isSelected()) {
                //标记时间点
                XYPlot xyplot = (XYPlot) chart.getPlot();
                x = xyplot.getDomainCrosshairValue();
                System.out.println(x);
                double y = xyplot.getRangeCrosshairValue();
                if (bestBid != null) {//清楚之前的marker
                    xyplot.clearAnnotations();
                    xyplot.clearDomainMarkers();
                }
                bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
                xyplot.addAnnotation(bestBid);
                //更新当前文件的时间界限
                win.getFileName_TimeBoundary().put(win.getCurrentFileName(), x);
                //阴影设置
                if (TEMHandelChannelDataWin.rightRadioButton.isSelected()) {
                    right_leftDemonMinMax = dataset.getSeries("时间/电压").getMinX();
                    intervalmarker = new IntervalMarker(right_leftDemonMinMax, x);
                } else if (TEMHandelChannelDataWin.leftRadioButton.isSelected()) {
                    right_leftDemonMinMax = dataset.getSeries("时间/电压").getMaxX();
                    intervalmarker = new IntervalMarker(x, right_leftDemonMinMax);
                }
                intervalmarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
                intervalmarker.setPaint(new Color(200, 200, 255));
                xyplot.addDomainMarker(intervalmarker, Layer.FOREGROUND);
                //绿色竖线界限
                ValueMarker valuemarker1 = new ValueMarker(x, Color.GREEN, new BasicStroke(2.0F));
                xyplot.addDomainMarker(valuemarker1, Layer.FOREGROUND);
                //为每个文件设置时间界限
                int counts = win.getFileName_TimeBoundary().size();
                for (int i = 0; i < counts; i++) {
                    win.getFileName_TimeBoundary().put(win.getFilesNameList().get(i), x);
                }
            } else {
                XYPlot xyplot = (XYPlot) chart.getPlot();
                x = xyplot.getDomainCrosshairValue();
                double y = xyplot.getRangeCrosshairValue();
                if (bestBid != null) {//清楚之前的marker
                    xyplot.clearAnnotations();
                    xyplot.clearDomainMarkers();
                }
                bestBid = new XYDrawableAnnotation(x, y, 11, 11, cd);
                xyplot.addAnnotation(bestBid);
                //更新当前文件的时间界限
                win.getFileName_TimeBoundary().put(win.getCurrentFileName(), x);
                //阴影设置
                if (TEMHandelChannelDataWin.rightRadioButton.isSelected()) {
                    right_leftDemonMinMax = dataset.getSeries("时间/电压").getMinX();
                    intervalmarker = new IntervalMarker(right_leftDemonMinMax, x);
                } else if (TEMHandelChannelDataWin.leftRadioButton.isSelected()) {
                    right_leftDemonMinMax = dataset.getSeries("时间/电压").getMaxX();
                    intervalmarker = new IntervalMarker(x, right_leftDemonMinMax);
                }
                intervalmarker.setLabelOffsetType(LengthAdjustmentType.EXPAND);
                intervalmarker.setPaint(new Color(200, 200, 255));
                xyplot.addDomainMarker(intervalmarker, Layer.FOREGROUND);
                //绿色竖线界限
                ValueMarker valuemarker1 = new ValueMarker(x, Color.GREEN, new BasicStroke(2.0F));
                xyplot.addDomainMarker(valuemarker1, Layer.FOREGROUND);
            }
        } else if (event.getTrigger().getClickCount() == 2 && TEMHandelChannelDataWin.deleteRadioButton.isSelected() && event.getTrigger().getButton() == MouseEvent.BUTTON1) {//当选择了删除数据点
            XYPlot xyplot = (XYPlot) chart.getPlot();
            double x = xyplot.getDomainCrosshairValue();
            double y = xyplot.getRangeCrosshairValue();
            XYSeries xySeries = dataset.getSeries("时间/电压");//获得集合
            xySeries.remove(x);//移除数据点
            //添加删除的数据点组合
            ArrayList<Double> tempXYList = new ArrayList<Double>();
            tempXYList.add(x);
            tempXYList.add(y);
            String fileName = win.getCurrentFileName();
            win.getFileName_DeletedPoints().get(fileName).add(tempXYList);
            //需要从显示的数据点中移除
            ArrayList<Double> tempTimeArrayList = win.getTempTimeList().get(fileName);
            int counts = tempTimeArrayList.size();
            for (int i = 0; i < counts; i++) {
                if (x == (Double) tempTimeArrayList.get(i)) {//当删除的点的时间等于集合中的时间时移除
                    //添加删除的索引
                    win.getTempTimeList().get(fileName).remove(x);
                    win.getTempVoltageList().get(fileName).remove(y);
                    break;
                }
            }
            //更新数据点数
            win.dataPointsLabel.setText("数据点：" + win.getTempTimeList().get(fileName).size());
//            System.out.println(win.getTempTimeList().get(fileName).size());
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX(); // initialized point whenenver mouse is pressed
        int y = e.getY();
        EntityCollection entities = this.info.getEntityCollection();
        ChartMouseEvent cme = new ChartMouseEvent(chart, e, entities
                .getEntity(x, y));
        ChartEntity entity = cme.getEntity();
        if ((entity != null) && (entity instanceof XYItemEntity)) {
            xyItemEntity = (XYItemEntity) entity;
        } else if (!(entity instanceof XYItemEntity)) {
            xyItemEntity = null;
            return;
        }
        if (xyItemEntity == null) {
            return;
        }
        Point pt = e.getPoint();
        XYPlot xy = chart.getXYPlot();
        Rectangle2D dataArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
        Point2D p = chartPanel.translateScreenToJava2D(pt);
        value = xy.getRangeAxis().java2DToValue(p.getY(), dataArea, xy.getRangeAxisEdge());
        double xPos = xy.getDomainAxis().java2DToValue(p.getX(), dataArea, xy.getDomainAxisEdge());
        //添加修改数值的数据点组合
        double itemIndex = xyItemEntity.getItem();
        String fileName = win.getCurrentFileName();
        ArrayList<Double> tempXYList = new ArrayList<Double>();
        tempXYList.add(itemIndex);
        tempXYList.add(value);
        win.getFileName_ChangedPoints().get(fileName).add(tempXYList);
//        //需要从显示的数据点中移除
//        ArrayList<Double> tempVolArrayList = win.getTempTimeList().get(fileName);
//        int counts = tempVolArrayList.size();
//        for (int i = 0; i < counts; i++) {
//            if (x == (Double) tempVolArrayList.get(i)) {//当删除的点的时间等于集合中的时间时移除
//                //添加删除的索引
//                win.getTempTimeList().get(fileName).remove(xPos);
//                win.getTempVoltageList().get(fileName).add(value);
//                break;
//            }
//        }
//        System.out.println(xPos);
//        System.out.println(value);
        chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (xyItemEntity == null) {
            return;
        }
        int seriesIndex = xyItemEntity.getSeriesIndex();
        int itemIndex = xyItemEntity.getItem();
//        int choice = JOptionPane.showConfirmDialog(null, "是否确定修改？", "数据点修改", JOptionPane.OK_OPTION);
//        if (choice == 0) {
//            //改变数值点
//            String fileName = win.getCurrentFileName();
//            XYPlot xy = chart.getXYPlot();
//            win.getTempTimeList().get(fileName).clear();
//            win.getTempVoltageList().get(fileName).clear();
//            XYSeriesCollection tsc = (XYSeriesCollection) xyItemEntity.getDataset();
//            XYSeries ts = tsc.getSeries(seriesIndex);
//            for (int i = 0; i < ts.getItemCount(); i++) {
//                win.getTempTimeList().get(fileName).add(ts.getX(i));
//                win.getTempVoltageList().get(fileName).add(ts.getY(i));
//            }
//        } else {
//            XYSeriesCollection tsc = (XYSeriesCollection) xyItemEntity.getDataset();
//            XYSeries ts = tsc.getSeries(seriesIndex);
//            ts.updateByIndex(itemIndex, value);
//        }
        //改变数值点
        String fileName = win.getCurrentFileName();
        XYPlot xy = chart.getXYPlot();
        win.getTempTimeList().get(fileName).clear();
        win.getTempVoltageList().get(fileName).clear();
        XYSeriesCollection tsc = (XYSeriesCollection) xyItemEntity.getDataset();
        XYSeries ts = tsc.getSeries(seriesIndex);
        for (int i = 0; i < ts.getItemCount(); i++) {
            win.getTempTimeList().get(fileName).add(ts.getX(i));
            win.getTempVoltageList().get(fileName).add(ts.getY(i));
        }
        //退出设置
        xyItemEntity = null;
        chartPanel.setMouseZoomable(true);
        chartPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (xyItemEntity == null) {
            return;
        } else {
            chartPanel.setMouseZoomable(false);
            chartPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        int seriesIndex = xyItemEntity.getSeriesIndex();
        int itemIndex = xyItemEntity.getItem();
        ChartRenderingInfo chartRenderingInfo = chartPanel.getChartRenderingInfo();
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        ValueAxis valueAxis = xyPlot.getRangeAxis();//获得纵坐标
        RectangleEdge rectangleEdge = xyPlot.getRangeAxisEdge();
        Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo().getDataArea();
        int xPos = e.getX();
        int yPos = e.getY();
        Point2D point2D = chartPanel.translateScreenToJava2D(new Point(xPos, yPos));
        double value = valueAxis.java2DToValue(point2D.getY(), rectangle2D, rectangleEdge);
        XYSeriesCollection tsc = (XYSeriesCollection) xyItemEntity.getDataset();
        XYSeries ts = tsc.getSeries(seriesIndex);
        ts.updateByIndex(itemIndex, value);
//        System.out.println(11111);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent evt) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
