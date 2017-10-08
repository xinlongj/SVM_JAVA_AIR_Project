package data_prepare;

import java.io.IOException;

/**
 * 
 * @author Xinlong Jiang
 * @function This class is for Testing data preparing, it set the original
 *           datafile and output datafile, then use the "PrepareData"
 */
public class PrepareTestingdata {
	public static void prepare() {
		prepareData = new PrepareData();
		prepareData.setInputDataFiles(downstairFiles, runFiles, stayFiles, upstairFiles, walkFiles, walkstayFiles);
		prepareData.setOutputDataFile(testingDataFile);
		try {
			prepareData.prepare();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static PrepareData prepareData = null;
	private static String[] downstairFiles = { "dataset/downstair/user4.txt" };
	private static String[] runFiles = { "dataset/run/user4.txt" };
	private static String[] stayFiles = { "dataset/stay/user4.txt" };
	private static String[] upstairFiles = { "dataset/upstair/user4.txt" };
	private static String[] walkFiles = { "dataset/walk/user4.txt" };
	private static String[] walkstayFiles = { "dataset/walkstay/user4.txt" };

	private static String testingDataFile = "test/testingData.txt";
}
