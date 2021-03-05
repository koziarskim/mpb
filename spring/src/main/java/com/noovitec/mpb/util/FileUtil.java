package com.noovitec.mpb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {
	
	public static String readFile(String path) throws IOException {
		ClassLoader classLoader = FileUtil.class.getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		String context = new String(Files.readAllBytes(file.toPath()));
		return context;
	}

}
