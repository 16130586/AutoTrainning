package main;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MyUtils {
	public static Mat scale(Mat src, int width, int height) {
		Mat rs = new Mat();
		Imgproc.resize(src, rs, new Size(width, height));
		return rs;
	}
}
