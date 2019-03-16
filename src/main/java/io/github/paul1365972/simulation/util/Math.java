package io.github.paul1365972.simulation.util;

public class Math extends org.joml.Math {
	
	public static final double PI = java.lang.Math.PI;
	public static final float PIF = (float) java.lang.Math.PI;
	public static final double PI2 = java.lang.Math.PI * 2.0;
	public static final float PIF2 = (float) (java.lang.Math.PI * 2.0);
	public static final double PI_2 = java.lang.Math.PI / 2.0;
	public static final float PIF_2 = (float) (java.lang.Math.PI / 2.0);
	public static final double PI_INV = 1.0 / PI;
	public static final float PIF_INV = (float) (1.0 / PI);
	
	private Math() {
	}
	
	public static int min(int a, int b) {
		return a < b ? a : b;
	}
	
	public static float min(float a, float b) {
		return a < b ? a : b;
	}
	
	public static double min(double a, double b) {
		return a < b ? a : b;
	}
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public static float max(float a, float b) {
		return a > b ? a : b;
	}
	
	public static double max(double a, double b) {
		return a > b ? a : b;
	}
	
	public static int clamp(int a, int min, int max) {
		return min(max, max(min, a));
	}
	
	public static float clamp(float a, float min, float max) {
		return min(max, max(min, a));
	}
	
	public static double clamp(double a, double min, double max) {
		return min(max, max(min, a));
	}
	
	
	public static double floor(double v) {
		return java.lang.Math.floor(v);
	}
	
	public static float floorf(double v) {
		return (float) java.lang.Math.floor(v);
	}
	
	public static double ceil(double v) {
		return java.lang.Math.ceil(v);
	}
	
	public static float ceilf(double v) {
		return (float) java.lang.Math.ceil(v);
	}
	
	public static long round(double v) {
		return java.lang.Math.round(v);
	}
	
	public static int roundf(float v) {
		return java.lang.Math.round(v);
	}
	
	
	public static int abs(int a) {
		return (a < 0) ? -a : a;
	}
	
	public static long abs(long a) {
		return (a < 0) ? -a : a;
	}
	
	public static float abs(float a) {
		return java.lang.Math.abs(a);
	}
	
	public static double abs(double a) {
		return java.lang.Math.abs(a);
	}
	
	
	public static double pow(double a, double b) {
		return java.lang.Math.pow(a, b);
	}
	
	public static float powf(double a, double b) {
		return (float) java.lang.Math.pow(a, b);
	}
	
	public static float sq(float a) {
		return a * a;
	}
	
	public static float cb(float a) {
		return a * a * a;
	}
	
	public static double sqrt(double a) {
		return java.lang.Math.sqrt(a);
	}
	
	public static float sqrtf(double a) {
		return (float) java.lang.Math.sqrt(a);
	}
	
	public static double cbrt(double a) {
		return java.lang.Math.cbrt(a);
	}
	
	public static float cbrtf(double a) {
		return (float) java.lang.Math.cbrt(a);
	}
	
	public static double exp(double a) {
		return java.lang.Math.exp(a);
	}
	
	public static float expf(double a) {
		return (float) java.lang.Math.exp(a);
	}
	
	public static double ln(double a) {
		return java.lang.Math.log(a);
	}
	
	public static float lnf(double a) {
		return (float) java.lang.Math.log(a);
	}
	
	public static double log10(double a) {
		return java.lang.Math.log10(a);
	}
	
	public static float log10f(double a) {
		return (float) java.lang.Math.log10(a);
	}
	
	public static double log(double a, double b) {
		return java.lang.Math.log(a) / java.lang.Math.log(b);
	}
	
	public static float logf(double a, double b) {
		return (float) java.lang.Math.log(a) / (float) java.lang.Math.log(b);
	}
	
	
	public static double toRadians(double a) {
		return a / 180.0 * PI;
	}
	
	public static float toRadians(float a) {
		return a / 180f * PIF;
	}
	
	public static double toDegree(double a) {
		return a * 180.0 * PI;
	}
	
	public static float toDegree(float a) {
		return a * 180f / PIF;
	}
	
	public static double sin(double a) {
		return java.lang.Math.sin(a);
	}
	
	public static float sinf(double a) {
		return (float) java.lang.Math.sin(a);
	}
	
	public static double cos(double a) {
		return java.lang.Math.cos(a);
	}
	
	public static float cosf(double a) {
		return (float) java.lang.Math.sin(a);
	}
	
	
	public static float tanhf(double a) {
		return (float) java.lang.Math.tanh(a);
	}
	
	public static float tanhf_gate(double a) {
		return ((float) java.lang.Math.tanh(a)) / 2.0f + 0.5f;
	}
	
	public static float sigmoid(double a) {
		return (float) (1 - 2 / (1 + java.lang.Math.exp(a)));
	}
	
	public static float sigmoid_gate(double a) {
		return (float) (1 / (1 + java.lang.Math.exp(a)));
	}
	
	public static float softsign(float a) {
		return a / (1 + java.lang.Math.abs(a));
	}
	
	public static float softsign_gate(float a) {
		return (a / (1 + java.lang.Math.abs(a)) + 1.0f) / 2.0f;
	}
	
	/*public static float ramp(float x, float ramp) {
		if (!Float.isFinite(x) || !Float.isFinite(ramp) || x < 0 || x > 1 || ramp < 0)
			throw new IllegalArgumentException("Illegal Arguments x=" + x + "; ramp=" + ramp);
		if (x == 0)
			return 0;
		if (x == 1)
			return 1;
		if (x == -1)
			return -1;
		float result = 1 - (float) java.lang.Math.pow(1 - java.lang.Math.pow(x, ramp), 1 / ramp);
		if (!Float.isFinite(result))
			throw new ArithmeticException("Arithmetic Exception x=" + x + "; ramp=" + ramp);
		return java.lang.Math.copySign(result, x);
	}*/
	
	
}
