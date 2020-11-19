package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



import org.opencv.core.Core;

public class Launcher {
	
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		var gameInfo = new GameInfo();
		List<String> gameInfoAsText = GameAction.gameInfo("input.png" , "roi.png");
		System.out.println(Arrays.toString(gameInfoAsText.toArray()));
		GameAction.upToDateGame(gameInfo, gameInfoAsText);
		System.out.println(gameInfo.reset);
		System.out.println(gameInfo.level);
		System.out.println(gameInfo.mapName);
		if(gameInfo.location != null)
			System.out.println(gameInfo.location.x + "," + gameInfo.location.y);
//		System.out.println(gameInfoAsText.get(0).trim());
//		System.out.println(gameInfoAsText.get(1).trim());
		
//		try {
//			Robot robot = new Robot();
//			String format = "jpg";
//			String fileName = "FullScreenshot." + format;
//			Thread.sleep(2000);
//			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//			BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
//			ImageIO.write(screenFullImage, format, new File(fileName));
//			
//			KeyPressingText(robot, "/move arena");
//			
//			System.out.println("A full screenshot saved!");
//		} catch (AWTException | IOException ex) {
//			System.err.println(ex);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
