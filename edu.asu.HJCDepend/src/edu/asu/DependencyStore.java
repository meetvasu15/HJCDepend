package edu.asu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DependencyStore {
/*
 * The following consists of list of all dependency across all three files 
 * the prefix of variable name indicates the originator of dependency
 * Example: a variable name with prefix html* means, the list would contain dependent components in the html document 
 */
	/*all accessors strings 
	* eg <div id="iAmId" class="iAmClass">
	*This list will store iAmId and iAmClass
	*/
	private ArrayList<String> htmlallHtmlAccessors;
	
	//all the javascript functions reffered by HTML
	private  HashMap<String, Integer> htmlEventListnerFunc;
	
	//all the css reffered by html using class attribute
	private ArrayList<String> htmlAllCssDependencies;
	
	// all the external javascript links reffered by HTML 
	private ArrayList<String> htmlAllJsExtLinks;
	
	//all the external css links reffered by HTML
	private ArrayList<String> htmlAllCssExtLinks;
	
	//all the Javascript files found on the server or disk
	private ArrayList<String> allJsFilesFound;
	
	//all the CSS files found on the server or disk
	private ArrayList<String> allCssFilesFound;
	
	//all functions found in all Js files
	private HashMap<String, Integer> jsAllFuncInJs;
	
	//all html ids reffered by Javascript
	private ArrayList<String> jsAllHtmlIdRead;
	
	//all html ids accessed by Javascript
	private ArrayList<String> jsAllHtmlIdWritten;
	
	//all css class selectors found in the css files
	private ArrayList<String> cssAllCssSelectors;
	//all css classes possibily written by JS
	private ArrayList<String> jsAllCssRef;

	
	/*
	 * initializing all lists
	 * we need every variable initialized to generate report
	 */
	public DependencyStore(){
		htmlallHtmlAccessors = new ArrayList<String>();
		htmlEventListnerFunc = new HashMap<String, Integer>();
		htmlAllCssDependencies = new ArrayList<String>();
		htmlAllJsExtLinks = new ArrayList<String>();
		htmlAllCssExtLinks = new ArrayList<String>();
		allJsFilesFound = new ArrayList<String>();
		allCssFilesFound = new ArrayList<String>();
		setJsAllFuncInJs(new HashMap<String, Integer>());
		jsAllHtmlIdRead = new ArrayList<String>();
		jsAllHtmlIdWritten = new ArrayList<String>();
		cssAllCssSelectors= new ArrayList<String>();
		jsAllCssRef = new ArrayList<String>();
		
	}
	
	public HashMap<String, Integer> getHtmlEventListnerFunc() {
		return htmlEventListnerFunc;
	}

	public void setHtmlEventListnerFunc(HashMap<String, Integer> htmlEventListnerFunc) {
		this.htmlEventListnerFunc = htmlEventListnerFunc;
	}

	public ArrayList<String> getHtmlAllCssDependencies() {
		return htmlAllCssDependencies;
	}

	public void setHtmlAllCssDependencies(ArrayList<String> htmlAllCssDependencies) {
		this.htmlAllCssDependencies = htmlAllCssDependencies;
	}

	public ArrayList<String> getHtmlAllJsExtLinks() {
		return htmlAllJsExtLinks;
	}

	public void setHtmlAllJsExtLinks(ArrayList<String> htmlAllJsExtLinks) {
		this.htmlAllJsExtLinks = htmlAllJsExtLinks;
	}

	public ArrayList<String> getHtmlAllCssExtLinks() {
		return htmlAllCssExtLinks;
	}

	public void setHtmlAllCssExtLinks(ArrayList<String> htmlAllCssExtLinks) {
		this.htmlAllCssExtLinks = htmlAllCssExtLinks;
	}

	public ArrayList<String> getAllJsFilesFound() {
		return allJsFilesFound;
	}

	public void setAllJsFilesFound(ArrayList<String> allJsFilesFound) {
		this.allJsFilesFound = allJsFilesFound;
	}

	public ArrayList<String> getAllCssFilesFound() {
		return allCssFilesFound;
	}

	public void setAllCssFilesFound(ArrayList<String> allCssFilesFound) {
		this.allCssFilesFound = allCssFilesFound;
	}


	public ArrayList<String> getJsAllHtmlIdRead() {
		return jsAllHtmlIdRead;
	}

	public void setJsAllHtmlIdRead(ArrayList<String> jsAllHtmlIdRead) {
		this.jsAllHtmlIdRead = jsAllHtmlIdRead;
	}

	public ArrayList<String> getJsAllHtmlIdWritten() {
		return jsAllHtmlIdWritten;
	}

	public void setJsAllHtmlIdWritten(ArrayList<String> jsAllHtmlIdWritten) {
		this.jsAllHtmlIdWritten = jsAllHtmlIdWritten;
	}

	public ArrayList<String> getHtmlallHtmlAccessors() {
		return htmlallHtmlAccessors;
	}

	public void setHtmlallHtmlAccessors(ArrayList<String> htmlallHtmlAccessors) {
		this.htmlallHtmlAccessors = htmlallHtmlAccessors;
	}

	public HashMap<String, Integer> getJsAllFuncInJs() {
		return jsAllFuncInJs;
	}

	public void setJsAllFuncInJs(HashMap<String, Integer> jsAllFuncInJs) {
		this.jsAllFuncInJs = jsAllFuncInJs;
	}

	public ArrayList<String> getCssAllCssSelectors() {
		return cssAllCssSelectors;
	}

	public void setCssAllCssSelectors(ArrayList<String> cssAllCssSelectors) {
		this.cssAllCssSelectors = cssAllCssSelectors;
	}

	public ArrayList<String> getJsAllCssRef() {
		return jsAllCssRef;
	}

	public void setJsAllCssRef(ArrayList<String> jsAllCssRef) {
		this.jsAllCssRef = jsAllCssRef;
	}
	
}
