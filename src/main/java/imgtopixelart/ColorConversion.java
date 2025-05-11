package imgtopixelart;

import java.awt.Color;

public class ColorConversion {
	public static int findClosestColor(int sampleColor, int[] colorList) {
        double minDistance = Double.MAX_VALUE;
        int closestColor = 0;
		
        double[] sampleLab = rgbToLab(new Color(sampleColor));
		
        for (int color : colorList) {
            double[] lab = rgbToLab(new Color(color));
			
            double distance = calculateLabDistance(sampleLab, lab);
			
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }
		
        return closestColor;
    }
	
    public static double[] rgbToLab(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;
		
        // Using D65 standard illuminant
        double x = 0.4124564 * r + 0.3575761 * g + 0.1804375 * b;
        double y = 0.2126729 * r + 0.7151522 * g + 0.0721750 * b;
        double z = 0.0193339 * r + 0.1191920 * g + 0.9503041 * b;
		
        x /= 0.95047;
        y /= 1.0;
        z /= 1.08883;
		
        double fx = f(x);
        double fy = f(y);
        double fz = f(z);
		
        double l = 116.0 * fy - 16.0;
        double a = 500.0 * (fx - fy);
        double b2 = 200.0 * (fy - fz);
		
        return new double[] { l, a, b2 };
    }
	
    public static double f(double t) {
        double threshold = 0.008856;
        if (t > threshold) {
            return Math.cbrt(t);
        } else {
            return (903.3 * t + 16.0) / 116.0;
        }
    }
	
    public static double calculateLabDistance(double[] lab1, double[] lab2) {
        double lDiff = lab1[0] - lab2[0];
        double aDiff = lab1[1] - lab2[1];
        double bDiff = lab1[2] - lab2[2];
		
        return Math.sqrt(lDiff * lDiff + aDiff * aDiff + bDiff * bDiff);
    }
}
