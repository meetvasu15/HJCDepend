package edu.asu.uploadData;

public class Payload implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private byte fileContent[];

	private String userID;
	
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte fileContent[]) {
		this.fileContent = fileContent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
}
