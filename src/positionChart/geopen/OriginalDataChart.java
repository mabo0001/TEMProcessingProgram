/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author Administrator
 */
public class OriginalDataChart implements ChartMouseListener, MouseListener, MouseMotionListener {

    private String fileName = "";
    private int channels = 1;
    public XYSeries xyseries1 = new XYSeries("通道_1");
    public XYSeries xyseries2 = new XYSeries("通道_2");
    public XYSeries xyseries3 = new XYSeries("通道_3");
    public XYPlot xyplot1;
    public XYPlot xyplot2;
    public XYPlot xyplot3;
    DefinedChartPanel chartPanel;

    public OriginalDataChart(String fileName, int channels) {
        this.fileName = fileName;
        this.channels = channels;
    }

    public DefinedChartPanel createDemoPanel(String title, String xLabel, String yLabel, Dimension deDimension, XYDataset dataset) {
        JFreeChart chart = createChart(title, xLabel, yLabel, yLabel, dataset);
        chartPanel = new DefinedChartPanel(chart);
        //添加监听事件
        chartPanel.addChartMouseListener(this);
        //增加属性
//        chartPanel.setDomainZoomable(true);
//        chartPanel.setRangeZoomable(true);
        //设定显示时间
        chartPanel.setDismissDelay(100000000);
        //设定图标最小值
//        chartPanel.setPreferredSize(deDimension);
        //防止图形变形
        chartPanel.setMaximumDrawWidth(10000);
        chartPanel.setMinimumDrawWidth(100);
        chartPanel.setMaximumDrawHeight(10000);
        chartPanel.setMinimumDrawHeight(50);
        chartPanel.setPreferredSize(deDimension);
        chartPanel.setMouseWheelEnabled(false);
        //
        chartPanel.getPopupMenu().removeAll();
        return chartPanel;
    }

    public JFreeChart createChart(String titleStr, String xLabel, String yLabel, String chartVersion, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                //        JFreeChart chart = ChartFactory.createScatterPlot(
                titleStr,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,//lengend
                true,
                false);
        XYPlot xyplot = (XYPlot) chart.getPlot();
        //设定标题
        TextTitle title = chart.getTitle();
        title.setHorizontalAlignment(HorizontalAlignment.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 10));
        title.setPaint(Color.BLUE);
        //设定背景
        chart.setBackgroundPaint(Color.WHITE);
        LegendTitle legendTitle = new LegendTitle(xyplot);
        legendTitle.setItemFont(new Font("楷体", Font.PLAIN, 10));
        BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
        blockcontainer.setFrame(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
        blockcontainer.setMargin(0, 0, 30, 0);
//        LabelBlock labelblock = new LabelBlock("Legend Items:", new Font("SansSerif", 1, 12));
//        labelblock.setPadding(5D, 5D, 5D, 5D);
//        blockcontainer.add(labelblock, RectangleEdge.TOP);
//        LabelBlock labelblock1 = new LabelBlock("Source: http://www.jfree.org");
//        labelblock1.setPadding(8D, 20D, 2D, 5D);
//        blockcontainer.add(labelblock1, RectangleEdge.BOTTOM);
        BlockContainer blockcontainer1 = legendTitle.getItemContainer();
//        blockcontainer1.setPadding(2D, 10D, 5D, 2D);
        blockcontainer.add(blockcontainer1);
        legendTitle.setWrapper(blockcontainer);
        legendTitle.setPosition(RectangleEdge.RIGHT);
//        legendtitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legendTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        chart.addSubtitle(legendTitle);
//        legendTitle.setBackgroundPaint(new Color(200, 200, 255, 200));
//        legendTitle.setFrame(new BlockBorder(new Color(200, 200, 255, 0)));
//        legendTitle.setPosition(RectangleEdge.BOTTOM);
//        XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.35, legendTitle,
//                RectangleAnchor.BOTTOM_RIGHT);
//        ta.setMaxWidth(0.43);//只显示第几次这几个字
//        xyplot.addAnnotation(ta);
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
//        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-2D, -2D, 4D, 4D);
//            //设定可见不可见
//            xylineandshaperenderer.setBaseItemLabelsVisible(false);
////            xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());//弹出数值
        xylineandshaperenderer.setBaseShapesVisible(true);
//       xylineandshaperenderer.setShape(new Rectangle(2, 2));
//            xylineandshaperenderer.setSeriesShape(0, double1);
//            xylineandshaperenderer.setSeriesLinesVisible(0, true);
//            xylineandshaperenderer.setSeriesPaint(0, Color.RED);
//            xylineandshaperenderer.setUseFillPaint(false);//填充
        xylineandshaperenderer.setSeriesShape(0, ShapeUtilities.createDownTriangle(2));
        xylineandshaperenderer.setSeriesShape(1, ShapeUtilities.createRegularCross(2, 2));
        xylineandshaperenderer.setSeriesShape(2, ShapeUtilities.createUpTriangle(2));
        //坐标轴
        NumberAxis xAxis = new NumberAxis(xLabel);
        NumberAxis yAxis = new NumberAxis(yLabel);
        xyplot.setDomainAxis(xAxis);
        xyplot.setRangeAxis(yAxis);
//            xAxis.setFixedAutoRange(30000D);//设定最小显示
        //坐标轴
        NumberAxis numberaxisX = (NumberAxis) xyplot.getDomainAxis();
        NumberAxis numberaxisY = (NumberAxis) xyplot.getRangeAxis();
//            numberaxisX.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        //设定x轴和y轴的坐标轴显示样式
        final DecimalFormat format = new DecimalFormat("#");
        format.setMaximumFractionDigits(10);//消除逗号并且设定最大精度
        numberaxisX.setNumberFormatOverride(format);//去掉逗号
        numberaxisY.setNumberFormatOverride(new NumberFormat() {//等坐标长度
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                StringBuffer formattedValue = new StringBuffer();
//                    DecimalFormat format = new DecimalFormat("#");
//                    format.setMaximumFractionDigits(10);//消除逗号并且设定最大精度
//                    double tempvalue = number;
                double tempvalue = Double.parseDouble(format.format(number));
                String strValue = String.valueOf(tempvalue);
                if (strValue.length() < 8) {
                    for (int i = 0; i <= 8 - strValue.length(); i++) {
                        formattedValue = formattedValue.append("  ");
                    }
                }
                formattedValue = formattedValue.append(tempvalue);
                return formattedValue;
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return null;
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        });
        numberaxisX.setAxisLineVisible(true);
        numberaxisX.setTickMarksVisible(true);
        numberaxisX.setLabelPaint(Color.BLUE);
        numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 9));
        numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));

//        numberaxisX.setInverted(true);//反向
        numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 9));
        numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        numberaxisY.setAxisLineVisible(false);
        numberaxisY.setTickMarksVisible(true);
        numberaxisY.setLabelPaint(Color.RED);
        numberaxisY.setTickLabelPaint(Color.RED);
//            setOffRight(numberaxisY);
//            numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
        //设定背景
//              xyplot.setMaximumLabelWidth
        xyplot.setBackgroundPaint(Color.WHITE);
        xyplot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));//坐标标签设定显示位置
//            xyplot.setDomainGridlinesVisible(false);//纵向线
//            xyplot.setRangeGridlinesVisible(false);
//            xyplot.setDomainGridlineStroke(new BasicStroke(1f));
//            xyplot.setRangeGridlineStroke(new BasicStroke(1f));
        xyplot.setDomainGridlinePaint(Color.lightGray);//纵向线
        xyplot.setRangeGridlinePaint(Color.lightGray);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairPaint(Color.BLUE);
        xyplot.setDomainCrosshairLockedOnData(true);
        xyplot.setRangeCrosshairVisible(true);
        xyplot.setRangeCrosshairPaint(Color.BLUE);//设定颜色
        xyplot.setRangeCrosshairLockedOnData(true);
        xyplot.setDomainZeroBaselineVisible(false);
        xyplot.setRangeZeroBaselineVisible(false);
        xyplot.setDomainPannable(true);//不可ctrl+拖动
        xyplot.setRangePannable(true);
        return chart;
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
        chartpanel.setPopupMenu(null);//不设定弹出对话框
        chartpanel.setMaximumDrawWidth(10000);
        chartpanel.setMinimumDrawWidth(100);
        chartpanel.setMaximumDrawHeight(10000);
        chartpanel.setMinimumDrawHeight(100);
        chartpanel.setPreferredSize(new Dimension(50, 150));
        chartpanel.setMouseWheelEnabled(false);
        return chartpanel;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        addLegendItemListner(event);
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
    }
    /*
     * 添加图例鼠标相应
     */

    private void addLegendItemListner(ChartMouseEvent event) {
        org.jfree.chart.entity.ChartEntity chartentity = event.getEntity();
        MouseEvent me = event.getTrigger();
//        System.out.println(me.getClickCount() + "鼠标打开/关闭" + chartentity);
        if (chartentity != null && (chartentity instanceof LegendItemEntity)) {
            JFreeChart tmpJFreeChart = event.getChart();
            LegendItemEntity legenditementity = (LegendItemEntity) chartentity;
            Comparable comparable = legenditementity.getSeriesKey();
            String leng = comparable.toString();
            XYSeriesCollection xydataset = (XYSeriesCollection) legenditementity.getDataset();
            XYLineAndShapeRenderer lineAndShapeRenderer = null;
            int index = Integer.parseInt(leng.split("_")[1]);
            XYPlot xyplot = (XYPlot) (tmpJFreeChart.getPlot());
            index = xydataset.getSeriesIndex(comparable);
            lineAndShapeRenderer = (XYLineAndShapeRenderer) xyplot.getRendererForDataset(xydataset);
//            System.out.println(lineAndShapeRenderer.getSeriesLinesVisible(index));
            if (me.getClickCount() == 1) {
                if (lineAndShapeRenderer.getSeriesLinesVisible(index) == null) {
                    lineAndShapeRenderer.setSeriesLinesVisible(index, Boolean.FALSE);
                    lineAndShapeRenderer.setSeriesShapesVisible(index, Boolean.FALSE);
                } else if (lineAndShapeRenderer.getSeriesLinesVisible(index) == false) {
                    lineAndShapeRenderer.setSeriesLinesVisible(index, Boolean.TRUE);
                    lineAndShapeRenderer.setSeriesShapesVisible(index, Boolean.TRUE);
                } else {
                    lineAndShapeRenderer.setSeriesLinesVisible(index, Boolean.FALSE);
                    lineAndShapeRenderer.setSeriesShapesVisible(index, Boolean.FALSE);
                }
            }
        }
    }
    double x = 0;
    double y = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
