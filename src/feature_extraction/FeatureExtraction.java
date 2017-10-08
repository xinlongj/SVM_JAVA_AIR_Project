package feature_extraction;

import java.util.Arrays;

/**
 * 
 * @author Xinlong Jiang
 * @function This is used to extract features from each window, 38 features will
 *           be obtained. 1:t_l_mean, 2:t_l_var, 3:t_l_max, 4:t_l_min, 5:t_l_q1,
 *           6:t_l_q2, 7:t_l_q3, 8:t_l_meancross 9:t_r_mean, 10:t_r_var,
 *           11:t_r_max, 12:t_r_min, 13:t_r_q1, 14:t_r_q2, 15:t_r_q3,
 *           16:t_r_meancross, 17:t_corr, 18:f_l_a1, 19:f_l_a2, 20:f_l_a3,
 *           21:f_l_a4, 22:f_l_a5, 23:f_l_a6, 24:f_l_a7, 25:f_l_a8, 26:f_l_a9,
 *           27:f_l_a10, 28:f_r_a1, 29:f_r_a2, 30:f_r_a3, 31:f_r_a4, 32:f_r_a5,
 *           33:f_r_a6, 34:f_r_a7, 35:f_r_a8, 36:f_r_a9, 37:f_r_a10, 38:f_corr
 */
public class FeatureExtraction {

	public static double[] extract(double[] yy1, double[] yy2) {
		double feature[] = new double[38];
		double[] left_time = new double[8];
		left_time = get_timefeature.get(yy1);
		double[] right_time = new double[8];
		right_time = get_timefeature.get(yy2);
		double time_corr = get_corr(left_time, right_time);
		// 计算序列在频域的幅频特性，Xk是幅值
		int N = 80, P = 8, Q = 10;
		double[] Xk = MyFFT.fft(yy1, N, P, Q);
		double[] Yk = MyFFT.fft(yy2, N, P, Q);
		double[] left_frequence = new double[10];
		double[] right_frequence = new double[10];
		left_frequence[0] = Xk[0] / 2;
		right_frequence[0] = Yk[0] / 2;
		for (int i = 1; i < 10; i++) {
			left_frequence[i] = Xk[i];
			right_frequence[i] = Yk[i];
		}
		double frequence_corr = get_corr(left_frequence, right_frequence);

		System.arraycopy(left_time, 0, feature, 0, 8);
		System.arraycopy(right_time, 0, feature, 8, 8);
		feature[16] = time_corr;
		System.arraycopy(left_frequence, 0, feature, 17, 10);
		System.arraycopy(right_frequence, 0, feature, 27, 10);
		feature[37] = frequence_corr;

		// Normalize with z-score
		feature = z_score(feature);
		return feature;
	}

	private static class get_timefeature {
		// the step of extracting the time domain features
		public static double[] get(double[] window) {
			double[] timeFeature = new double[8];
			double sum = 0;
			int squareSum = 0;
			for (int k = 0; k < window.length; k++) {
				sum += window[k];
			}
			int n = window.length;
			double average = sum / n;
			for (int k = 0; k < window.length; k++) {
				double minus = window[k] - average;
				squareSum += minus * minus;
			}
			double variance = Math.sqrt(squareSum / (n - 1));
			double array[] = new double[n];
			for (int i = 0; i < window.length; i++) {
				array[i] = window[i];
			}
			Arrays.sort(array);
			double minimum = array[0];
			double maximum = array[n - 1];

			double q11 = (float) n / 4;
			double q10 = Math.floor(n / 4);
			double q10Value = (float) window[(int) q10];
			double q12 = q10 + 1;
			double q12Value = (float) window[(int) q12];
			double q1Value = q10Value + (q11 - q10) * (q12Value - q10Value);

			double q21 = (float) n / 2;
			double q20 = Math.floor(n / 2);
			double q20Value = (float) window[(int) q20];
			double q22 = q20 + 1;
			double q22Value = (float) window[(int) q22];
			double q2Value = q20Value + (q21 - q20) * (q22Value - q20Value);

			double q31 = (float) n * 3 / 4;
			double q30 = Math.floor(n * 3 / 4);
			double q30Value = (float) window[(int) q30];
			double q32 = q30 + 1;
			double q32Value = (float) window[(int) q32];
			double q3Value = q30Value + (q31 - q30) * (q32Value - q30Value);

			int meanCross = 0;
			for (int k = 0; k < window.length - 1; k++) {
				double judge = (window[k] - average) * (window[k + 1] - average);
				if (judge <= 0) {
					meanCross++;
				}
			}
			timeFeature[0] = average;
			timeFeature[1] = variance;
			timeFeature[2] = minimum;
			timeFeature[3] = maximum;
			timeFeature[4] = q1Value;
			timeFeature[5] = q2Value;
			timeFeature[6] = q3Value;
			timeFeature[7] = meanCross;

			return timeFeature;

		}
	}

	// the correlation coefficent of two vectors: corr =
	// cov(x,y)/sigma(x)*sigma(y)
	// cov(x,y) = E[(X-EX)*(Y-EY)] = EXY - EX*EY
	private static double get_corr(double[] a, double[] b) {
		double time_corr = 0, cov = 0;
		double sum_a = 0, sum_b = 0;
		double EXX_a = 0, EXX_b = 0, EXY = 0;
		for (int i = 0; i < a.length; i++) {
			sum_a = sum_a + a[i];
			EXX_a = EXX_a + a[i] * a[i];
			sum_b = sum_b + b[i];
			EXX_b = EXX_b + b[i] * b[i];
			EXY = EXY + a[i] * b[i];
		}
		double EX_a = sum_a / a.length;
		double EX_b = sum_b / b.length;
		EXX_a = EXX_a / a.length;
		EXX_b = EXX_b / b.length;
		double sigma_a = Math.sqrt(EXX_a - EX_a * EX_a);
		double sigma_b = Math.sqrt(EXX_b - EX_b * EX_b);
		EXY = EXY / a.length;
		cov = EXY - EX_a * EX_b;
		time_corr = cov / (sigma_a * sigma_b);
		return time_corr;
	}

	public static double[] z_score(double[] x) {
		// DecimalFormat df = new DecimalFormat("0.00");
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
