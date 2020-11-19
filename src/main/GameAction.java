package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.TesseractException;

public class GameAction {
	private static Robot robot;
	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public static void keyTrigger(int key) {
		robot.keyPress(key);
		robot.keyRelease(key);
	}

	public static void enterText(String text) {
		var keysToPress = text.toCharArray();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		if (keysToPress == null || keysToPress.length <= 0)
			return;
		for (char c : keysToPress) {
			var mappedKey = KeyEvent.getExtendedKeyCodeForChar(c);
			robot.keyPress(mappedKey);
			robot.keyRelease(mappedKey);
		}
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	public static List<String> gameInfo(String screenImgUrl, String roiImgUrl) {
		List<String> result = new LinkedList<>();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// load input screen imgs
		Mat input = Imgcodecs.imread(screenImgUrl);
		Mat roi = Imgcodecs.imread(roiImgUrl);
		roi = MyUtils.scale(roi, screenSize.width, screenSize.height);
		// end
		// tien xu ly roi
		Imgproc.threshold(roi, roi, 128, 1, Imgproc.THRESH_BINARY);
		Mat applyRoi = new Mat();
		Core.multiply(input, roi, applyRoi);
		Imgcodecs.imwrite("applyRoi.jpg", applyRoi);
		Imgproc.cvtColor(applyRoi, applyRoi, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(applyRoi, applyRoi, new Size(1, 1));
		// end tien xu ly

		List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
		Mat hirachical = new Mat();
		Imgproc.findContours(applyRoi, contours, hirachical, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(applyRoi, contours, -1, new Scalar(204, 51, 0));
		MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];

		// get bounded rect
		Rect[] boundRect = new Rect[contours.size()];
		for (int i = 0; i < contours.size(); i++) {
			contoursPoly[i] = new MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
			boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
		}

		Mat drawing = Mat.zeros(applyRoi.size(), CvType.CV_8UC3);
		List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
		for (MatOfPoint2f poly : contoursPoly) {
			contoursPolyList.add(new MatOfPoint(poly.toArray()));
		}
		var fileNames = new ArrayList<String>();
		for (int i = 0; i < contours.size(); i++) {
			Scalar color = new Scalar(204, 51, 0);
			Imgproc.drawContours(drawing, contoursPolyList, i, color);
			Imgproc.rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 2);

			if (boundRect[i].area() >= 2000) {
				System.out.println(boundRect[i].area());
				Imgcodecs.imwrite("crop" + (i + 1) + ".jpg", input.submat(boundRect[i]));
				fileNames.add("crop" + (i + 1) + ".jpg");
				System.out.println("crop" + (i + 1));
			}
		}
		Imgcodecs.imwrite("drawing.jpg", drawing);

		for (String cropFile : fileNames) {
			try {

				String text = MyOcr.doOcr(new File(cropFile));
				result.add(text);

			} catch (TesseractException e) {

			}
		}

		return result;
	}

	public static void upToDateGame(GameInfo inforStorage, List<String> informationRead) {
		String reset = null;
		String level = null;
		try {
			var levelLine = informationRead.get(0).replaceAll("\\s+", "");
			reset = levelLine.substring(levelLine.indexOf("(") + 1, levelLine.indexOf(")"));
			level = levelLine.substring(levelLine.indexOf("Level") + "Level".length(), levelLine.indexOf("("));
			inforStorage.reset = Integer.parseInt(reset);
			inforStorage.level = Integer.parseInt(level);
		} catch (Exception e) {
			if (inforStorage.reset == 0)
				inforStorage.reset = 1;
			if (inforStorage.level == 0)
				inforStorage.level = 1;
		}

		var mapLine = informationRead.get(1).toLowerCase().replaceAll("\\s+", "");
		System.out.println(mapLine);
		String mapName = null;

		try {
			mapName = mapLine.split("\\d+,\\d+")[0];
			var mapLocation = mapLine.substring(mapName.length());

			inforStorage.mapName = mapName;
			inforStorage.location = new Point(Integer.parseInt(mapLocation.split(",")[0]),
					Integer.parseInt(mapLocation.split(",")[1]));
		} catch (Exception e) {
			if (inforStorage.level >= 100)
				inforStorage.mapName = "arena";
			else
				inforStorage.mapName = "lorencia";
			inforStorage.location = null;
		}
	}
}
