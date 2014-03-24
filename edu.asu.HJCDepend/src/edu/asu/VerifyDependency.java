package edu.asu;

import java.util.ArrayList;
import java.util.List;

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
		
		return this.foundIssuesList;
	}
	
	/*
	 * This method checks all the event listner found on the html page against the list of all JS functions
	 */
	private void checkAllHtmlEventListnerFunc (){
		 ArrayList<String> htmlEventListnerFuncList = dependencyStore.getHtmlEventListnerFunc();
		 ArrayList<String> allFuncInJs= dependencyStore.getJsAllFuncInJs();
		if(!Util.isBlankList(htmlEventListnerFuncList)){
			for(String oneFunction: htmlEventListnerFuncList){
				oneFunction = (oneFunction.substring(0, oneFunction.indexOf("()"))).trim();
				if(!allFuncInJs.contains(oneFunction)){
					foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.HTML_TO_JAVASCRIPT, "Could not find " +
							oneFunction+" event listner in JavaScript"
							, this.htmlFilepath,null));
				}
			}
		}
	}
}
