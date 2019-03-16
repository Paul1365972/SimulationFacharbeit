package io.github.paul1365972.simulation.util;

public class Color {
	
	private final float r, g, b, a;
	
	public Color(float r, float g, float b, float a) {
		testColorValueRange(r, g, b, a);
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	private static void testColorValueRange(float r, float g, float b, float a) {
		String badComponentString = "";
		if (a < 0f || a > 1f)
			badComponentString += " Alpha";
		if (r < 0f || r > 1f)
			badComponentString += " Red";
		if (g < 0f || g > 1f)
			badComponentString += " Green";
		if (b < 0f || b > 1f)
			badComponentString += " Blue";
		
		if (!badComponentString.isEmpty())
			throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponentString);
	}
	
	public static Color rgb(int r, int g, int b) {
		return rgba(r, g, b, 255);
	}
	
	public static Color rgba(int r, int g, int b, int a) {
		return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
	public static Color rgb(float r, float g, float b) {
		return rgba(r, g, b, 1);
	}
	
	public static Color rgba(float r, float g, float b, float a) {
		return new Color(r, g, b, a);
	}
	
	public static Color hsb(float hue, float saturation, float brightness) {
		return hsba(hue, saturation, brightness, 1f);
	}
	
	public static Color hsba(float hue, float saturation, float brightness, float alpha) {
		float r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = brightness;
		} else {
			float h = (hue - Math.floorf(hue)) * 6.0f;
			float f = h - Math.floorf(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = brightness;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = brightness;
					b = p;
					break;
				case 2:
					r = p;
					g = brightness;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = brightness;
					break;
				case 4:
					r = t;
					g = p;
					b = brightness;
					break;
				case 5:
					r = brightness;
					g = p;
					b = q;
					break;
			}
		}
		return new Color(r, g, b, alpha);
	}
	
	public static Color hsl(float h, float s, float l) {
		return hsba(h, s, l, 1f);
	}
	
	public static Color hsla(float h, float s, float l, float a) {
		float r, g, b;
		
		if (s == 0) {
			r = l;
			b = l;
			g = l;
		} else {
			float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
			float p = 2f * l - q;
			
			r = hueToRgb(p, q, h + (1f / 3f));
			g = hueToRgb(p, q, h);
			b = hueToRgb(p, q, h - (1f / 3f));
		}
		
		return new Color(r, g, b, a);
	}
	
	private static float hueToRgb(float p, float q, float t) {
		if (t < 0)
			t++;
		if (t > 1)
			t--;
		
		if (t < 1f / 6f)
			return p + (q - p) * 6f * t;
		if (t < 1f / 2f)
			return q;
		if (t < 2f / 3f)
			return p + (q - p) * (2f / 3f - t) * 6f;
		
		return p;
	}
	
	public float getR() {
		return r;
	}
	
	public float getG() {
		return g;
	}
	
	public float getB() {
		return b;
	}
	
	public float getA() {
		return a;
	}
}
