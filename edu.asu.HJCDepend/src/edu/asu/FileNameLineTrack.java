package edu.asu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileNameLineTrack {
	
	public FileNameLineTrack (){
		JsEndLineNumberMap = new LinkedHashMap<String, Integer>();
	}
	// This Map has name of file -----> Line Number 
	private LinkedHashMap<String, Integer> JsEndLineNumberMap;

	public LinkedHashMap<String, Integer> getJsEndLineNumberMap() {
		return JsEndLineNumberMap;
	}

	public Integer getLastLineNumberJsEndLineNumberMap() {
		Integer lastLineNumber = 0;
		Iterator<Integer> it = JsEndLineNumberMap.values().iterator();
		while(it.hasNext()){
			lastLineNumber = it.next();
		}
		return lastLineNumber;
	}

	
	public void setJsEndLineNumber(String fileName, Integer lineInteger) {
		if(lineInteger != null && !Util.isBlankString(fileName)){
			JsEndLineNumberMap.put(fileName, lineInteger) ;
		}
	}
	
	public Integer calculateJsTrueLineNum (Integer lineNumber){
		int retVal = 0;
		int prevVal = 0;
		for (Map.Entry<String, Integer> entry : JsEndLineNumberMap.entrySet())
		{
			if(entry.getValue() >= lineNumber){
				return lineNumber - prevVal;
			}
			prevVal = entry.getValue();
		   // System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		
	return retVal;
	
}

	public String calculateJsTrueFileName (Integer lineNumber){ 
		String prevName = "";
		for (Map.Entry<String, Integer> entry : JsEndLineNumberMap.entrySet())
		{
			if(entry.getValue() >= lineNumber){
				/*if(!Util.isBlankString(prevName)){
					return prevName;
				}else{*/
					return entry.getKey();
				//} 
			}
			prevName = entry.getKey();
		   // System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		
	return prevName;
	
}


}
