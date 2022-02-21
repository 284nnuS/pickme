package tech.zoomidsoon.pickme_restful_api.models;

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
	private String payload;

	@Override
	public boolean isEmpty() {
		return this.mediaName == null;
	}

	public void write() throws Exception {
		byte[] buffer = base64Decoder.decode(payload);
		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		IOProcess.createFolder(Integer.toString(this.userId));
		IOProcess.writeToFile(buffer, filePath, true);
	}

	public Pair<byte[], String> read() throws Exception {
		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		byte[] buffer = IOProcess.readFromFile(filePath);
		String mediaType = Files.probeContentType(Paths.get(filePath));
		return new Pair<>(buffer, mediaType);
	}

	public void delete() {
		String filePath = String.format("./%s/%s", this.userId, this.mediaName);
		IOProcess.deleteFile(filePath);
	}
}
