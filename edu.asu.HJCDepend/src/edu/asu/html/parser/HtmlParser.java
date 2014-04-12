package edu.asu.html.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlParser {

	/**
	 * @param args
	 */
	// private String htmlContent;
	private Document doc;

	public HtmlParser(String path, boolean isUrl) {

		BufferedReader br = null;
		try {
			if (!isUrl) { 
				// we gotta read the html this is the complete path of the html
				// file.
				StringBuilder lineRead = new StringBuilder();
				String currentLine;
			//	System.out.println(path);
				br = new BufferedReader(new FileReader(path));

				while ((currentLine = br.readLine()) != null) {
					lineRead.append(currentLine);
				}

				this.doc = Jsoup.parse(lineRead.toString());
			} else {
				this.doc = Jsoup.connect(path).get();

			}
		} catch (IOException io) {
			io.printStackTrace();
			System.out.println("error in fetching web content from the URL");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public Integer searchEvents(String functionName){
		Integer retVal = 0;
		//	this.doc.getElementsByAttributeValueContaining(key, match)
		return retVal;
	}
	public Document getDocumentObject() {
		return this.doc;
	}

}
