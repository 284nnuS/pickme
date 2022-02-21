package tech.zoomidsoon.pickme_restful_api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOProcess {
	
	public static void writeToFile(byte[] buffer,
			String filePath, boolean overwrite) throws Exception {
				
		File file = new File(filePath);

		if (!overwrite && file.exists())
			throw new FileAlreadyExistsException(file.getAbsolutePath());

		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(buffer);
		}
	}

	public static boolean createFolder(String folderName) {
		Path path = Paths.get("./" + folderName);

		try {
			if (Files.exists(path) && !Files.isDirectory(path))
				Files.delete(path);
			Files.createDirectory(path);
			return true;
		} catch (IOException e) {
			// Permission issue
		}
		return false;
	}

	public static Boolean deleteFile(String filePath) {
		File file = new File(filePath);

		if (file.exists()) {
			try {
				file.delete();
				return true;
			} catch (Exception e) {
				// Permission issue
			}
		}
		return false;
	}

	public static byte[] readFromFile(String filePath) throws Exception {
		File file = new File(filePath);

		if (!file.exists()) 
			throw new FileNotFoundException("Something went wrong");

		byte[] buffer = new byte[(int)file.length()];

		try (FileInputStream in = new FileInputStream(file)) {
			in.read(buffer);
		}

		return buffer;
	}
}