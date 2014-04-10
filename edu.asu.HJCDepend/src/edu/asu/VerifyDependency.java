package edu.asu;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.asu.hjcdepend.ResultStoreBean;

/*
 * This class compares lists of dependencies found and adds the errors and warning to foundIssuesList
 */
public class VerifyDependency {
	private DependencyStore dependencyStore;
	private List<ResultStoreBean> foundIssuesList;
	private String htmlFilepath;
	
	public VerifyDependency(DependencyStore dependencyStore, List<ResultStoreBean> foundIssuesList, String htmlFilepath){
		this.dependencyStore = dependencyStore;
		this.foundIssuesList = foundIssuesList;
		this.htmlFilepath=htmlFilepath;
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
				 
				foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.HTML_TO_CSS, "Could not find HTML class reference " +
						oneHtmlRef+" selector in any CSS file."
						, this.htmlFilepath,""));
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
				//oneFunction = (oneFunction.substring(0, oneFunction.indexOf("("))).trim();
				if(!allFuncInJs.contains((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
					foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.HTML_TO_JAVASCRIPT, "Could not find " +
							(oneFunction.substring(0, oneFunction.indexOf("("))).trim()+" event listner in JavaScript"
							, this.htmlFilepath,null));
				}else if(allFuncInJs.contains((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
					//check function variadicity (Number of function params)
					if(dependencyStore.getHtmlEventListnerFunc().get(oneFunction) != null && dependencyStore.getHtmlEventListnerFunc().get(oneFunction) != dependencyStore.getJsAllFuncInJs().get((oneFunction.substring(0, oneFunction.indexOf("("))).trim())){
						foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.HTML_TO_JAVASCRIPT, "In your HTML file, the argument count in event listner function '" +
								oneFunction+"' does not match parameter count in the JavaScript file"
								, this.htmlFilepath,null));
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
}
