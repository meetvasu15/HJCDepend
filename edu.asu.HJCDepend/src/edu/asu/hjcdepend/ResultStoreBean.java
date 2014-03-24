package edu.asu.hjcdepend;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ResultStoreBean {
	private String severity;
	private String type;
	private String description;
	private String fileName;
	private String lineNo; 

	public ResultStoreBean() {

	}

	public ResultStoreBean(String severity, String type, String description,
			String fileName, String lineNo) {
		this.description = description;
		this.fileName = fileName;
		this.lineNo = lineNo;
		this.severity = severity;
		this.type = type;

	}


	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
}
