package main;

import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;

public class MyOcr {
	
	private static final String DEFAULT_LANGUGE = "eng";
	private static final int DEFAULT_OEM_MODE = TessOcrEngineMode.OEM_LSTM_ONLY;
	public static String testdataStorage = "./tessdata";
	private static Tesseract tesseract;
	
	public MyOcr() {
		
	}
	static {
		tesseract = new Tesseract();
		tesseract.setOcrEngineMode(DEFAULT_OEM_MODE);
		tesseract.setLanguage(DEFAULT_LANGUGE);
		tesseract.setDatapath(testdataStorage);
		tesseract.setPageSegMode(7);

	}
	public static String doOcr(File file) throws TesseractException {
		return tesseract.doOCR(file);
	}
}
