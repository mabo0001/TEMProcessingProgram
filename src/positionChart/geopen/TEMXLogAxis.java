/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package positionChart.geopen;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class TEMXLogAxis extends LogAxis {

    private static final long serialVersionUID = 1L;

    public TEMXLogAxis(String title) {
        super(title);
//        final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
//        newSymbols.setExponentSeparator("E");
//        newSymbols.setDecimalSeparator('.');
        final DecimalFormat decForm = new DecimalFormat("0.############");
//        final DecimalFormat decForm = new DecimalFormat("0");
//        final DecimalFormat decForm = new DecimalFormat("0.#E0#");
//        decForm.setDecimalFormatSymbols(newSymbols);
        setNumberFormatOverride(decForm);
    }

    protected List<NumberTick> refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        List<NumberTick> ticks = new ArrayList<NumberTick>();
        double start = Math.floor(calculateLog(getLowerBound()));
        double end = Math.ceil(calculateLog(getUpperBound()));
        for (int i = (int) start; i < end; i++) {
            double v = Math.pow(this.getBase(), i);
            for (double j = 1; j <= this.getBase(); j++) {
                String l = createTickLabel(j * v);
                if (j != this.getBase()) {
                    l = "";
                }
                if (range.contains(j * v)) {
                    ticks.add(new NumberTick(new Double(j * v), l, TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
        }
        return ticks;
    }
}
