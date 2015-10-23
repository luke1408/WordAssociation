package at.shark.association;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class AssociationTree {
	private HashMap<String, AssociationPointer> pointers;
	private HashMap<String, Multimap<Integer, AssociationPointer>> cardinalityMap;
	private Multimap<Character, String> blockedWords;
	
	public AssociationPointer register(AssociationPointer p){
		String id1 = p.blob1.toLowerCase() + "|" + p.blob2.toLowerCase();
		String id2 = p.blob2.toLowerCase() + "|" + p.blob1.toLowerCase();
		
		//Check if there is some trace of an entry  for this
		//!Expect inconstancy!
		AssociationPointer p1 = pointers.get(id1);
		AssociationPointer p2 = pointers.get(id2);
		AssociationPointer pointer = null;
		if((p1 == null) != (p2==null)){
			pointer = p1 == null ? p2 : p1;
		}else{
			pointer = p1;
		}
		if(pointer == null){
			pointer = p;
		}
		
		pointers.put(id1, pointer);
		pointers.put(id2, pointer);
		pointer.raise();
		
		//Refresh CardinalityMap
		String[] names = new String[]{p.blob1, p.blob2};
		for(String name:names){
			Multimap<Integer, AssociationPointer> mm = cardinalityMap.get(name);
			if(mm == null){
				mm = ArrayListMultimap.create();
				cardinalityMap.put(name, mm);
			}
			mm.put(pointer.getCardinality(), pointer);
		}
		
		p = pointer;
		return p;
	}
	
	public void feed(String... words){
		Iterator<String> it = new Iterator<String>() {
			int i=0;
			@Override
			public boolean hasNext() {
				return words.length < i;
			}

			@Override
			public String next() {
				String word = null;
				if(hasNext()){
					word = words[i];
				}
				i++;
				while(hasNext() && blockedWords.containsEntry(words[i].toLowerCase().charAt(0), words[i].toLowerCase())){
					i++;
				}
				return word;
			}
		};
		if(words.length > 1){
			for(int i=1; i!=words.length; i++){
				AssociationPointer ap = new AssociationPointer(words[i-1], words[i]);
				register(ap);
			}
		}
	}
	
	public void getAssociations(String word, String[] associations){
		Multimap<Integer, AssociationPointer> mm = cardinalityMap.get(word);
		if(mm == null)
			return;
		Integer[] numbers = (Integer[]) mm.keys().toArray();
		Arrays.sort(numbers, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1-o2;
			}
		});
		int counter = 0;
		for(int i=0; i!=numbers.length; i++){
			for(AssociationPointer ap : mm.get(numbers[i])){
				if(counter >= associations.length){
					return;
				}
				String b = (ap.blob1.toLowerCase()+ap.blob2.toLowerCase()).replace(word, "");
				associations[counter] = b;
				counter++;
			}
		}
		
	}
	
	public void addBlockedWord(String s){
		String word = s.toLowerCase();
		if(!blockedWords.containsEntry(word.charAt(0), word)){
			blockedWords.put(word.charAt(0), word);
		}
	}
}
