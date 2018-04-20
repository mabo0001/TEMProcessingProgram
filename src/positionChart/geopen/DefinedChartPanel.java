/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import ui.geopen.TEMProcessingProgramWin;
import static ui.geopen.TEMProcessingProgramWin.jPanel1;
import static ui.geopen.TEMProcessingProgramWin.setFixedRange;

/**
 *
 * @author Administrator
 */
public class DefinedChartPanel extends ChartPanel {

    private double yMin = 0;
    private double yMax = 0;
    private double xMin = 0;
    private double xMax = 0;
    private Point p;
    public static double zoomX1 = 0;
    public static double zoomY1 = 0;
    public static double zoomX2 = 0;
    public static double zoomY2 = 0;

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public DefinedChartPanel(JFreeChart chart) {
        super(chart);
    }

    public DefinedChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    public DefinedChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, properties, save, print, zoom, tooltips);
    }

    public DefinedChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, save, print, zoom, tooltips);
    }

    public DefinedChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, copy, save, print, zoom, tooltips);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        p = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e); //To change body of generated methods, choose Tools | Templates.
        boolean length = super.getFillZoomRectangle();
        Point point = e.getPoint();
        if (p.getX() - point.getX() >= super.getZoomTriggerDistance()) {
            TEMProcessingProgramWin.setAxisRange(this, TEMProcessingProgramWin.yMin, TEMProcessingProgramWin.yMax);
//            TEMProcessingProgramWin.setAxisRangeX(this, TEMProcessingProgramWin.xMin, TEMProcessingProgramWin.xMax);
//            TEMProcessingProgramWin.setAxisRangeX(this, xMin, xMax);
        }
        int count = TEMProcessingProgramWin.jPanel1.getComponentCount();
        if (count != 0) {
            JFreeChart jfreechart = this.getChart();
            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
            if (xyplot.getDomainAxis() instanceof TEMXLogAxis) {
                return;
            }
            NumberAxis numberaxisX = (NumberAxis) xyplot.getDomainAxis();
            NumberAxis numberaxisY = (NumberAxis) xyplot.getRangeAxis();
            TEMProcessingProgramWin.yMaxZ = numberaxisY.getUpperBound();
            TEMProcessingProgramWin.yMinZ = numberaxisY.getLowerBound();
            TEMProcessingProgramWin.xMaxZ = numberaxisX.getUpperBound();
            TEMProcessingProgramWin.xMinZ = numberaxisX.getLowerBound();

            TEMProcessingProgramWin.setAllZoom(TEMProcessingProgramWin.jPanel1,
                    TEMProcessingProgramWin.xMinZ,
                    TEMProcessingProgramWin.xMaxZ,
                    TEMProcessingProgramWin.yMinZ,
                    TEMProcessingProgramWin.yMaxZ);
        }
    }
}
