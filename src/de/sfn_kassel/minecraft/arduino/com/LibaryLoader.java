package de.sfn_kassel.minecraft.arduino.com;

import java.io.File;

public class LibaryLoader {

	private static final String[] LIBARYS_WINDOWS = {
		"rxtxParallel.dll",
		"rxtxSerial.dll"
	};
	@SuppressWarnings("unused")
	private static final String[] LIBARYS_LINUX = {
		"librxtxParallel.so",
		"librxtxSerial.so"
	};

	public static void loadLibarys() {
		String[] LIBARYS = LIBARYS_WINDOWS;

		char fSep = File.separatorChar;
		String path = "plugins"+fSep+"ArduinoMCPlugin"+fSep+"lib";

		for (String libary : LIBARYS) {
			if (new File(path+fSep+libary).exists())
				try {
					System.out.println("trying to load "+new File(path+fSep+libary).getAbsolutePath());
					System.load(new File(path+fSep+libary).getAbsolutePath());
//					System.loadLibrary(new File(path+fSep+libary).getAbsolutePath());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			else
				System.err.println("can not load libary "+new File(path+fSep+libary).getAbsolutePath());
//			info("load: "+new File(libary).getAbsolutePath());
		}
	}
}
