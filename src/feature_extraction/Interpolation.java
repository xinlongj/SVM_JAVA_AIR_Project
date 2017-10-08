package feature_extraction;

public class Interpolation {
	double x[], y[];
	double h[]; // Set h[i] = x[i] - x[i-1]
	int n;

	public Interpolation(double[] x, double[] y, int n) {
		this.x = x;
		this.y = y;
		this.n = n;
	}

	public double[] insert(double[] xx) {
		double M[] = getParameter();// 求出s(x)的参数Mi-Mn,设M[i]=S(x[i])的二阶导
		double newValue[] = new double[xx.length];
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < xx.length; j++) {
				if (xx[j] > x[i] && xx[j] < x[i + 1]) {
					// 插值公式S(x)
					// 利用插值条件S(x[i]) = y[i]求出连续两次积分产生的两个参数c1和c2
					double c1 = (y[i + 1] - y[i]) / h[i] - (h[i] / 6) * (M[i + 1] - M[i]);
					double c2 = (y[i] * x[i + 1] - y[i + 1] * x[i]) / h[i]
							- (M[i] * x[i + 1] - M[i + 1] * x[i]) * h[i] / 6;
					newValue[j] = M[i + 1] * Math.pow((xx[j] - x[i]), 3) / (6 * h[i])
							- M[i] * Math.pow((xx[j] - x[i + 1]), 3) / (6 * h[i]) + xx[j] * c1 + c2;
				} else if (xx[j] == x[i]) {
					newValue[j] = y[i];
				} else if (xx[j] == x[i + 1]) {
					newValue[j] = y[i + 1];
				}
			}
		}
		return newValue;
	}

	public double[] getParameter() {
		double r[] = new double[n - 1];// gamma系数
		double a[] = new double[n - 1];// alpha系数
		double b[] = new double[n];// β系数
		h = new double[n - 1];// 求求上面三个系数用到的中间数，其中h[i]=x[i]-x[i-1]
		for (int i = 0; i < n - 1; i++) {
			h[i] = x[i + 1] - x[i];
		}
		a[0] = 1;
		for (int i = 0; i < n - 2; i++) {
			r[i] = h[i] / (h[i] + h[i + 1]);// 系数gamma[i]表达式，其中i=1,..,n-1
			a[i + 1] = 1 - r[i];// 系数gamma[i]和alpha[i]的关系
		}
		r[n - 2] = 1;// gamma[n]

		// 下面求β0和βn时，近似求的y0和yn的导数，如有误差，此处嫌疑最大
		double y11 = (y[1] - y[0]) / (x[1] - x[0]);
		double yn1 = (y[n - 1] - y[n - 2]) / (x[n - 1] - x[n - 2]);
		b[0] = 6 / h[0] * ((y[1] - y[0]) / h[0] - y11);
		for (int i = 1; i < n - 1; i++) {
			b[i] = 6 / (h[i - 1] + h[i]) * ((y[i + 1] - y[i]) / h[i] - (y[i] - y[i - 1]) / h[i - 1]);
		}
		b[n - 1] = 6 / h[n - 2] * (yn1 - (y[n - 1] - y[n - 2]) / h[n - 2]);

		double M[] = zhuigan(r, a, b);// 追赶法求出M1-Mn
		return M;
	}

	// 追赶法求解三角矩阵方程组
	public double[] zhuigan(double[] a, double[] c, double[] d) {
		double b[] = new double[n];
		for (int i = 0; i < n; i++) {
			b[i] = 2;
		}
		// gamma，alpha，beta是三角矩阵A分解成LU的三个参数，根据推出的三个公式求出三个系数
		double gamma[] = new double[n - 1];
		double alpha[] = new double[n];
		double beta[] = new double[n - 1];

		for (int i = 0; i < n - 1; i++) {
			gamma[i] = a[i];
		}
		alpha[0] = b[0];
		for (int i = 1; i < n; i++) {
			beta[i - 1] = c[i - 1] / alpha[i - 1];
			alpha[i] = b[i] - gamma[i - 1] * beta[i - 1];
		}

		// 矩阵A分解成A=LU 则求解三角矩阵方程组AM=d，转化成L(UM)=d M是未知数待求
		// 求解LV=d方程组，顺序追赶：
		double V[] = new double[n];
		V[0] = d[0] / alpha[0];
		for (int i = 1; i < n; i++) {
			V[i] = (d[i] - gamma[i - 1] * V[i - 1]) / alpha[i];
		}

		// 求解UM=V方程组，倒序追赶：
		double M[] = new double[n];
		M[n - 1] = V[n - 1];
		for (int i = n - 2; i > 0; i--) {
			M[i] = V[i] - beta[i] * M[i + 1];
		}
		return M;
	}
}