package tech.zoomidsoon.pickme_restful_api.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Base64.Decoder;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import tech.zoomidsoon.pickme_restful_api.helpers.Pair;
import tech.zoomidsoon.pickme_restful_api.utils.IOProcess;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Media extends Entity {
	private static Decoder base64Decoder = Base64.getDecoder();

	private String mediaName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer userId;
	private String mediaType;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String payload;

	@Override
	public boolean isEmpty() {
		return this.mediaName == null;
	}

	public void write() throws FileAlreadyExistsException, SecurityException, IOException, IllegalArgumentException {
		byte[] buffer;
		if (payload == null || (buffer = base64Decoder.decode(payload)) == null)
			throw new IllegalArgumentException("Payload format is null or not base64");

		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		IOProcess.createFolderInStorage(Integer.toString(this.userId));
		IOProcess.writeToFile(buffer, filePath, true);
	}

	public Pair<byte[], String> read() throws FileNotFoundException, IOException {
		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		byte[] buffer = IOProcess.readFromFile(filePath);
		String mediaType = Files.probeContentType(Paths.get(filePath));
		return new Pair<>(buffer, mediaType);
	}

	public void delete() throws SecurityException, IOException {
		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		IOProcess.deleteFile(filePath);
	}
}
