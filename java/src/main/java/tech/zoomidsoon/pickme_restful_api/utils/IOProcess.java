package tech.zoomidsoon.pickme_restful_api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

public class IOProcess {
	private static String storageFolder = Utils.getEnv("STORAGE_FOLDER", "./storage");

	public static void writeToFile(byte[] buffer, String filePath, boolean overwrite)
			throws FileAlreadyExistsException, IOException {
		File file = Paths.get(IOProcess.storageFolder, filePath).toAbsolutePath().toFile();
		file.getParentFile().mkdirs();

		if (!overwrite && file.exists())
			throw new FileAlreadyExistsException(file.getAbsolutePath());

		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(buffer);
		}
	}

	public static void deleteFile(String filePath)
			throws FileNotFoundException, SecurityException, IOException {
		File file = Paths.get(IOProcess.storageFolder, filePath).toAbsolutePath().toFile();

		if (!file.exists())
			throw new FileNotFoundException(file.getCanonicalPath());

		file.delete();
	}

	public static FileInputStream readFromFile(String filePath) throws FileNotFoundException, IOException {
		File file = Paths.get(IOProcess.storageFolder, filePath).toAbsolutePath().toFile();

		if (!file.exists())
			throw new FileNotFoundException("Something went wrong");

		return new FileInputStream(file);
	}
}