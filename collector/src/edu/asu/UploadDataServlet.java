package edu.asu;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.asu.uploadData.Payload;

public class UploadDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			if(request.getInputStream() != null){
				ObjectInputStream objIn = new ObjectInputStream(
						request.getInputStream());
				
				Object obj = objIn.readObject();
				if(request.getInputStream() != null){
					Payload newPayload = (Payload) obj;
					System.out.println("File Name recvd::: "+newPayload.getFileName());
						if(newPayload.getFileContent() != null){
							 byte fileContent[]  = newPayload.getFileContent();
							 String strFileContent = new String(fileContent);
						     
						       System.out.println("File content : ");
						       System.out.println(strFileContent);
						}else{
							System.out.println("No FIle content received");
						}
				    }else{
				    	System.out.println("No data received");
				    }
				}else{
					System.out.println("No data received");
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
