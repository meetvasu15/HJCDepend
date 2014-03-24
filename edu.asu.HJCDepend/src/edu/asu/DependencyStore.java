package edu.asu;

import java.util.ArrayList;
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
	private ArrayList<String> htmlEventListnerFunc;
	
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
	private ArrayList<String> jsAllFuncInJs;
	
	//all html ids reffered by Javascript
	private ArrayList<String> jsAllHtmlIdRead;
	
	//all html ids accessed by Javascript
	private ArrayList<String> jsAllHtmlIdWritten;
	

	
	/*
	 * initializing all lists
	 * we need every variable initialized to generate report
	 */
	public DependencyStore(){
		htmlEventListnerFunc = new ArrayList<String>();
		htmlAllCssDependencies = new ArrayList<String>();
		htmlAllJsExtLinks = new ArrayList<String>();
		htmlAllCssExtLinks = new ArrayList<String>();
		allJsFilesFound = new ArrayList<String>();
		allCssFilesFound = new ArrayList<String>();
		jsAllFuncInJs = new ArrayList<String>();
		jsAllHtmlIdRead = new ArrayList<String>();
		jsAllHtmlIdWritten = new ArrayList<String>();
		
	}
	
	public ArrayList<String> getHtmlEventListnerFunc() {
		return htmlEventListnerFunc;
	}

	public void setHtmlEventListnerFunc(ArrayList<String> htmlEventListnerFunc) {
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

	public ArrayList<String> getJsAllFuncInJs() {
		return jsAllFuncInJs;
	}

	public void setJsAllFuncInJs(ArrayList<String> jsAllFuncInJs) {
		this.jsAllFuncInJs = jsAllFuncInJs;
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
	
}
