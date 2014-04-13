package edu.asu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;

public class Util {
	public static  boolean isBlankString(String str){
		if(str == null || str.trim().equals("")){
			return true;
		}
		return false;
	}
	
	public static  boolean compareString(String str1, String str2){
		if(str1 != null && str2 != null && (str1.trim()).equalsIgnoreCase(str2.trim())){
			return true;
		}
		return false;
	}
	
	public static boolean isBlankList(List list){
		if(list != null && list.size()>0){
			return false;
		}
		return true;
	}
	public static ArrayList<String> getDOMSelectors(){
		ArrayList<String> retList = new ArrayList<String>();
		retList.add(Constants.GET_ELEMENT_BY_ID);
		retList.add(Constants.GET_ELEMENTS_BY_CLASS_NAME);
		//retList.add(Constants.GET_ELEMENTS_BY_NAME);
		//retList.add(Constants.GET_ELEMENTS_BY_TAG_NAME); 
		return retList;
	}
	
	
	public static boolean isInteger(String str) {
	    try { 
	        Integer.parseInt(str); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } 
	    return true;
	}
	
	public static String getWorkspaceRelativePath(String fileAbsoultePath){
		if(!Util.isBlankString(fileAbsoultePath)){
			if(fileAbsoultePath.startsWith("http")){
				return fileAbsoultePath;
			}
			if(!Util.isBlankString(fileAbsoultePath)){
				String sanitizedPath = fileAbsoultePath.trim();
				String workSpaceLocation = ResourcesPlugin.getWorkspace().getRoot()
						.getRawLocation().toString();
				// remove "/" in the incoming filename
				if (sanitizedPath.charAt(0) == '/' && sanitizedPath.length() > 1) {
					sanitizedPath = sanitizedPath.substring(1);
				} else if (sanitizedPath.charAt(0) == '/') {
					// There is nothing to go to, there is no fileName
					return null;
				}
				// subtract path with workSpaceLocation
			
				if (!sanitizedPath.startsWith(workSpaceLocation)
						&& workSpaceLocation.length() > sanitizedPath.length()) {
					return null;
				}
				return sanitizedPath.substring(workSpaceLocation.length(), sanitizedPath.length());
			}
		}
		return "";
	}
	
	
}
