package data_prepare;

import java.io.IOException;

/**
 * 
 * @author Xinlong Jiang
 * @function This class is for preparing training dataset, it set the original
 *           datafile and output datafile, then use the "PrepareData"
 */
public class PrepareTrainingdata {

	public static void prepare() {
		prepareData = new PrepareData();
		prepareData.setInputDataFiles(downstairFiles, runFiles, stayFiles, upstairFiles, walkFiles, walkstayFiles);
		prepareData.setOutputDataFile(trainingDataFile);
		try {
			prepareData.prepare();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static PrepareData prepareData = null;
	static String[] downstairFiles = { "dataset/downstair/user1.txt", "dataset/downstair/user2.txt",
			"dataset/downstair/user3.txt" };
	static String[] runFiles = { "dataset/run/user1.txt", "dataset/run/user2.txt", "dataset/run/user3.txt" };
	static String[] stayFiles = { "dataset/stay/user1.txt", "dataset/stay/user2.txt", "dataset/stay/user3.txt" };
	static String[] upstairFiles = { "dataset/upstair/user1.txt", "dataset/upstair/user2.txt",
			"dataset/upstair/user3.txt" };
	static String[] walkFiles = { "dataset/walk/user1.txt", "dataset/walk/user2.txt", "dataset/walk/user3.txt" };
	static String[] walkstayFiles = { "dataset/walkstay/user1.txt", "dataset/walkstay/user2.txt",
			"dataset/walkstay/user3.txt" };

	private static String trainingDataFile = "test/trainingData.txt";
}
