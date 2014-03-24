package edu.asu.css;

import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CSSSelector;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.ICSSSelectorMember;
import com.phloc.css.reader.CSSReader;

public class CssParser {
	public static CascadingStyleSheet readCSS30 (final String cssFIleContent)
	  {
	    final CascadingStyleSheet aCSS = CSSReader.readFromString(cssFIleContent, "utf-8", ECSSVersion.CSS30);
	    if (aCSS == null)
	    {
	      // Most probably a syntax error 
	      return null;
	    }
	    for(CSSStyleRule one:aCSS.getAllStyleRules()){
	    	for(CSSSelector oneSelector :one.getAllSelectors()){
	    		oneSelector.toString();
	    		for(ICSSSelectorMember oneMember: oneSelector.getAllMembers()){
	    			oneMember.toString();
	    		}
	    	}
	    }
	    return aCSS;
	  }
}
