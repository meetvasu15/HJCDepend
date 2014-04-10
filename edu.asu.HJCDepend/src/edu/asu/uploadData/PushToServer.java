package edu.asu.uploadData;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import edu.asu.Util;

public class PushToServer {
	 
		private String serverUrl; 
		private String userID;
		
		public PushToServer(String _serverUrl, String _userID){ 
			serverUrl = _serverUrl;
			userID = _userID;
		}
		public void sendFile(){
			
			//FileInputStream fis = null;
			
			Payload filePayload = new Payload();
			if(!Util.isBlankString(userID))
				filePayload.setUserID(userID);
			filePayload.setFileName("logfile.log");
			URL url;
			try {
			        url = new URL("platform:/plugin/edu.asu.HJCDepend/log/hjcdepend.log");
			    InputStream inputStream = url.openConnection().getInputStream();
			    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			    String inputLine;
			 
			    while ((inputLine = in.readLine()) != null) {
			        System.out.println(inputLine);
			    }
			 
			    in.close();
			 
			} catch (IOException e) {
			    e.printStackTrace();
			}
			sendObject(filePayload);
		}
		private Object sendObject(Object obj) {

			Object reply = null;
			try {
				URLConnection conn = null;
				URL servletURL = new URL(serverUrl);
				try {
					// open URL connection
					conn = servletURL.openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					// send object
					ObjectOutputStream objOut = new ObjectOutputStream(
							conn.getOutputStream());
					objOut.writeObject(obj);
					objOut.flush();
					objOut.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					return null;
				}
				// recieve reply
				try {
					ObjectInputStream objIn = new ObjectInputStream(
							conn.getInputStream());
					reply = objIn.readObject();
					objIn.close();
				} catch (Exception ex) {
					// it is ok if we get an exception here
					// that means that there is no object being returned
					if (!(ex instanceof EOFException))
						ex.printStackTrace();
					// System.err.println("*");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return reply;
		}
}
