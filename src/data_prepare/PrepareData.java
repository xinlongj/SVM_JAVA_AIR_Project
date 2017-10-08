package data_prepare;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 
 * @author Xinlong Jiang
 * @function The PrepareData class is used to prepare the data which can be used
 *           for model training and testing.
 */
public class PrepareData {

	public void setInputDataFiles(String[] downstairFiles, String[] runFiles, String[] stayFiles, String[] upstairFiles,
			String[] walkFiles, String[] walkstayFiles) {
		DownstairFiles = downstairFiles;
		RunFiles = runFiles;
		StayFiles = stayFiles;
		UpstairFiles = upstairFiles;
		WalkFiles = walkFiles;
		WalkstayFiles = walkstayFiles;
	}

	public void setOutputDataFile(String outputFile) {
		this.OutputFile = outputFile;
	}

	public void prepare() throws IOException {
		List<double[]> downstair_feature = DataSegment.seg(DownstairFiles);
		List<double[]> run_feature = DataSegment.seg(RunFiles);
		List<double[]> stay_feature = DataSegment.seg(StayFiles);
		List<double[]> upstair_feature = DataSegment.seg(UpstairFiles);
		List<double[]> walk_feature = DataSegment.seg(WalkFiles);
		List<double[]> walkstay_feature = DataSegment.seg(WalkstayFiles);
		String encoding = "GBK";
		OutputStreamWriter write_test = new OutputStreamWriter(new FileOutputStream(OutputFile), encoding);
		BufferedWriter bufferedWriter_test = new BufferedWriter(write_test);
		// downstair label = 1
		for (int i = 0; i < downstair_feature.size(); i++) {
			double[] feature = downstair_feature.get(i);
			String feature_line = "1";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		// run label = 2
		for (int i = 0; i < run_feature.size(); i++) {
			double[] feature = run_feature.get(i);
			String feature_line = "2";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		// stay label = 3
		for (int i = 0; i < stay_feature.size(); i++) {
			double[] feature = stay_feature.get(i);
			String feature_line = "3";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		// upstair label = 4
		for (int i = 0; i < upstair_feature.size(); i++) {
			double[] feature = upstair_feature.get(i);
			String feature_line = "4";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		// walk label = 5
		for (int i = 0; i < walk_feature.size(); i++) {
			double[] feature = walk_feature.get(i);
			String feature_line = "5";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		// walkstay label = 6
		for (int i = 0; i < walkstay_feature.size(); i++) {
			double[] feature = walkstay_feature.get(i);
			String feature_line = "6";
			for (int j = 0; j < feature.length; j++) {
				feature_line = feature_line + " " + (j + 1) + ":" + feature[j];
			}
			bufferedWriter_test.write(feature_line);
			bufferedWriter_test.newLine();
		}
		bufferedWriter_test.close();
	}

	private static String[] DownstairFiles = null;
	private static String[] RunFiles = null;
	private static String[] StayFiles = null;
	private static String[] UpstairFiles = null;
	private static String[] WalkFiles = null;
	private static String[] WalkstayFiles = null;

	private static String OutputFile = null;
}
