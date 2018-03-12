/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Administrator
 */
public class Resis_Volt_TimeChart {

    XYDataset dataset;

    public Resis_Volt_TimeChart(XYDataset dataset) {
        this.dataset = dataset;
    }

    public JFreeChart createChart(String titleStr, String xLabel, String yLabel, String chartVersion, XYDataset dataset, boolean flagLineOrSingle) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                //        JFreeChart chart = ChartFactory.createScatterPlot(
                titleStr,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,//lengend
                true,
                false);
        TextTitle title = chart.getTitle();
        if (flagLineOrSingle == false) {
            title.setFont(new Font("楷体_GB2312", Font.BOLD, 16));
            title.setPaint(Color.blue);
        } else {
            title.setFont(new Font("", Font.PLAIN, 10));
            title.setPaint(Color.blue);
        }
        chart.setBackgroundPaint(Color.white);
        if (chartVersion.equalsIgnoreCase("VT")) {
            //设定标题
//            if (flagLineOrSingle == false) {
//                title.setFont(new Font("宋体", Font.BOLD, 14));
//                title.setPaint(Color.BLACK);
//            } else {
//                title.setFont(new Font("", Font.BOLD, 12));
//                title.setPaint(Color.blue);
//            }
//            chart.setBackgroundPaint(Color.white);
//            chart.removeLegend();//移除下方颜色标签

            java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D);
            XYPlot xyplot = (XYPlot) chart.getPlot();
            //设定可见不可见
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
            xylineandshaperenderer.setBaseItemLabelsVisible(false);
            xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());//弹出数值
            xylineandshaperenderer.setBaseShapesVisible(true);
            xylineandshaperenderer.setSeriesShape(0, double1);
            xylineandshaperenderer.setSeriesLinesVisible(0, true);
            xylineandshaperenderer.setSeriesPaint(0, Color.RED);
            xylineandshaperenderer.setUseFillPaint(false);//填充
            //显示样式
            StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                    StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.##########"),
                    new java.text.DecimalFormat("0.###################"));
            xylineandshaperenderer.setBaseToolTipGenerator(ttg);

            //坐标轴
            TEMXLogAxis xAxis = new TEMXLogAxis(xLabel);
            LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
//            LogAxis xAxis = new LogAxis(xLabel);
//            LogAxis yAxis = new LogAxis(yLabel);
//            yAxis.setAutoRangeMinimumSize(1E-20);//自定义最小值 最小的设定
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
//            LogAxis numberaxisX = (LogAxis) xyplot.getDomainAxis();
//            LogAxis numberaxisY = (LogAxis) xyplot.getRangeAxis();
            numberaxisX.setAxisLineVisible(false);
            numberaxisX.setTickMarksVisible(false);
            numberaxisX.setLabelPaint(Color.BLUE);
            numberaxisX.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));

            if (flagLineOrSingle == false) {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            } else {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            }
            numberaxisY.setAxisLineVisible(false);
            numberaxisY.setTickMarksVisible(false);
            numberaxisY.setLabelPaint(Color.BLUE);
            numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));

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
            xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
            xyplot.setDomainGridlinePaint(Color.lightGray);//纵向线
            xyplot.setRangeGridlinePaint(Color.lightGray);
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairPaint(Color.BLUE);
            xyplot.setDomainCrosshairLockedOnData(true);
            xyplot.setRangeCrosshairVisible(true);
            xyplot.setRangeCrosshairPaint(Color.BLUE);//设定颜色
            xyplot.setRangeCrosshairLockedOnData(true);
            xyplot.setDomainZeroBaselineVisible(true);
            xyplot.setRangeZeroBaselineVisible(true);
            xyplot.setDomainPannable(false);//不可ctrl+拖动
            xyplot.setRangePannable(false);
        } else if (chartVersion.equalsIgnoreCase("RT")) {
            //设定标题
//            TextTitle title = chart.getTitle();
//            title.setFont(new Font("宋体", Font.BOLD, 14));
//            title.setPaint(Color.BLACK);
//            chart.setBackgroundPaint(Color.white);
//            chart.removeLegend();//移除下方颜色标签

            java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D);
            XYPlot xyplot = (XYPlot) chart.getPlot();
            //设定可见不可见
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
            xylineandshaperenderer.setBaseItemLabelsVisible(false);
            xylineandshaperenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());//弹出数值
            xylineandshaperenderer.setBaseShapesVisible(true);
            xylineandshaperenderer.setSeriesShape(0, double1);
            xylineandshaperenderer.setSeriesLinesVisible(0, true);
            xylineandshaperenderer.setSeriesPaint(0, Color.GREEN);
            xylineandshaperenderer.setUseFillPaint(false);//填充
            //显示样式
            StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                    StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.0000000"),
                    new java.text.DecimalFormat("0.0000000"));
            xylineandshaperenderer.setBaseToolTipGenerator(ttg);

            //坐标轴
            TEMXLogAxis xAxis = new TEMXLogAxis(xLabel);
            LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
//            LogAxis xAxis = new LogAxis(xLabel);
//            LogAxis yAxis = new LogAxis(yLabel);
            yAxis.setAutoRangeMinimumSize(1E-20);//自定义最小值 最小的设定
//        yAxis.setBase(100);
//        LogFormat format = new LogFormat(yAxis.getBase(), "111", "222", true);//自定义坐标轴的显示
//        yAxis.setNumberFormatOverride(format);
//        System.out.println(yAxis.getSmallestValue() + "****");
//        yAxis.setAutoRange(true);
            xyplot.setDomainAxis(xAxis);
            xyplot.setRangeAxis(yAxis);
            //坐标轴只显示数字
//            LogAxis numberaxisX = (LogAxis) xyplot.getDomainAxis();
//            LogAxis numberaxisY = (LogAxis) xyplot.getRangeAxis();
            TEMXLogAxis numberaxisX = (TEMXLogAxis) xyplot.getDomainAxis();
            LogarithmicAxis numberaxisY = (LogarithmicAxis) xyplot.getRangeAxis();
            numberaxisX.setAxisLineVisible(false);
            numberaxisX.setTickMarksVisible(false);
            numberaxisX.setLabelPaint(Color.BLUE);
            numberaxisX.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
            numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            if (flagLineOrSingle == false) {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            } else {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            }
            numberaxisY.setAxisLineVisible(false);
            numberaxisY.setTickMarksVisible(false);
            numberaxisY.setLabelPaint(Color.BLUE);
            numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
            numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
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
            xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
            xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);//纵向线
            xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairPaint(Color.BLUE);
            xyplot.setDomainCrosshairLockedOnData(true);
            xyplot.setRangeCrosshairVisible(true);
            xyplot.setRangeCrosshairPaint(Color.BLUE);//设定颜色
            xyplot.setRangeCrosshairLockedOnData(true);
            xyplot.setDomainZeroBaselineVisible(true);
            xyplot.setRangeZeroBaselineVisible(true);
            xyplot.setDomainPannable(false);
            xyplot.setRangePannable(false);
        } else if (chartVersion.equalsIgnoreCase("RD")) {
            //设定标题
//            TextTitle title = chart.getTitle();
//            title.setFont(new Font("宋体", Font.BOLD, 14));
//            title.setPaint(Color.BLACK);
//            chart.setBackgroundPaint(Color.white);
//            chart.removeLegend();//移除下方颜色标签
            java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D);
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
//            StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
//                    StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.000E00"),
//                    new java.text.DecimalFormat("0.000E00"));
            StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
                    StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new java.text.DecimalFormat("0.0000000"),
                    new java.text.DecimalFormat("0.0000000"));
            xylineandshaperenderer.setBaseToolTipGenerator(ttg);

            //坐标轴
            TEMXLogAxis xAxis = new TEMXLogAxis(xLabel);
            LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
//            LogAxis xAxis = new LogAxis(xLabel);
//            LogAxis yAxis = new LogAxis(yLabel);
            yAxis.setAutoRangeMinimumSize(1E-20);//自定义最小值 最小的设定
            yAxis.setInverted(true);//设定是不是反向
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
//            LogAxis numberaxisX = (LogAxis) xyplot.getDomainAxis();
//            LogAxis numberaxisY = (LogAxis) xyplot.getRangeAxis();
            numberaxisX.setAxisLineVisible(false);
            numberaxisX.setTickMarksVisible(false);
            numberaxisX.setLabelPaint(Color.BLUE);
            numberaxisX.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
            numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            if (flagLineOrSingle == false) {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 9));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
            } else {
                numberaxisX.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisY.setTickLabelFont(new Font("", Font.PLAIN, 8));
                numberaxisX.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
                numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            }
            numberaxisY.setAxisLineVisible(false);
            numberaxisY.setTickMarksVisible(false);
            numberaxisY.setLabelPaint(Color.BLUE);
            numberaxisY.setTickLabelInsets(new RectangleInsets(0, 0, 0, 0));
            numberaxisY.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
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
            xyplot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
            xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);//纵向线
            xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairPaint(Color.BLUE);
            xyplot.setDomainCrosshairLockedOnData(true);
            xyplot.setRangeCrosshairVisible(true);
            xyplot.setRangeCrosshairPaint(Color.BLUE);//设定颜色
            xyplot.setRangeCrosshairLockedOnData(true);
            xyplot.setDomainZeroBaselineVisible(true);
            xyplot.setRangeZeroBaselineVisible(true);
            xyplot.setDomainPannable(false);
            xyplot.setRangePannable(false);
        }
        return chart;
    }
}
