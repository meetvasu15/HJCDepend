package edu.asu.javascript.parser;

import java.util.Iterator;
import java.util.List;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableInitializer;

import edu.asu.Constants;
import edu.asu.DependencyStore;
import edu.asu.FileNameLineTrack;
import edu.asu.Util;
import edu.asu.hjcdepend.ResultStoreBean;
import edu.asu.javascript.JsDoc;

public class JavascriptParser {

	public JsDoc jsDocObj;
	private DependencyStore dependencyStore;
	private List<ResultStoreBean> foundIssuesList;
	private FileNameLineTrack fileNameLineTrack;

	public JavascriptParser(DependencyStore dependencyStore, List<ResultStoreBean> foundIssuesList) {
		this.jsDocObj = new JsDoc();
		this.dependencyStore = dependencyStore;
		this.foundIssuesList = foundIssuesList;
	}

	public void parser(String jsString, FileNameLineTrack fileNameLineTrack) throws EvaluatorException {
		this.fileNameLineTrack = fileNameLineTrack;
		NodeVisitor nodeVisitor = new NodeVisitor() {
			public boolean visit(AstNode node) {
				int type = node.getType();
				 //try handle situations like object.property
				if (type == Token.CALL) {
					isDocumentSelector(node);
				}
				if (type == Token.ASSIGN){
					isDomIdentifierAccessed((Assignment)node);
				} 
				 
				return true;
			}
		};
		AstRoot astRoot;
		try{
		 astRoot = new Parser().parse(jsString, "uri", 1);
		}catch(EvaluatorException ee){
			//unlikely to happen, we take care of parsing way before
			/*foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.HJC_ERROR, Constants.ERROR_DURING_PARSING_JS +ee.getMessage()
					,null,null));*/
			throw new EvaluatorException(ee.getMessage());
		}
		//System.out.println(jsString);
		//The following code may be useful in future for getting all filenames.
	 	List<AstNode> statList = astRoot.getStatements();
		for (Iterator<AstNode> iter = statList.iterator(); iter.hasNext();) {
			AstNode nextNode = iter.next();
		if(nextNode != null && nextNode instanceof FunctionNode){
			FunctionNode fNode = (FunctionNode) nextNode; 
				//System.out.println(fNode.getParamCount());
				dependencyStore.getJsAllFuncInJs().put( fNode.getName().trim(), (Integer)fNode.getParamCount());
			}
		} 
		
		astRoot.visit(nodeVisitor);
		//System.out.println("Js ---> DOM \n\nListing DOM IDs read by Javascript :-");
	/*	for(String elt: jsDocObj.getIdFetchedEltList()){
			
			System.out.println(elt);
		} 

		//System.out.println("\nListing DOM IDs written to by Javascript :-");
		for(String eltDomId: jsDocObj.getDomEltWrittenToMap().keySet()){
			//System.out.print("The DOM id "+eltDomId+" was written on line numbers ");
			for(Integer lineNum: jsDocObj.getDomEltWrittenToMap().get(eltDomId)){
				//System.out.print(lineNum+", ");
			}
			//System.out.println("");
		}
		//System.out.println("**** +++ ****");
*/
	}
	
	   /* This method checks whether it is a element object we already know of and is being tried to modified using a assignment
		* example var elt = document.getElementById("element");
		* elt.style.display = "none";
		* Here elt is modified therefore it is a dependency
		*/
	public boolean isDomIdentifierAccessed(Assignment assignExpr) {
		// System.out.println(assignExpr.getLineno());
		if (assignExpr.getLeft() != null
				&& assignExpr.getLeft() instanceof PropertyGet) {
			PropertyGet property = (PropertyGet) assignExpr.getLeft();

			String assignmentObject = getLeftMostPropertyName(property);

			// check and report if unavailable HTML reference IDS was written to!!
			if (assignmentObject != null
					&& jsDocObj.getUnavailableDomReferences() != null
					&& jsDocObj.getUnavailableDomReferences().containsKey(
							assignmentObject)) {

				foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.JAVASCRIPT_TO_HTML,
						"Access to an undefined DOM element '"
								+ assignmentObject+"', check initialization of this variable", 
										 this.fileNameLineTrack.calculateJsTrueFileName(assignExpr.getLineno())
											,  this.fileNameLineTrack.calculateJsTrueLineNum(assignExpr.getLineno())+""));// report it!!
			}

			// check if the identifier exists in our identifier lists of DOM
			// references
			if (assignmentObject != null
					&& jsDocObj.getVariableIdentifier().containsKey(
							assignmentObject)) {
				jsDocObj.setDomEltWrittenToMap(jsDocObj.getVariableIdentifier()
						.get(assignmentObject), property.getLineno());

				// System.out.println("The DOM id \""+jsDocObj.getVariableIdentifier().get(assignmentObject)+"\" was accessed on line number "+property.getLineno());
			}
		}
		return false;
	}
	
	
	public String getLeftMostPropertyName(PropertyGet prop){ 
		if(prop.getLeft() instanceof  PropertyGet){
			return getLeftMostPropertyName((PropertyGet)prop.getLeft());
			
		}else if(prop.getLeft() instanceof Name){
			return prop.getLeft().getString();
		}
		return null;
		
	}
	
	//This method creates a list of all the dom references using document.getElementById("element")
	//This method also checks whether it is a element object we already know of and is being tried to modified using a function call
	// example var elt = document.getElementById("element");
	//elt.appendChild(somethingElse);
	//Here elt is modified therefore it is a dependency 
	
	public boolean isDocumentSelector(AstNode node) {
		
		/*VariableInitializer elementDeclaration = (VariableInitializer) node.getParent();
		elementDeclaration.getTarget().getString();
		elementDeclaration.getScope();*/
		FunctionCall functionExp = (FunctionCall) node;

		// check it is of type PropertyGet
		if (functionExp.getTarget() != null && functionExp.getTarget() instanceof PropertyGet) {
			PropertyGet property = (PropertyGet) functionExp.getTarget();

			// check if the call is made on the document object
			if (property.getLeft() != null && property.getLeft() instanceof Name 
					&& Util.compareString(property.getLeft().getString(),Constants.DOCUMENT_OBJECT)) {
				
				
				// what function is called on the document object
				for (String attribute : Util.getDOMSelectors()) {
					
					if (Util.compareString(property.getRight().getString(), attribute)) {
						
						// Access all args and add to the list
						List allargs = functionExp.getArguments();
						for (Iterator iter = allargs.iterator(); iter.hasNext();) {
							Object currArg = iter.next();
							
							if (currArg instanceof StringLiteral) {
								StringLiteral strArg = (StringLiteral) currArg;
								jsDocObj.getIdFetchedEltList().add(strArg.getValue());
								
								
								//check whether there is a html identifier in dom or else report it as warning.
								if(Util.isBlankList(dependencyStore.getHtmlallHtmlAccessors()) || !dependencyStore.getHtmlallHtmlAccessors().contains( strArg.getValue())){
									foundIssuesList.add(new ResultStoreBean(Constants.WARNING, Constants.JAVASCRIPT_TO_HTML, "Javascript tried to fetch " +
											"unavailable HTML reference '"+  strArg.getValue()+"'", this.fileNameLineTrack.calculateJsTrueFileName(node.getLineno())
											,  this.fileNameLineTrack.calculateJsTrueLineNum(node.getLineno())+""));//report it!!
									if(getVarInitializer(functionExp.getParent()) != null){
										
										jsDocObj.getUnavailableDomReferences().put(getVarInitializer(functionExp.getParent()), strArg.getValue());
									}
									//check if it is trying to write to a non existing property
									 if(isAccessingProperty(node.getParent())){
										 foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.JAVASCRIPT_TO_HTML, "Access to undefined DOM element " +
										 		", HTML reference '" +
													  strArg.getValue()+"' unavailable", this.fileNameLineTrack.calculateJsTrueFileName(node.getLineno())
													,  this.fileNameLineTrack.calculateJsTrueLineNum(node.getLineno())+""));//report it!!
									 }
								}
								//put the var identifier and value in the map eg.
								//var answer = document.getElementById("element");
								//key for map is answer and value is element.
								else if(getVarInitializer(functionExp.getParent()) != null){
									//check if the HTML reference is present in HTML document
									
									jsDocObj.getVariableIdentifier().put(getVarInitializer(functionExp.getParent()), strArg.getValue());
								}
								//getVarInitializer(functionExp.getParent());
							}
							//TO do:
							// This is dangerous, you are saving the instances reference not the actual value being passed
							else if (currArg instanceof Name) {
								Name varArg = (Name) currArg;
								jsDocObj.getIdFetchedEltList().add(varArg.getString());
								//jsDocObj.getIdentifierMap().put(key, varArg.getString())
							}
						}
						return true;
					}

				}
			}
			//check for if a addclass operation is being done?
			//if yes add this class name to JSimplicit css
			else if(property.getLeft() != null && property.getLeft() instanceof FunctionCall ){
				checkAddClassFuncCall(property, functionExp);
			}
			//check whether it is a identifier we already know of and is being tried to accessed on its properties.
			else if(property.getLeft() != null && property.getLeft() instanceof Name){
				//check identifier in existing list
				if(jsDocObj.getVariableIdentifier().containsKey(property.getLeft().getString())){
					jsDocObj.setDomEltWrittenToMap(jsDocObj.getVariableIdentifier().get(property.getLeft().getString()), property.getLineno());
					//System.out.println("The DOM element referencing id \""+jsDocObj.getVariableIdentifier().get(property.getLeft().getString())+"\" in the html was accessed on line number "+property.getLineno());
				}
				//check if the identifier belongs to unavaible DOM references
				if(jsDocObj.getUnavailableDomReferences().containsKey(property.getLeft().getString())){
					foundIssuesList.add(new ResultStoreBean(Constants.ERROR, Constants.JAVASCRIPT_TO_HTML, "Access to an undefined DOM element '" +
							  property.getLeft().getString()+"', check initialization of this variable", this.fileNameLineTrack.calculateJsTrueFileName(node.getLineno())
							,  this.fileNameLineTrack.calculateJsTrueLineNum(node.getLineno())+""));//report it!!
				}
			}

		}
		return false;
	}
	
	private void checkAddClassFuncCall(PropertyGet property, FunctionCall functionExp) {
		
		FunctionCall funcCall = (FunctionCall) property.getLeft();
		if (funcCall.getTarget() != null
				&& funcCall.getTarget() instanceof PropertyGet) {
			
			PropertyGet propLeft = (PropertyGet) funcCall.getTarget();

			// check if the call is made on the document object
			if (propLeft.getLeft() != null
					&& propLeft.getLeft() instanceof Name
					&& Util.compareString(propLeft.getLeft().getString(),
							Constants.DOCUMENT_OBJECT)) {
				
				if (property.getRight() != null
						&& property.getRight() instanceof Name
						&& Util.compareString(property.getRight().getString(),
								Constants.FUNC_ADD_CLASS)) {
					
					List allargs = functionExp.getArguments();
					
					for (Iterator iter = allargs.iterator(); iter.hasNext();) {
						Object currArg = iter.next();

						if (currArg instanceof StringLiteral) {
							StringLiteral strArg = (StringLiteral) currArg;
							dependencyStore.getJsAllCssRef().add(strArg.getValue());
							//System.out.println(strArg.getValue());
						}

					}
				}
			}
		}

	}

	private boolean isAccessingProperty(AstNode node) {
		if(node instanceof  PropertyGet){
			PropertyGet prop = (PropertyGet)node;
			if(prop.getRight() != null){
				return true;
			}
			
		}
		return false;
	}

	// this method returns the reference string of a variable initializer node
	public String getVarInitializer(AstNode node){
		if(node != null && node instanceof VariableInitializer){
			return ((VariableInitializer)node).getTarget().getString();  
		}
		return null;
	}

}
