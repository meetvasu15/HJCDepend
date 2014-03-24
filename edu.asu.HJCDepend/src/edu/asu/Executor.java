package edu.asu;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
import edu.asu.css.CssParser;
import edu.asu.hjcdepend.ResultStoreBean;
import edu.asu.html.HtmlUtil;
import edu.asu.html.parser.HtmlParser;
import edu.asu.javascript.parser.JavascriptParser;
public class Executor {

	 static Logger log = Logger.getLogger("edu.asu.Executor"); 
	private String htmlFilepath; 
	private String projectDirPath;
	private static DependencyStore dependencyStore;
	private List<ResultStoreBean> foundIssuesList;
	
	public Executor(){
		foundIssuesList = new ArrayList<ResultStoreBean>();
		dependencyStore = new DependencyStore();
	}
	public List<ResultStoreBean> runDependencyAnalyser(String[] prjctNhtmlPath) { 
		if(prjctNhtmlPath.length <2){
			return null;
		}
		foundIssuesList.clear();
		HtmlParser htmlParser ;
		
		this.projectDirPath =prjctNhtmlPath[0];		//Context path of the project
		this.htmlFilepath = prjctNhtmlPath[1]; 		//directory path of the html File
		if(!Util.isBlankString(this.projectDirPath) || !Util.isBlankString(this.htmlFilepath) ){
			try{
					htmlParser = new HtmlParser(this.htmlFilepath, false); // parse the html doc 
				log.info("Running dependency analysis for "+this.htmlFilepath); 
				Elements allElts = HtmlUtil.getAllHtmlElements(htmlParser.getDocumentObject());		//gets all the HTML Elements
				getDOMEvents(allElts);		//Gets all event Listeners on HTML Doc and stores in dependency store
				//Gets all js and css external dependencies links on the HTML document
				//stores the absolute path in the dependency store
				//logs error if it doesnot find a mentioned js or css file on disk
				getExternalLinkDependencies(htmlParser.getDocumentObject()); 
				
				String jsString = fetchFileFromDisk(dependencyStore.getHtmlAllJsExtLinks(), Constants.HTML_TO_JAVASCRIPT);//returns the content of all the js files
				String cssString = fetchFileFromDisk(dependencyStore.getHtmlAllCssExtLinks(), Constants.HTML_TO_CSS);//returns the content of all the css files
				System.out.println(cssString);
				CssParser.readCSS30(cssString);
				buildDOMDependencies(allElts); // gets all css dependencies and stores in dependencyStore, 
											   // also get all possible references a javascript could make
											   // store it in dependencyStore.allHTMLAccessors
				
				parseJavascript(jsString);// entry point for js parsing
	
				VerifyDependency vd = new VerifyDependency(dependencyStore, this.foundIssuesList, this.htmlFilepath);
				vd.initializeVerification();
			}catch(Exception e){
				e.getMessage();
			}
		}else{
			foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.HJC_ERROR, Constants.CURRENT_FILE_NOT_HTML
					,null,null));
			log.error("FATAL HJC ERROR: No html file/ text found or could not acquire project context path.");
			return null;
		}
		return foundIssuesList;
	}

	
	
	
	// O(n^2) :( Think! Think! Think!
	// distinguish in js written in the DOM itself and external function call
	// we need to get more intelligent on eventlisteners being attached by javascript...
	// But to me its a Javascript to HTML dependency not the other way... :|
	//This method lists all the event Listeners present in the HTML document.
	public HashMap<String, String> getDOMEvents(Elements allElts){
		 HashMap<String, String> retMap = new HashMap<String, String>();
		ArrayList<String> allDomEventList= HtmlUtil.getAllHtmlListeners();
		Attributes attrs;
		log.info("Listing all HTML --> Javascript dependencies:-\n");
		for(Element oneElt: allElts){
			attrs = oneElt.attributes();
			for (Attribute oneAttr: attrs){
				if(allDomEventList.contains(oneAttr.getKey().trim())){
					retMap.put(oneAttr.getKey(), oneAttr.getValue());
					
					log.info("Listener Type: "+oneAttr.getKey()+", dependent JS: "+oneAttr.getValue());
					dependencyStore.getHtmlEventListnerFunc().add(oneAttr.getValue());
					
				}
			}
			
		}
		return retMap;
	}
	
	// again this is O(n^2), we need to modify and merge this method with getDomEvents coz the same
	//two lists are being iterated in nested for loops
	// This method only looks for class attribute in an element for now
	public  HashMap<String, String> buildDOMDependencies(Elements allElts){
		 HashMap<String, String> retMap = new HashMap<String, String>();
		 ArrayList<String> allDomCssDepList= HtmlUtil.getAllHtmlCssSelectorList();
		 ArrayList<String> allDomPossibleAccessorList= HtmlUtil.getAllHtmlSelectorList();
		Attributes attrs;
		log.info("Listing all HTML --> Css3 dependencies:-\n");
		for(Element oneElt: allElts){
			attrs = oneElt.attributes();
			for (Attribute oneAttr: attrs){
				if(allDomCssDepList.contains(oneAttr.getKey().trim())){
					retMap.put(oneAttr.getKey(), oneAttr.getValue());
					log.info("Attr Type: "+oneAttr.getKey()+", dependent CSS class: "+oneAttr.getValue());
					dependencyStore.getHtmlAllCssDependencies().add(oneAttr.getValue());
				}
				if(allDomPossibleAccessorList.contains(oneAttr.getKey().trim())){
					dependencyStore.getHtmlallHtmlAccessors().add(oneAttr.getValue());
				}
			}
			
		} 
		return retMap;
		
	}
	
	// This method gets all JS and CSS links that are external to the HTML web Page and
	// stores them in dependencyStore
	
	public HashMap<String, String> getExternalLinkDependencies(Document doc){
		HashMap<String, String> retMap = new HashMap<String, String>();
		// get all JS links
		log.info("Listing all HTML --> JS ext dpendencies:-\n");
		Elements allElts = doc.select("script");
		Attributes attrs;
		for(Element oneElt: allElts){
			attrs = oneElt.attributes();
			if(attrs.hasKey("src")){
				retMap.put("js", attrs.get("src"));
				//log.info(attrs.get("src"));
				//System.out.println(calculateAbsDiskPath(attrs.get("src")));
				
				// This if block checks whether the external link provided points to a file or not
				if(calculateAbsDiskPath(attrs.get("src")) != null){
					dependencyStore.getHtmlAllJsExtLinks().add(calculateAbsDiskPath(attrs.get("src")));
				}else{
					ResultStoreBean rs = new ResultStoreBean();
					rs.setFileName(this.htmlFilepath);
					rs.setSeverity(Constants.ERROR);
					rs.setDescription(Constants.JS_FILE_NOT_FOUND);
					rs.setType(Constants.HTML_TO_JAVASCRIPT); 
					foundIssuesList.add(rs);
				}
			}
			
		} 
		
		//TO-DO: implement new logic for extracting css external links
		// get all CSS links
		log.info("Listing all HTML --> CSS ext dpendencies:-\n");
		Elements allCSSElts = doc.select("link");
		Attributes CSSAttrs;
		for(Element oneElt: allCSSElts){
			CSSAttrs = oneElt.attributes();
			if((CSSAttrs.hasKey("rel") && CSSAttrs.get("rel").equalsIgnoreCase("stylesheet"))
					|| (CSSAttrs.hasKey("type") && CSSAttrs.get("type").equalsIgnoreCase("text/css"))){
				retMap.put("css", CSSAttrs.get("href"));
				if(calculateAbsDiskPath( CSSAttrs.get("href")) != null){
					dependencyStore.getHtmlAllCssExtLinks().add(calculateAbsDiskPath(CSSAttrs.get("href")));
				}else{
					ResultStoreBean rs = new ResultStoreBean();
					rs.setFileName(this.htmlFilepath);
					rs.setSeverity(Constants.ERROR);
					rs.setDescription(Constants.CSS_FILE_NOT_FOUND);
					rs.setType(Constants.HTML_TO_CSS); 
					foundIssuesList.add(rs);
				}
				
				//dependencyStore.getHtmlAllCssExtLinks().add(CSSAttrs.get("href"));
				//log.info(CSSAttrs.get("href"));
			}
			
		} 
		return retMap;
	}
	/*
	 * Entry point of Javascript parser
	 */
	public void parseJavascript(String jsString){
		JavascriptParser jp = new JavascriptParser(dependencyStore,foundIssuesList);
		jp.parser(jsString);
	}
	/*
	 * This method fetches javascript files from URL and returns them as a string  
	 */
/*	public String fetchJavascriptFromUrl(ArrayList<String> UrlList){
		StringBuilder allJavascriptContent= new StringBuilder();
		for(String oneURL: UrlList){
			   try {
				URL oneJsFile = new URL(oneURL);
				 BufferedReader in = new BufferedReader(new InputStreamReader(oneJsFile.openStream()));
					        String inputLine;
					        while ((inputLine = in.readLine()) != null){
					        	allJavascriptContent.append(inputLine+" \n");
					        }
					        in.close();
			} catch (MalformedURLException e) {
				log.error("ERROR in fetching Javascript from the HTML Page, probably because of a malformed URL");
				e.printStackTrace();
			} catch (IOException e) {
				log.error("ERROR in fetching Javascript from the HTML Page");
				e.printStackTrace();
			}
		}
		return allJavascriptContent.toString();
	}
	 */
	/*
	 * This method takes in a list of all js ext links found on the HTML file and return one string of the js files content
	 */
	public String fetchFileFromDisk(ArrayList<String> diskPathList, String dependencyType){
		StringBuilder allJavascriptContent= new StringBuilder();
		for(String onePath:diskPathList){
			try{
				String readContnt = readFileFromDisk(onePath);
				if(!Util.isBlankString(readContnt)){
					allJavascriptContent.append(readContnt);
				}else{
					foundIssuesList.add(new ResultStoreBean(Constants.WARNING, dependencyType, Constants.EMPTY_FILE
							+onePath, this.htmlFilepath,null));
				
				}
				
			}catch(IOException e){
				foundIssuesList.add(new ResultStoreBean(Constants.ERROR,dependencyType, Constants.COULD_NOT_READ_FILE+
						onePath, this.htmlFilepath,null));
			}
		}
		
		return allJavascriptContent.toString();
	}
	/*
	 * This method reads a file from absolute system path fed to it
	 * @returns a string of the file
	 */
	public String readFileFromDisk(String absolutePath) throws IOException {
		StringBuilder lineRead = new StringBuilder();
		String currentLine;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(absolutePath));

			while ((currentLine = br.readLine()) != null) {
				lineRead.append(currentLine+"\n");
			}
		} catch (IOException io) {
			io.printStackTrace();
			System.out.println("error in fetching web content from the URL");

			throw new IOException(io);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				//ex.printStackTrace();
			}
		}
		return lineRead.toString();
	}
	/*
	 *  Possible path start to handle
	 *   	 - nothing, we need to pick up the file relative to the location of html file
	 *   ../ - we need to go back a dir or even more, this can get recursive
	 *   /   - we need to start from the context dir of the project 
	 *   
	 */
	public String calculateAbsDiskPath(String relativePathInHtml){ 
		String homeOfHtml = this.htmlFilepath.substring(0, this.htmlFilepath.lastIndexOf("/")+1);
		if(!Util.isBlankString(relativePathInHtml)){
			if(relativePathInHtml.startsWith("/")){
				return this.projectDirPath+relativePathInHtml ;
				
			}else if(relativePathInHtml.startsWith("../")){
				return moveUpDir(homeOfHtml, relativePathInHtml);
			}else if(relativePathInHtml.startsWith("./")){
				return  homeOfHtml+relativePathInHtml.substring(2);
			}else{
				System.out.println(homeOfHtml+relativePathInHtml);
				return homeOfHtml+relativePathInHtml;
			}
		}
		
		return null;
	}
	/*
	 * recursive function that resolves ../ in path
	 */
	
	public String moveUpDir( String currentCntxt, String filePath){
		if(filePath.startsWith("../")){
			if(currentCntxt.endsWith("/")){
				currentCntxt= currentCntxt.substring(0, currentCntxt.length()-1);
			}
			if(currentCntxt.length()<1){
				return null;
			}
			return moveUpDir(currentCntxt.substring(0, currentCntxt.lastIndexOf("/")+1), filePath.substring(3));
		}else{
			return currentCntxt+filePath;
		} 
	}
}
