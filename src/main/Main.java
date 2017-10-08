package main;

import java.io.IOException;

import data_prepare.PrepareTestingdata;
import data_prepare.PrepareTrainingdata;
import svm.svm_predict;
import svm.svm_train;

/**
 * 
 * @author Xinlong Jiang
 * @affiliate Institute of Computing Technology, CAS
 * @email jiangxinlong@ict.ac.cn
 * @paper 《AIR: recognizing activity through IR-based distance sensing on feet》
 * @website http://dl.acm.org/citation.cfm?id=2971447
 * 
 */
public class Main {
	public static void main(String[] args) throws IOException {

		/**
		 * Step 1
		 * Prepare the training dataset
		 * 	1. Load the original collected data
		 * 	2. Data segment
		 * 	3. Feature extraction for every single window
		 */

		PrepareTrainingdata.prepare();

		/**
		 * Step 2
		 * Prepare the training dataset
		 * 	1. Load the original collected data
		 *  2. Data segment
		 *  3. Feature extraction for every single window
		 */

		PrepareTestingdata.prepare();

		/**
		 * Step 3
		 * Training
		 *  "-t" "2" kernel_type RBF
		 *  "-c" "6" c
		 *  "-g" "0" gamma
		 *  "-d" "3" degree for poly
		 */
		String[] trainArgs = { "-t", "2", "-c", "6", "-g", "0", "-d", "3", "test/trainingData.txt",
				"test/svm_model_para.txt" };
		svm_train.main(trainArgs);

		/**
		 * Step 4
		 * Testing
		 */
		String[] testArgs = { "test/testingData.txt", "test/svm_model_para.txt", "test/testingResult.txt" };
		svm_predict.main(testArgs);
	}
}