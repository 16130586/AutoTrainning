package main;

import java.util.LinkedList;
import java.util.Queue;

import org.opencv.core.Point;

public class GameInfo {
	public static Queue<Integer> keyQueue = new LinkedList<>();
	public static int clickInterval = 100;
	public String mapName = null;
	public Point location = null;
	public int level;
	public int reset;
}
