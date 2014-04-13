package edu.asu.html.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.asu.Util;

public class HtmlParser {

	/**
	 * @param args
	 */
	// private String htmlContent;
	private Document doc;
	private String htmlWithLineNumber;
	private String hjcStamp = "\\hjcstamp";
	private String hjcEndStamp = "\\hjcendstamp";

	public HtmlParser(String path, boolean isUrl) {
		StringBuilder htmlStrWithLineNum = new StringBuilder();
		BufferedReader br = null;
		try {
			if (!isUrl) { 
				// we gotta read the html this is the complete path of the html
				// file.
				StringBuilder lineRead = new StringBuilder();
				String currentLine;
			//	System.out.println(path);
				br = new BufferedReader(new FileReader(path));
				int count = 1;
				while ((currentLine = br.readLine()) != null) {
					lineRead.append(currentLine+"\n");
					htmlStrWithLineNum.append(currentLine+hjcStamp+count+hjcEndStamp);
					count+=1;
				}

				this.doc = Jsoup.parse(lineRead.toString());
				this.htmlWithLineNumber = htmlStrWithLineNum.toString();
				//System.out.println(this.htmlWithLineNumber);
				//getEmbeddedJavaScript();
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
	
	public String getEmbeddedJavaScript(){
		StringBuilder retStr=new StringBuilder("");
		Elements scripts = doc.select("script"); // Get the script part
		Attributes attrs;
		for(Element oneElt: scripts){
			attrs = oneElt.attributes();
			if(!attrs.hasKey("src")){
				retStr.append(oneElt.html());
			}
		}
		return retStr.toString();
	}
	public Document getDocumentObject() {
		return this.doc;
	}
	public String getHTMLFileLineNumber(String toFind){
		
		String retStr="";
			if(!Util.isBlankString(toFind)){
			toFind = "\""+toFind+"\"";
			int indexOftoFindStr= this.htmlWithLineNumber.indexOf(toFind);
			if( indexOftoFindStr != 0)
			{
				int indexOfHjcStamp = this.htmlWithLineNumber.indexOf(hjcStamp,indexOftoFindStr );
				int indexOfHjcEndStamp = this.htmlWithLineNumber.indexOf(hjcEndStamp,indexOftoFindStr );
				int startIndexOflineNumber = indexOfHjcStamp+hjcStamp.length();
				if(indexOfHjcEndStamp < this.htmlWithLineNumber.length()){
				retStr = this.htmlWithLineNumber.substring(startIndexOflineNumber, indexOfHjcEndStamp);
				}
			}
			
		}
		return retStr;
	}

}
