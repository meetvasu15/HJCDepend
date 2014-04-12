
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Normalize {

	/**
	 * @param args
	 */
	public Normalize(){
		allEntriesValues = new HashMap<String, ArrayList<Integer>>();
		allEntriesTotal= new HashMap<String, Integer>();
		sorted =  new HashMap<String, Integer>();
		
	}
	
	
	public HashMap<String, ArrayList<Integer>> allEntriesValues;
	public HashMap<String, Integer> allEntriesTotal;
	 Map<String, Integer> sorted;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Normalize n = new Normalize();
		n.readCsv("Survey.csv");
		
	}

	public int replaceText(String Text) {
		return 0;
	}

	public String readCsv(String path) {
		StringBuffer ret = new StringBuffer();
		String csvFile = path;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<Integer>toReadCols =  new ArrayList<Integer>();
		toReadCols.add(2);
		toReadCols.add(3);
		toReadCols.add(4);
		toReadCols.add(5);
		toReadCols.add(6);
		toReadCols.add(7);
		toReadCols.add(8);
		try {
			boolean line1 = true;
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				if(line1){
					line1 = false;
					continue;
				}
				ArrayList<Integer> newEntryArr = new ArrayList<Integer>();
				// use comma as separator
				String[] values = line.split(cvsSplitBy);
				if(values != null){
					int total=0;
					//System.out.print(values[1]+", ");
					for(int i = 0; i<values.length; i++){
						if(toReadCols.contains(i)){
							newEntryArr.add(convertKnowledge(values[i]));
							total += convertKnowledge(values[i]);
							//System.out.print(convertKnowledge(values[i])+", ");
						}
					}
					allEntriesValues.put(values[1], newEntryArr);
					//System.out.println(total);
					allEntriesTotal.put(values[1], total);
				}

			}
			
			//sort the map
			sorted = sortByValues(allEntriesTotal);
			for (Map.Entry<String, Integer> entry : sorted.entrySet())
			{
			    System.out.println(entry.getKey() + " / " + entry.getValue());
			}
			String [] sortedUID = new String[sorted.keySet().size()];
			int count = 0;
			for(String oneKey: sorted.keySet()){
				sortedUID[count] = oneKey;
				count+=1;
			}
			
			ArrayList<String> GroupA = new ArrayList<String>();
			ArrayList<String> GroupB = new ArrayList<String>();
			
			for (int j = 0; j < (sortedUID.length/2); j++)
			{
				if(j%2 == 0){
				GroupA.add(sortedUID[j]);
				GroupB.add(sortedUID[(sortedUID.length-1) - j]);
				}else{
					GroupB.add(sortedUID[j]);
					GroupA.add(sortedUID[(sortedUID.length-1) - j]);
				}
			    
			}
			if(sortedUID.length%2 != 0){
				//System.out.println(sortedUID.length/2+1);
				GroupA.add(sortedUID[sortedUID.length/2]);
			}
			ret.append("\nGroupA\n");
			for(String one: GroupA){
				ret.append(one+"\n");
			}
			ret.append("\nGroupB\n");
			for(String one: GroupB){
				ret.append(one+"\n");
			}
			
		
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(ret.toString());
		return ret.toString();
	}
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public int convertKnowledge(String currentValue) {
		
		if (currentValue != null && !currentValue.trim().equals("")) {
			if(isNumeric(currentValue)){
				return Integer.parseInt(currentValue);
			}
			currentValue = currentValue.trim();
			if (currentValue.equals("Never Used")) {
				return 0;
			} else if (currentValue.contains("Poor")) {
				return 1;
			} else if (currentValue.equals("None")) {
				return 0;
			} else if (currentValue.equals("Average")) {
				return 2;
			} else if (currentValue.equals("Good")) {
				return 3;
			} else if (currentValue.equals("Zen Master")) {
				return 5;
			} else if (currentValue.equals("Less than 1 Year")) {
				return 1;
			} else if (currentValue
					.equals("More than 1 but less than 3 Years")) {
				return 2;
			} else if (currentValue
					.equals("More than 3 but less than 5 Years")) {
				return 3;
			} else if (currentValue.equals("More than 5 years")) {
				return 4;
			} else if (currentValue.equals("2.0 or below")) {
				return 0;
			} else if (currentValue.equals("2.1 - 2.5")) {
				return 1;
			} else if (currentValue.equals("2.6 - 3.0")) {
				return 2;
			} else if (currentValue.equals("3.1 - 3.5")) {
				return 3;
			} else if (currentValue.equals("3.6 - 4.0")) {
				return 4;
			}
		}
		return 0;

	}
	
	
	
	
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }

	
	
	

}
