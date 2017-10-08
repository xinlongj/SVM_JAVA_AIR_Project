package feature_extraction;

public class Zscore {
	public static double[] z_score(double[] x) {
		double sum = 0, square_sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += x[i];
			square_sum += x[i] * x[i];
		}
		double EX = sum / x.length;
		double EXX = square_sum / x.length;
		double sigma = Math.sqrt(EXX - EX * EX);
		for (int i = 0; i < x.length; i++) {
			x[i] = (x[i] - EX) / sigma;
		}
		return x;

	}
}
