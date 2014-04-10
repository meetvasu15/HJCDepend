package edu.asu.css;

import java.util.List;

import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CSSSelector;
import com.phloc.css.decl.CSSSelectorSimpleMember;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.ECSSSelectorCombinator;
import com.phloc.css.decl.ICSSSelectorMember;
import com.phloc.css.reader.CSSReader;

import edu.asu.DependencyStore;
import edu.asu.Util;

public class CssParser {
	public static CascadingStyleSheet readCSS30 (final String cssFIleContent, DependencyStore dependencyStore)
	  {
	    final CascadingStyleSheet aCSS = CSSReader.readFromString(cssFIleContent, "utf-8", ECSSVersion.CSS30);
	    if (aCSS == null)
	    {
	      // Most probably a syntax error 
	      return null;
	    } 
	    //VisitUrl visitAllUrl = new  VisitUrl();
	   //visitAllUrl.getAllImportStatementCSS(aCSS, dependencyStore);
	    
	    for(CSSStyleRule one:aCSS.getAllStyleRules()){
	    	
	    	for(CSSSelector oneSelector :one.getAllSelectors()){
	    		oneSelector.toString();
	    		for(ICSSSelectorMember oneMember: oneSelector.getAllMembers()){
	    			if(oneMember instanceof CSSSelectorSimpleMember){
	    			CSSSelectorSimpleMember mem = (CSSSelectorSimpleMember)oneMember;
		    			if(mem.isClass()){
		    				if(!Util.isBlankString(mem.getValue()) && mem.getValue().length()>=1){
		    					dependencyStore.getCssAllCssSelectors().add(mem.getValue().substring(1));
		    				}
		    			}
	    			
	    			}else if(oneMember instanceof ECSSSelectorCombinator){
	    				
	    			} 
	    		}
	    	}
	    }
	    return aCSS;
	  }
}
