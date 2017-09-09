package colorBar.geopen;

import java.awt.Color;

public class GradientChannel_Time_Volt {

    public static int divisions = 300;
    public final static Color[] GRADIENT_MAROON_TO_GOLD = createGradient(
            new Color(0xA0, 0x00, 0x00), new Color(0xFF, 0xFF, 0x00), divisions);
    public final static Color[] GRADIENT_BLUE_TO_RED = createGradient(
            Color.BLUE, Color.RED, divisions);
    public final static Color[] GRADIENT_BLACK_TO_WHITE = createGradient(
            Color.BLACK, Color.WHITE, divisions);
    public final static Color[] GRADIENT_RED_TO_GREEN = createGradient(
            Color.RED, Color.GREEN, divisions);
    public final static Color[] GRADIENT_GREEN_YELLOW_ORANGE_RED = createMultiGradient(
            new Color[]{Color.green, Color.yellow, Color.orange, Color.red},
            divisions);
    public final static Color[] GRADIENT_RAINBOW = createMultiGradient(
            new Color[]{new Color(181, 32, 255), Color.blue, Color.green,
                Color.yellow, Color.orange, Color.red}, divisions);
    public final static Color[] GRADIENT_HOT = createMultiGradient(new Color[]{
                Color.black, new Color(87, 0, 0), Color.red, Color.orange,
                Color.yellow, Color.white}, divisions);
    public final static Color[] GRADIENT_HEAT = createMultiGradient(
            new Color[]{Color.black, new Color(105, 0, 0),
                new Color(192, 23, 0), new Color(255, 150, 38), Color.white},
            divisions);
    public final static Color[] GRADIENT_ROY = createMultiGradient(new Color[]{
                Color.red, Color.orange, Color.yellow}, divisions);

    public static Color[] createGradient(final Color one, final Color two,
            final int numSteps) {
        int r1 = one.getRed();
        int g1 = one.getGreen();
        int b1 = one.getBlue();
        int a1 = one.getAlpha();

        int r2 = two.getRed();
        int g2 = two.getGreen();
        int b2 = two.getBlue();
        int a2 = two.getAlpha();

        int newR = 0;
        int newG = 0;
        int newB = 0;
        int newA = 0;

        Color[] gradient = new Color[numSteps];
        double iNorm;
        for (int i = 0; i < numSteps; i++) {
            iNorm = i / (double) numSteps; // a normalized [0:1] variable
            newR = (int) (r1 + iNorm * (r2 - r1));
            newG = (int) (g1 + iNorm * (g2 - g1));
            newB = (int) (b1 + iNorm * (b2 - b1));
            newA = (int) (a1 + iNorm * (a2 - a1));
            gradient[i] = new Color(newR, newG, newB, newA);
        }

        return gradient;
    }

    public static Color[] createMultiGradient(Color[] colors, int numSteps) {
        int numSections = colors.length - 1;
        int gradientIndex = 0; // points to the next open spot in the final
        // gradient
        Color[] gradient = new Color[numSteps];
        Color[] temp;
        if (numSections <= 0) {
            throw new IllegalArgumentException(
                    "You must pass in at least 2 colors in the array!");
        }

        for (int section = 0; section < numSections; section++) {
            temp = createGradient(colors[section], colors[section + 1],
                    numSteps / numSections);
            for (int i = 0; i < temp.length; i++) {
                gradient[gradientIndex++] = temp[i];
            }
        }

        if (gradientIndex < numSteps) {
            for (/* nothing to initialize */; gradientIndex < numSteps; gradientIndex++) {
                gradient[gradientIndex] = colors[colors.length - 1];
            }
        }

        return gradient;
    }
}
