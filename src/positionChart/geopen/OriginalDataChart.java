/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author Administrator
 */
public class OriginalDataChart {

    private String fileName = "";
    private int channels = 1;
    public XYSeries xyseries1 = new XYSeries("通道1 Voltage ( mV )");
    public XYSeries xyseries2 = new XYSeries("通道2 Voltage ( mV )");
    public XYSeries xyseries3 = new XYSeries("通道3 Voltage ( mV )");
    public XYPlot xyplot1;
    public XYPlot xyplot2;
    public XYPlot xyplot3;

    public OriginalDataChart(String fileName, int channels) {
        this.fileName = fileName;
        this.channels = channels;
    }

    public JFreeChart createCombinedChart() {
        //添加数据 生成数据集合
        XYDataset xydataset1 = createDatasetChannel(xyseries1);
        XYDataset xydataset2 = createDatasetChannel(xyseries2);
        XYDataset xydataset3 = createDatasetChannel(xyseries3);
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D);
//        java.awt.geom.Rectangle2D.Double double1 = new java.awt.geom.Rectangle2D.Double(-3D, -3D, 6D, 6D);
//        Shape[] cross = DefaultDrawingSupplier.createStandardSeriesShapes();//创建图形集合
//        System.out.println(cross.length);
//        Shape dataPoint = ShapeUtilities.createUpTriangle(3);//三角形
//        Shape dataPoint = ShapeUtilities.createTranslatedShape(double1, 10, 10);//偏移10
        //设定弹出数据样式
        StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
        StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.000"),
                new java.text.DecimalFormat("0.000000"));
        standardxyitemrenderer.setBaseToolTipGenerator(ttg);
        //建立坐标轴
        NumberAxis numberaxis1 = new NumberAxis("Channel_1  Voltage ( mV )");
        numberaxis1.setLabelPaint(Color.BLUE);
        numberaxis1.setTickLabelFont(new Font("", Font.PLAIN, 9));
        numberaxis1.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        xyplot1 = new XYPlot(xydataset1, null, numberaxis1, standardxyitemrenderer);
        StandardXYItemRenderer xylineandshaperenderer1 = (StandardXYItemRenderer) xyplot1.getRenderer();
        xylineandshaperenderer1.setBaseShapesVisible(true);
        xylineandshaperenderer1.setSeriesShape(0, double1);
        xyplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        NumberAxis numberaxis2 = new NumberAxis("Channel_2  Voltage ( mV )");
        numberaxis2.setLabelPaint(Color.BLUE);
        numberaxis2.setTickLabelFont(new Font("", Font.PLAIN, 9));
        numberaxis2.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        xyplot2 = new XYPlot(xydataset2, null, numberaxis2, standardxyitemrenderer);
        StandardXYItemRenderer xylineandshaperenderer2 = (StandardXYItemRenderer) xyplot2.getRenderer();
        xylineandshaperenderer2.setSeriesShape(0, double1);
        xyplot2.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        NumberAxis numberaxis3 = new NumberAxis("Channel_3  Voltage ( mV )");
        numberaxis3.setLabelPaint(Color.BLUE);
        numberaxis3.setTickLabelFont(new Font("", Font.PLAIN, 9));
        numberaxis3.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        xyplot3 = new XYPlot(xydataset3, null, numberaxis3, standardxyitemrenderer);
        StandardXYItemRenderer xylineandshaperenderer3 = (StandardXYItemRenderer) xyplot3.getRenderer();
        xylineandshaperenderer3.setSeriesShape(0, double1);
        xyplot3.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
        //添加时间标记轴
        xyplot1.setDomainCrosshairVisible(true);
        xyplot1.setDomainCrosshairLockedOnData(true);
        xyplot2.setDomainCrosshairVisible(true);
        xyplot2.setDomainCrosshairLockedOnData(true);
        xyplot3.setDomainCrosshairVisible(true);
        xyplot3.setDomainCrosshairLockedOnData(true);
        //添加到综合坐标系下
        NumberAxis domain = new NumberAxis("Time( ms )");
        domain.setLabelPaint(Color.BLUE);
        domain.setTickLabelFont(new Font("", Font.PLAIN, 9));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        CombinedDomainXYPlot combineddomainxyplot = new CombinedDomainXYPlot(domain);
        combineddomainxyplot.setGap(10D);
        if (channels == 1) {
            combineddomainxyplot.add(xyplot1, 1);
        } else if (channels == 2) {
            combineddomainxyplot.add(xyplot1, 1);
            combineddomainxyplot.add(xyplot2, 1);
        } else if (channels == 3) {
            combineddomainxyplot.add(xyplot1, 1);
            combineddomainxyplot.add(xyplot2, 1);
            combineddomainxyplot.add(xyplot3, 1);
        }
        //拖动
        combineddomainxyplot.setRangePannable(true);
        combineddomainxyplot.setDomainPannable(true);

        combineddomainxyplot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart jfreechart = new JFreeChart(fileName, JFreeChart.DEFAULT_TITLE_FONT, combineddomainxyplot, true);
        //设定标题
        TextTitle title = jfreechart.getTitle();
        title.setFont(new Font("", Font.BOLD, 14));
        title.setPaint(Color.BLUE);
        jfreechart.removeLegend();
        //设定背景
        jfreechart.setBackgroundPaint(Color.WHITE);
        return jfreechart;
    }

    public XYDataset createDatasetChannel(XYSeries xyseries) {
        return new XYSeriesCollection(xyseries);
    }

    public JPanel createDemoPanel() {
        JFreeChart jfreechart = createCombinedChart();
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setMouseWheelEnabled(false);
        chartpanel.setPopupMenu(null);//不设定弹出对话框
        chartpanel.setMaximumDrawWidth(10000);
        chartpanel.setMinimumDrawWidth(100);
        chartpanel.setMaximumDrawHeight(10000);
        chartpanel.setMinimumDrawHeight(100);
        chartpanel.setPreferredSize(new Dimension(50, 150));
        return chartpanel;
    }
}
