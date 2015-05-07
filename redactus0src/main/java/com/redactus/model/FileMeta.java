package com.redactus.model;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
@JsonIgnoreProperties({"bytes"})
@Document(collection = FileMeta.COLLECTION_NAME)
public class FileMeta implements Serializable {
	public static final String COLLECTION_NAME = "fs.files";
	@Id
    private Long id;
	private String fileName;
	private String fileSize;
	private String fileType;
	private String uuid;
	private boolean ready;
	private byte[] bytes;
	public void setId(Long id) {
        this.id = id;
    }
	public Long getId() {
        return id;
    }
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public String getUuid(){
		return this.uuid;
	}
	public void setFileUuid(String uuid){
		this.uuid = uuid;
	}
	@Override
	public String toString(){
		return String.format(
			"FileName:'%s', UUID:'%s' ",
			fileName,uuid);
	}
	public String getExtension(){
		String[] parts = this.fileName.split("\\.");
		String ext="."+parts[parts.length-1];
		return ext;
	}
}
