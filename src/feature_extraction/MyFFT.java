package feature_extraction;

import java.text.DecimalFormat;
import java.util.List;
/**
 * 
 * @author Xinlong Jiang
 * @function Fast Fourier Transformation
 */
public class MyFFT {
	public static double[] fft(double[] dataList,int N, int P, int Q) {
		// 任意组合数的FFT变换，分解因子N=PQ P行Q列
		// 时域下标 n=n1*Q+n0 (n1=0,1,..,P-1; n0=0,1,..,Q-1) 这种形式能包含所有的下标
		// 时域表达式转换成x(n)=x(n1*Q+n0)
		// 频域下标 k=k1*P+k0 (k1=0,1,..,Q-1; k0=0,1,..,P-1)
		// 频域表达式转换成X(k)=x(k1*Q+k0)
		// 根据DFT表达式，进行代入合并，最终需要进行三步合并X1(k0,n0),X11(k0,n0),最后得到X(k1,k0)=X2(k0,k1)的关系式
		double x[][] = new double[P][Q];// 输入时域序列
		double X[] = new double[N];// 存储最终的|Xk|模值
		double X1r[][] = new double[P][Q];
		double X1i[][] = new double[P][Q];
		double X2[][] = new double[P][Q];
		double X2r[][] = new double[P][Q];
		double X2i[][] = new double[P][Q];
		double X11r[][] = new double[P][Q];
		double X11i[][] = new double[P][Q];

		double WPr = 0;
		double WPi = 0;
		double WQr = 0;
		double WQi = 0;
		double WNr = 0;
		double WNi = 0;
		DecimalFormat df = new DecimalFormat("0.00");
		for (int k0 = 0; k0 < P; k0++) {
			for (int n0 = 0; n0 < Q; n0++) {
				double TempX1r = 0;
				double TempX1i = 0;
				for (int n1 = 0; n1 < P; n1++) {
					x[n1][n0] = dataList[n1 * Q + n0];
					double w1 = (k0 * n1 * 2 * Math.PI / P);
					WPr = Double.parseDouble(df.format(Math.cos(w1)));
					WPi = Double.parseDouble(df.format(-Math.sin(w1)));
					TempX1r = TempX1r + x[n1][n0] * WPr;
					TempX1i = TempX1i + x[n1][n0] * WPi;
				}
				X1r[k0][n0] = TempX1r;
				X1i[k0][n0] = TempX1i;
				double w2 = (k0 * n0 * 2 * Math.PI / N);
				WNr = Double.parseDouble(df.format(Math.cos(w2)));
				WNi = Double.parseDouble(df.format(-Math.sin(w2)));
				X11r[k0][n0] = X1r[k0][n0] * WNr - X1i[k0][n0] * WNi;
				X11i[k0][n0] = X1r[k0][n0] * WNi + X1i[k0][n0] * WNr;
			}

		}
		for (int k0 = 0; k0 < P; k0++) {
			for (int k1 = 0; k1 < Q; k1++) {
				double TempX2r = 0;
				double TempX2i = 0;
				for (int n0 = 0; n0 < Q; n0++) {
					double w = (k1 * n0 * 2 * Math.PI / Q);
					WQr = Double.parseDouble(df.format(Math.cos(w)));
					WQi = Double.parseDouble(df.format(-Math.sin(w)));
					TempX2r = TempX2r + X11r[k0][n0] * WQr - X11i[k0][n0] * WQi;
					TempX2i = TempX2i + X11r[k0][n0] * WQi + X11i[k0][n0] * WQr;
				}
				X2r[k0][k1] = TempX2r;
				X2i[k0][k1] = TempX2i;
				X2[k0][k1] = Math.sqrt(X2r[k0][k1] * X2r[k0][k1] + X2i[k0][k1] * X2i[k0][k1]);
				X[k0 + k1 * P] = Double.parseDouble(df.format(X2[k0][k1]));
				X[k0 + k1 * P] = X[k0 + k1 * P]/(N/2);
			}
		}
		return X;
	}



	public static double[] addZeroFFT(List<Integer> dataList) {
		// 不满足2的N次方的时域序列，用0补足，该FFT用蝶型计算
		int N0 = dataList.size();// 输入序列个数
		double e = Math.log((double) dataList.size()) / Math.log(2);
		int E = (int) Math.ceil(e);// 2的指数
		int N = (int) Math.pow(2, E);
		// 不足2的N次方的位数补0
		for (int i = 0; i < (N - N0); i++) {
			dataList.add(0);
		}
		// 按时域抽取的基-2FFT算法
		// 对输入数据即x(n)进行二进制倒序排序
		double x[] = new double[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			String binary = Integer.toBinaryString(i);
			if (binary.length() < E) {
				int N1 = E - binary.length();
				for (int j = 0; j < N1; j++) {
					String zero = "0";
					binary = zero + binary;
				}
			}
			char[] buffer = binary.toCharArray();
			StringBuffer strBuffer = new StringBuffer();
			for (int j = buffer.length - 1; j >= 0; j--) {
				strBuffer.append(buffer[j]);
			}
			String newBinary = strBuffer.toString();
			int newInteger = Integer.valueOf(newBinary, 2);
			x[i] = dataList.get(newInteger);
			// System.out.println("X["+i+"]值为："+x[i]);
		}

		// System.out.println(x[127]);检验倒序是否正确的

		// 有N阶蝶形图的级联，为节省存储空间，进行原位计算
		// 输入排序后的x1(n)序列，求出最后的X(K)

		// X(k)的实部是Xr，虚部是Xi；中间结果实部存在TempXr，虚部存在TempXi；相移因子的实部是Wr，虚部是Wi,用于蝶型计算。
		double[] Xr = new double[N];
		double[] Xi = new double[N];
		double[] TempXr = new double[N];
		double[] TempXi = new double[N];
		double[] Wr = new double[N / 2];
		double[] Wi = new double[N / 2];

		// 初始化X(k)，为输入x1(n)序列的初始值
		for (int i = 0; i < N; i++) {
			Xr[i] = x[i];
			Xi[i] = 0;// 输入是实数，没有虚部
		}

		// 进行E阶级联的蝶型计算
		int r = 1;
		for (int i = 0; i < E; i++) {
			// 在第i阶段有m组蝶型进行运算，每组r个输入，相移因子有r/2个。
			r = r * 2;
			int m = N / r;
			// 计算第i阶段的相移因子的个数和值
			for (int j = 0; j < (r / 2); j++) {
				double w = j * 2 * Math.PI / r;
				Wr[j] = Math.cos(w);
				Wi[j] = -Math.sin(w);
			}

			// 把上一次的计算结果作为输入序列，复制一次放缓存里，蝶型计算中用到
			for (int j = 0; j < N; j++) {
				TempXr[j] = Xr[j];
				TempXi[j] = Xi[j];
				// System.out.println("第"+Math.log(r)/Math.log(2)+"阶的"+"暂存器初始值："+TempXr[j]+"+i*"+TempXi[j]);
			}

			// System.out.println(Wr);
			// 对m个蝶型进行运算
			for (int k = 0; k < m; k++) {
				int index = 0;
				// 在第k个蝶型中只需计算前一半数的值，后一半根据公式直接求出，即计算r/2次
				// 共m组，每一组数都有r/2个，第k组数下标是从k*r——k*r+r/2；而相移因子个数也是r/2个，但下标从0开始
				for (int j = k * r; j < (k * r + r / 2); j++) {
					// 根据周期性：X[j] = G[j] + H[j]*W[index]；X[j+r/2] = G[j] -
					// H[j]*W[index]
					// 上述乘法部分结果的实部为X1，虚部为X2
					double X1 = TempXr[j + r / 2] * Wr[index] - TempXi[j + r / 2] * Wi[index];
					double X2 = TempXi[j + r / 2] * Wr[index] + TempXr[j + r / 2] * Wi[index];
					Xr[j] = TempXr[j] + X1;
					Xi[j] = TempXi[j] + X2;
					Xr[j + r / 2] = TempXr[j] - X1;
					Xi[j + r / 2] = TempXi[j] - X2;
					index = index + 1;
				}
			}
		}
		double[] Xk = new double[N];
		for (int i = 0; i < N; i++) {
			Xk[i] = Math.sqrt(Xr[i] * Xr[i] + Xi[i] * Xi[i]);
		}
		return Xk;

	}

}
