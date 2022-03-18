package tech.zoomidsoon.pickme_restful_api.models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Base64;
import java.util.Base64.Decoder;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.utils.IOProcess;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class File extends Entity {
	private String fileName;
	private String bucketName;
	private Integer userId;
	private String mimeType;

	private static Decoder decoder = Base64.getDecoder();

	@Override
	public boolean isEmpty() {
		return this.fileName == null;
	}

	public void write(String b64)
			throws FileAlreadyExistsException, SecurityException, IOException, IllegalArgumentException {
		String filePath = String.format("./%s/%s/%s", userId, bucketName, fileName);
		IOProcess.writeToFile(decoder.decode(b64), filePath, true);
	}

	public FileInputStream read() throws FileNotFoundException, IOException {
		String filePath = String.format("./%s/%s/%s", userId, bucketName, fileName);
		FileInputStream stream = IOProcess.readFromFile(filePath);
		return stream;
	}

	public void delete() throws SecurityException, IOException {
		String filePath = String.format("./%s/%s/%s", userId, bucketName, fileName);
		IOProcess.deleteFile(filePath);
	}
}
