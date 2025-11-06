package org.springblade.common.utils;

public class StrToDoubleArray {
	public static double[] strToDoubleArray(String str, String splitChar) {
		String[] split = str.substring(1, str.length() - 1).replaceAll("\\s+", "").split(splitChar);
		double[] doubles = new double[split.length];
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = Double.parseDouble(split[i]);
		}
		return doubles;
	}

	public static void main(String[] args) {

	}
}
