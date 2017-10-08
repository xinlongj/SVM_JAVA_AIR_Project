package data_prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import feature_extraction.FeatureExtraction;
import feature_extraction.Interpolation;
/**
 * 
 * @author Xinlong Jiang
 * Original data: 
 * ID TimeInMiliSec left right 
 * 7 1456380691843 l387.00r217.00
 * 8 1456380691844 l387.00r218.00 
 * 9 1456380691845 l385.00r218.00 
 * ...
 * 
 * 
 * The data will be firstly segmented using sliding windows: Window length L
 * = 2 sec, Step S = 1 sec. the original data is voltage value read from IR
 * sensors, it will be transfer to distance using voltage2distance();
 */

public class DataSegment {
	public static List<double[]> seg(String[] flieArray) throws IOException {
		List<double[]> features = new ArrayList<double[]>();
		String encoding = "GBK";
		for (int f = 0; f < flieArray.length; f++) {
			File file = new File(flieArray[f]);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedRead = new BufferedReader(read);
				String line = bufferedRead.readLine();
				long time0 = Long.parseLong(line.trim().split("\\s+")[1]);
				List<Double> timeList = new ArrayList<Double>();
				List<Double> leftDisList = new ArrayList<Double>();
				List<Double> rightDisList = new ArrayList<Double>();
				while (line != null) {
					double time = (Long.parseLong(line.trim().split("\\s+")[1]) - time0);
					timeList.add(time);
					double leftV = Double.parseDouble(line.trim().split("\\s")[2].split("l")[1].split("r")[0]);// 左脚电压值
					leftDisList.add(voltage2distance(leftV));
					double rightV = Double.parseDouble(line.trim().split("\\s")[2].split("l")[1].split("r")[1]);// 右脚电压值
					rightDisList.add(voltage2distance(rightV));
					line = bufferedRead.readLine();
				}
				bufferedRead.close();

				int dataNum = timeList.size();
				int N = (int) Math.floor(timeList.get(dataNum - 1) / 1000);
				// There are N-1 windows with L = 2s in total
				for (int i = 1; i < N - 1; i++) {
					List<Double> oldTime = new ArrayList<Double>();
					List<Double> leftWindow = new ArrayList<Double>();
					List<Double> rightWindow = new ArrayList<Double>();

					for (int j = 0; j < timeList.size(); j++) {
						// 2s窗口
						double t = timeList.get(j);
						if ((t < 1000 * (i + 1)) && (t >= 1000 * (i - 1))) {
							oldTime.add(t);
							leftWindow.add(leftDisList.get(j));
							rightWindow.add(rightDisList.get(j));
						}
					}
					double t0 = oldTime.get(0);// The first time stamp
					int t_length = oldTime.size();
					double[] x = new double[t_length + 1];
					double[] y1 = new double[leftWindow.size() + 1];
					double[] y2 = new double[rightWindow.size() + 1];
					double xx[] = new double[2001];
					x[t_length] = 2000;
					y1[t_length] = leftWindow.get(t_length - 1);
					y2[t_length] = rightWindow.get(t_length - 1);
					int n = x.length;
					for (int k = 0; k < t_length; k++) {
						double ti = oldTime.get(k) - t0;
						oldTime.set(k, ti);
						x[k] = oldTime.get(k);
						y1[k] = leftWindow.get(k);
						y2[k] = rightWindow.get(k);
					}
					for (int k = 0; k < 2001; k++) {
						xx[k] = k;
					}
					Interpolation function_left = new Interpolation(x, y1, n);
					Interpolation function_right = new Interpolation(x, y2, n);
					double yy1[] = function_left.insert(xx);
					double yy2[] = function_right.insert(xx);
					double new_yy1[] = new double[80];
					double new_yy2[] = new double[80];
					for (int k = 0; k < yy1.length - 1; k++) {
						if (k % 25 == 0) {
							int k1 = (int) Math.floor(k / 25);
							new_yy1[k1] = yy1[k];
							new_yy2[k1] = yy2[k];
						}
					}
					double[] feature = FeatureExtraction.extract(new_yy1, new_yy2);
					String feature0 = "";
					for (int k = 0; k < feature.length; k++) {
						feature0 = feature0 + (k + 1) + ":" + feature[k] + " ";
					}
					System.out.println(feature0);
					features.add(feature);
				}
			}
		}
		return features;
	}

	// The Conversion relationship from voltage to distance is build by curve fitting
	private static double voltage2distance(double voltage) {
		double dis = -1;
		if (voltage < 30) {
			dis = (double) (2865 / (30 + 6.485) - 2.135);
		} else {
			dis = (double) (2865 / (voltage + 6.485) - 2.135);
		}
		return dis;
	}
}
