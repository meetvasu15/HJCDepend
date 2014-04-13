package edu.asu;

import java.util.ArrayList;
import java.util.List;

import edu.asu.hjcdepend.ResultStoreBean;
import edu.asu.html.parser.HtmlParser;

/*
 * This class compares lists of dependencies found and adds the errors and warning to foundIssuesList
 */
public class VerifyDependency {
	private DependencyStore dependencyStore;
	private List<ResultStoreBean> foundIssuesList;
	private String htmlFilepath;
	private HtmlParser htmlParser;
	
	public VerifyDependency(DependencyStore dependencyStore, List<ResultStoreBean> foundIssuesList, String htmlFilepath, HtmlParser htmlParser){
		this.dependencyStore = dependencyStore;
		this.foundIssuesList = foundIssuesList;
		this.htmlFilepath=htmlFilepath;
		this.htmlParser = htmlParser;
	}
	public  List<ResultStoreBean> initializeVerification(){
		checkAllHtmlEventListnerFunc();
		checkAllHTMLRefCSSSelector();
		 checkAllJsRefCSSStyle();
		return this.foundIssuesList;
	}
	/*
	 * This method checks for all HTML classes references. If the class attr is not found in the stylesheet, it is reported
	 */
	private void checkAllHTMLRefCSSSelector() {
		// TODO Auto-generated method stub
		ArrayList<String> allHTMLRef = dependencyStore.getHtmlAllCssDependencies();
		ArrayList<String> allCSSSelectors = dependencyStore.getCssAllCssSelectors();
		for(String oneHtmlRef: allHTMLRef){
			if(!allCSSSelectors.contains(oneHtmlRef)){
				 
				foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.HTML_TO_CSS, "Could not find css class '" +
						oneHtmlRef+"' in any CSS file"
						, this.htmlFilepath,htmlParser.getHTMLFileLineNumber(oneHtmlRef)));
			}
		}
	}
	/*
	 * This method checks all the event listner found on the html page against the list of all JS functions
	 */
	private void checkAllHtmlEventListnerFunc (){
		 ArrayList<String> htmlEventListnerFuncList =new ArrayList<String> (dependencyStore.getHtmlEventListnerFunc().keySet()); 
		 ArrayList<String> allFuncInJs= new ArrayList<String> (dependencyStore.getJsAllFuncInJs().keySet()); 
		 
		if(!Util.isBlankList(htmlEventListnerFuncList)){
			for(String oneFunction: htmlEventListnerFuncList){
				//check if this function is a browser built in
				 if(!getBrowserBuiltInFuncs().contains((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
					 //Now check if the function is in JS Files or not
					 String functionName = (oneFunction.substring(0, oneFunction.indexOf("("))).trim();
					if(!Util.isBlankString(functionName) && !functionName.equals("function") && !allFuncInJs.contains((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
						foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.HTML_TO_JAVASCRIPT, "Call to undefined function '" +
								(oneFunction.substring(0, oneFunction.indexOf("("))).trim()+"'"
								, this.htmlFilepath, htmlParser.getHTMLFileLineNumber(oneFunction)));
					}else if(allFuncInJs.contains((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
						//check function variadicity (Number of function params)
						if(dependencyStore.getHtmlEventListnerFunc().get(oneFunction) != null && dependencyStore.getHtmlEventListnerFunc().get(oneFunction) != dependencyStore.getJsAllFuncInJs().get((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
							foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.HTML_TO_JAVASCRIPT, "Invalid argument count of function '" +
									oneFunction+"' in HTML file"
									, this.htmlFilepath,  htmlParser.getHTMLFileLineNumber(oneFunction)));
						}
					}
				}
			}
		}
	}
	
	/*
	 * This method checks for all css classes referenced by JavaScript. If the class is not found in the stylesheet, it is reported
	 */
	private void checkAllJsRefCSSStyle() { 
		ArrayList<String> allJSRef = dependencyStore.getJsAllCssRef();
		ArrayList<String> allCSSSelectors = dependencyStore.getCssAllCssSelectors();
		for(String oneJsRef: allJSRef){
			if(!allCSSSelectors.contains(oneJsRef)){
				foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.HTML_TO_CSS, "JavaScript tried to apply unknown CSS class '" +
						oneJsRef+"' using .addClass method to HTML file"
						, null,null));
			}
		}
	}
	
	public static ArrayList<String> getBrowserBuiltInFuncs(){
		ArrayList<String> retList = new ArrayList<String>();
		retList.add("alert");
		retList.add("decodeURI");
		retList.add("decodeURIComponent");
		retList.add("encodeURI");
		retList.add("encodeURIComponent");
		retList.add("escape");
		retList.add("eval");
		retList.add("isFinite");
		retList.add("isNaN");
		retList.add("Number");
		retList.add("parseFloat");
		retList.add("parseInt");
		retList.add("String");
		retList.add("unescape");
		retList.add("atob");
		retList.add("blur");
		retList.add("btoa");
		retList.add("clearInterval");
		retList.add("clearTimeout");
		retList.add("close");
		retList.add("confirm");
		retList.add("createPopup");
		retList.add("focus");
		retList.add("moveBy");
		retList.add("moveTo");
		retList.add("open");
		retList.add("print");
		retList.add("prompt");
		retList.add("resizeBy");
		retList.add("resizeTo");
		retList.add("scroll");
		retList.add("scrollBy");
		retList.add("scrollTo");
		retList.add("setInterval");
		retList.add("setTimeout");
		retList.add("stop");
		retList.add("console.log");

		return retList;
	}
}
