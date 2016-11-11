package ba.sema.pdf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;


public class Helper 
{
	
	public static List<String> naziviFajlovaSortiraniPoVeliciniDesc(String putanja) {
		String putanjaOriginalnihFajlova = putanja;
		
		File[] originalniFajlovi = new File(putanjaOriginalnihFajlova).listFiles();
				
		HashMap<String, Long> hashmapNameSize = new HashMap<String, Long>();
		for (File originalniFajl : originalniFajlovi) {
		    if (originalniFajl.isFile()) {
		        hashmapNameSize.put(originalniFajl.getName(), (originalniFajl.length() / 1024));
		    }
		}
        
		HashMap<String, Long> hashmapNameSizeSortDesc = sortByValuesDesc(hashmapNameSize);
        List<String> naziviSortDesc = new ArrayList<String>();
         
        Set<Entry<String, Long>> set2 = hashmapNameSizeSortDesc.entrySet();
        Iterator<Entry<String, Long>> iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
             Entry<String, Long> me2 = (Entry<String, Long>)iterator2.next();             
             naziviSortDesc.add(me2.getKey().toString());
        }
        
        return naziviSortDesc;
	}
	
	private static HashMap<String, Long> sortByValuesDesc(HashMap<String, Long> map) { 
        List<Entry<String, Long>> list = new LinkedList<Entry<String, Long>>(map.entrySet());
        //
        Collections.sort(list, new Comparator<Entry<String, Long>>() {
        	@Override
            public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
               return o2.getValue().compareTo(o1.getValue());
            }
        });
        //
        HashMap<String, Long> sortedHashMap = new LinkedHashMap<String, Long>();
        for (Iterator<Entry<String, Long>> it = list.iterator(); it.hasNext();) {
              Entry<String, Long> entry = (Entry<String, Long>)it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
    }
	
}
