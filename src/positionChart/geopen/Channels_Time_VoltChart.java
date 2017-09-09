/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import colorBar.geopen.Gradient;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.ui.VerticalAlignment;
import ui.geopen.TEMLine_Time_Resis_VolWin;

/**
 * 时间抽道界
 *
 * @author Administrator
 */
public class Channels_Time_VoltChart implements ChartMouseListener {

    private XYItemRenderer renderer;
    private JFreeChart jfreechart;
    private BasicStroke stroke2F = new BasicStroke(1.0f);
    private BasicStroke stroke4F = new BasicStroke(3.0f);
    private int legendCounts = -1;//自动更新图例个数
    private String blocklabel = "";//图例标签内容
    private BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
    private BlockContainer blockcontainer1;
    private TEMLine_Time_Resis_VolWin lineWin;//创建对象
    private boolean flagOnlyonce = false;//只显示splitpane 一部分

    public Channels_Time_VoltChart(TEMLine_Time_Resis_VolWin lineWin) {
        this.lineWin = lineWin;
    }

    public BlockContainer getBlockcontainer1() {
        return blockcontainer1;
    }

    public int getLegendCounts() {
        return legendCounts;
    }

    public BlockContainer getBlockcontainer() {
        return blockcontainer;
    }

    public void setLegendCounts(int legendCounts) {
        this.legendCounts = legendCounts;
    }

    public JFreeChart getJfreechart() {
        return jfreechart;
    }

    public void setJfreechart(JFreeChart jfreechart) {
        this.jfreechart = jfreechart;
    }

    public XYItemRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(XYItemRenderer renderer) {
        this.renderer = renderer;
    }

    private XYDataset createSampleDataset(XYSeries[] timeSeries) {
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        int counts = timeSeries.length;
        for (int i = 0; i < counts; i++) {
//            System.out.println(timeSeries[i]);
            xyseriescollection.addSeries(timeSeries[i]);
        }
        return xyseriescollection;
    }

    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createXYLineChart("电压抽道剖面", "Number", "Voltage( mV/AM2 )", xydataset, PlotOrientation.VERTICAL, true, true, false);
        //设定标题
        TextTitle title = jfreechart.getTitle();
        title.setFont(new Font("楷体_GB2312", Font.BOLD, 16));
        title.setPaint(Color.BLUE);
        jfreechart.removeLegend();//移除下方颜色标签
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        renderer = xyplot.getRenderer();
//        LogAxis yAxis = new LogAxis("Voltage( V/AM2 )");
        LogarithmicAxis yAxis = new LogarithmicAxis("Voltage( mV/AM2 )");
        yAxis.setAxisLineVisible(false);
        yAxis.setTickMarksVisible(false);
        yAxis.setLabelPaint(Color.BLUE);
//        yAxis.set
//        yAxis.setTickMarkInsideLength(1f);
//        yAxis.setTickMarkPaint(Color.LIGHT_GRAY);
        yAxis.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
        yAxis.setTickLabelFont(new Font("", Font.PLAIN, 10));
        yAxis.setLabelPaint(Color.BLUE);
        yAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        //自定义legend
        LegendTitle legendtitle = new LegendTitle(jfreechart.getPlot());
        blockcontainer.setFrame(new BlockBorder(1.0D, 1.0D, 1.0D, 1.0D));
        legendCounts = xydataset.getSeriesCount();
        blocklabel = "图例(" + legendCounts + "个)\n(单位/ms)";
        LabelBlock labelblock = new LabelBlock(blocklabel, new Font("楷体_GB2312", Font.PLAIN, 10));
        labelblock.setPadding(5D, 5D, 5D, 5D);
        blockcontainer.add(labelblock, RectangleEdge.TOP);
        blockcontainer1 = legendtitle.getItemContainer();
        blockcontainer1.setPadding(2D, 10D, 5D, 2D);
        blockcontainer.add(blockcontainer1);
        legendtitle.setWrapper(blockcontainer);
        legendtitle.setPosition(RectangleEdge.RIGHT);
        legendtitle.setItemFont(new Font("", Font.PLAIN, 8));
//        legendtitle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legendtitle.setVerticalAlignment(VerticalAlignment.CENTER);
        jfreechart.addSubtitle(legendtitle);
        //坐标轴只显示数字
        NumberAxis numberaxisX = (NumberAxis) xyplot.getDomainAxis();
        numberaxisX.setAutoRangeIncludesZero(true);
        numberaxisX.setAxisLineVisible(false);
        numberaxisX.setTickMarksVisible(false);
//        numberaxisX.setTickMarkPaint(Color.LIGHT_GRAY);
//        numberaxisX.setTickMarkInsideLength(1f);
        numberaxisX.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
        numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 10));
        numberaxisX.setLabelPaint(Color.BLUE);
        numberaxisX.setLabelFont(new Font("", Font.PLAIN, 12));
        //设定最小值
        numberaxisX.setAutoRangeIncludesZero(false);
//        NumberAxis numberaxisY = (NumberAxis) xyplot.getRangeAxis();
//        numberaxisY.setAxisLineVisible(false);
////        numberaxisY.setTickMarksVisible(false);
//        numberaxisY.setTickMarkPaint(Color.LIGHT_GRAY);
//        numberaxisY.setTickMarkInsideLength(1f);
//        numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
//        numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 10));
//        numberaxisY.setLabelPaint(Color.BLUE);
//        numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        //显示样式
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.############"),
                new java.text.DecimalFormat("0.#####################"));
        xylineandshaperenderer.setBaseToolTipGenerator(ttg);
        //设置所有有时间抽道为 渐变色彩虹色
        Color[] colors = Gradient.createMultiGradient(
                new Color[]{Color.BLUE, Color.green, Color.red},
                xyplot.getSeriesCount());
        for (int i = 0; i < xyplot.getSeriesCount(); i++) {
            xylineandshaperenderer.setSeriesPaint(i, colors[i]);
            xylineandshaperenderer.setSeriesStroke(i, stroke2F);
        }
        xyplot.setRangeAxis(yAxis);
        xyplot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());//设定x坐标为整数
        xyplot.getDomainAxis().setAxisLineVisible(false);
        xyplot.getDomainAxis().setTickMarksVisible(false);
        xyplot.getDomainAxis().setLabelPaint(Color.BLUE);
        //设定背景
        Color background = new Color(247, 247, 247);
        xyplot.setBackgroundPaint(background);
        xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        xyplot.setDomainGridlinePaint(Color.GRAY);//纵向线
        xyplot.setRangeGridlinePaint(Color.GRAY);

        //设定键盘鼠标 按住ctrl 拖动
        xyplot.setDomainPannable(false);
        xyplot.setRangePannable(false);
        return jfreechart;
    }

    public JPanel createDemoPanel(XYSeries[] timeSeries) {
        if (timeSeries.length != 0) {
            XYDataset xydataset = createSampleDataset(timeSeries);
            jfreechart = createChart(xydataset);
            //添加箭头
            ChartPanel chartpanel = new ChartPanel(jfreechart, true);
            chartpanel.addChartMouseListener(this);
            chartpanel.setPopupMenu(null);//不弹出菜单
            //防止图形变形
            chartpanel.setMaximumDrawWidth(5000);
            chartpanel.setMaximumDrawHeight(5000);
            chartpanel.setDismissDelay(100000);//设定显示时间
            return chartpanel;
        } else {
            JOptionPane.showMessageDialog(null, "无数据，请检查！");
            return null;
        }
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent chartmouseevent) {
        if (chartmouseevent.getTrigger().getClickCount() == 2) {
            //设定右边的边框不可显示
            int expandWidth = 150;
            if (flagOnlyonce == false) {
                setSplitPaneRightOrLeftVisual(lineWin.channel_time_voltSplitPane, expandWidth, flagOnlyonce);
                flagOnlyonce = true;
            } else {
                setSplitPaneRightOrLeftVisual(lineWin.channel_time_voltSplitPane, expandWidth, flagOnlyonce);
                flagOnlyonce = false;
            }
        } else {
            org.jfree.chart.entity.ChartEntity chartentity = chartmouseevent.getEntity();
            if (chartentity != null && (chartentity instanceof LegendItemEntity)) {
                LegendItemEntity legenditementity = (LegendItemEntity) chartentity;
                Comparable comparable = legenditementity.getSeriesKey();
                XYPlot xyplot = (XYPlot) jfreechart.getPlot();
                XYDataset xydataset = xyplot.getDataset();
                XYItemRenderer xyitemrenderer = xyplot.getRenderer();
                for (int i = 0; i < xydataset.getSeriesCount(); i++) {
                    xyitemrenderer.setSeriesStroke(i, stroke2F);
                    if (xydataset.getSeriesKey(i).equals(comparable)) {
                        //设定线段粗细 以显示
                        xyitemrenderer.setSeriesStroke(i, stroke4F);
                        //添加annotation
                        xyplot.clearAnnotations();//先清除标记
                        XYSeries tempSeries = ((XYSeriesCollection) xydataset).getSeries(comparable);//实现单个点击单个显示时间曲线的名字
                        XYPointerAnnotation xypointerannotation = new XYPointerAnnotation(tempSeries.getKey().toString(),
                                tempSeries.getMaxX(), tempSeries.getY(tempSeries.getItemCount() - 1).doubleValue(), -0.9D);
                        xypointerannotation.setBaseRadius(25D);//箭头长度
                        xypointerannotation.setTipRadius(1D);//偏离
                        xypointerannotation.setFont(new Font("SansSerif", 0, 9));
//        xypointerannotation.setArrowPaint;//箭头长度
//        xypointerannotation.setPaint(Color.WHITE);
                        xypointerannotation.setTextAnchor(TextAnchor.HALF_ASCENT_CENTER);
                        xyplot.addAnnotation(xypointerannotation);
                    }
                }

            }
        }
    }

    public void setSplitPaneRightOrLeftVisual(JSplitPane splitPane, int expandWidth, boolean flagOnlyone) {
        if (flagOnlyone == true) {
            splitPane.setDividerLocation(splitPane.getWidth());
        } else {
            splitPane.setDividerLocation(splitPane.getWidth() - expandWidth);
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
    }
}
