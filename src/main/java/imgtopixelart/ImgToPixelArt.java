package imgtopixelart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Scanner;
import uilibrary.animation.ImageLoader;

public class ImgToPixelArt {
	/*
	To get the new colors from r/place, select the color palette from html and do this code in console:
	function rgbToHex(rgbColor) {
		// Get the individual RGB components from the rgb() string
		const rgbValues = rgbColor.match(/\d+/g);
	
		// Convert each component to a two-digit hexadecimal representation
		const hexValues = rgbValues.map(value => {
			const hex = parseInt(value).toString(16);
			return hex.length === 1 ? "0" + hex : hex;
		});
	
		// Combine the hex values to get the final hex color
		return "0x" + hexValues.join("");
	}
	let cols = $0.getElementsByClassName("color");
	const colorsAsHex = Array.from(cols, col => rgbToHex(col.getElementsByTagName("div")[0].style.backgroundColor));
	console.log(colorsAsHex.join(", "));
	*/
	
	private static final int[] rPlaceColors = new int[] { 0xFF4500, 0xFFA800, 0xFFD635, 0x00A368, 0x3690EA, 0xB44AC0, 0x000000, 0xFFFFFF };
	private static final int[] rPlaceColorsNew = new int[] { 0xFF4500, 0xFFA800, 0xFFD635, 0x00A368, 0x7EED56, 0x2450A4, 0x3690EA, 0x51E9F4, 0x811E9F, 0xB44AC0, 0xFF99AA, 0x9C6926, 0x000000, 0x898D90, 0xD4D7D9, 0xFFFFFF };
	private static final int[] rPlaceColorsNew2 = new int[] { 0xbe0039, 0xff4500, 0xffa800, 0xffd635, 0x00a368, 0x00cc78, 0x7eed56, 0x00756f, 0x009eaa, 0x2450a4, 0x3690ea, 0x51e9f4, 0x493ac1, 0x6a5cff, 0x811e9f, 0xb44ac0, 0xff3881, 0xff99aa, 0x6d482f, 0x9c6926, 0x000000, 0x898d90, 0xd4d7d9, 0xffffff };
	
	private static int[] allowedColors;
	
    public static void main(String[] args) {
		allowedColors = rPlaceColorsNew2;
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Img path:");
		String url = scan.nextLine();
		
		BufferedImage img = ImageLoader.loadImage(url, true);
		
		System.out.println("New width:");
		String w = scan.nextLine();
		int width;
		boolean useAverageColor = false;
		
		if (w.isBlank()) {
			width = img.getWidth();
		} else {
			width = Integer.parseInt(w);
			System.out.println("Use average color (true/false):");
			useAverageColor = Boolean.parseBoolean(scan.nextLine());
		}
		
		BufferedImage newImg = getPixelArt(img, width, useAverageColor);
		
		System.out.println("New name:");
		String name = scan.nextLine();
		
		ImageLoader.saveImage(newImg, name + ".png", "png");
    }
	
	public static BufferedImage getPixelArt(BufferedImage img, int newWidth, boolean useAverageColor) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		double pixelsPerNewPixel = w / (double) newWidth;
		int newHeight = (int) (h / pixelsPerNewPixel);
		
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < newHeight; y++) {
			for (int x = 0; x < newWidth; x++) {
				int checkX = (int) (x * pixelsPerNewPixel);
				int checkY = (int) (y * pixelsPerNewPixel);
				
				int rgb;
				if (useAverageColor) {
					rgb = avgColor(img, checkX, checkY, (int) pixelsPerNewPixel);
				} else {
					rgb = img.getRGB(checkX, checkY);
				}
				
				int closestColor = closestColor(rgb, allowedColors);
				//int closestColor = findClosestColor(rgb, allowedColors);
				//int closestColor = ColorConversion.findClosestColor(rgb, allowedColors);
				newImg.setRGB(x, y, closestColor);
			}
		}
		
		return newImg;
	}
	
	public static int avgColor(BufferedImage img, int startX, int startY, int dist) {
		int sumR = 0, sumG = 0, sumB = 0;
		
		if (dist < 1) {
			dist = 1;
		}
		int colors = dist * dist;
		
		for (int y = 0; y < dist; y++) {
			for (int x = 0; x < dist; x++) {
				int rgb = img.getRGB(startX + x, startY + y);
				Color col = new Color(rgb);
				sumR += col.getRed();
				sumG += col.getGreen();
				sumB += col.getBlue();
			}
		}
		
		return new Color(sumR / colors, sumG / colors, sumB / colors).getRGB();
	}
	
	public static int closestColor(int color, int[] colorList) {
		int closest = 0;
		double closestDist = Double.POSITIVE_INFINITY;
		Color col = new Color(color);
		
		for (int allowedColor : colorList) {
			double dist = colorDistance(col, new Color(allowedColor));
			if (dist < closestDist) {
				closestDist = dist;
				closest = allowedColor;
			}
		}
		
		return closest;
	}
	
	public static int findClosestColor(int rgb, int[] colorList) {
        double minDistance = Double.MAX_VALUE;
        int closestColor = 0;
		
		Color sampleColor = new Color(rgb);
        int sampleR = sampleColor.getRed();
        int sampleG = sampleColor.getGreen();
        int sampleB = sampleColor.getBlue();
		
        for (int allowedColor : colorList) {
			Color color = new Color(allowedColor);
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
			
            double distance = Math.sqrt(Math.pow(r - sampleR, 2) + Math.pow(g - sampleG, 2) + Math.pow(b - sampleB, 2));
			
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color.getRGB();
            }
        }
		
        return closestColor;
    }
	
	public static double colorDistance(Color c1, Color c2) {
		int red1 = c1.getRed();
		int red2 = c2.getRed();
		int rmean = (red1 + red2) >> 1;
		int r = red1 - red2;
		int g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
	}
}
