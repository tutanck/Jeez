package com.aj.jeez.tools;

import org.json.JSONObject;

import java.util.*;

/**
 * Mr : MapRefiner
 * @author ANAGBLA Joan */
//TODO generiser ? iof string
//TODO relire attentivement tt repose su ca
public class MapRefiner {

	/**
	 * Reshape the input map from Map<String,String[]> to Map<String,String>
	 * by replacing the table of string map's value by his first element 
	 * @param parametersMap
	 * @return */
	public static Map<String,String> refine(
			Map<String,String[]> parametersMap
			) {
		Map<String,String> simpleKeyValMap = new HashMap<String, String>();
		for(Map.Entry<String,String[]> kv : parametersMap.entrySet())
			simpleKeyValMap.put(kv.getKey(), kv.getValue()[0]);
		return simpleKeyValMap;
	}

	
	
	/**
	 * Return an JSONObject equivalent of the {map}
	 * @param map
	 * @return */
	public static JSONObject jsonify(
			Map<?,?> map
	){
		return new JSONObject(map);
	}
	
	
	public Entry<String,String> getEntry(
			Map<String,String> map,
			String key
			){
		return new Entry<String,String>(key,map.get(key));
	}
	
	
	public class Entry<K,V>{
		
		private K key;
		private V val;
		
		public Entry(K key,V val) {
			this.key=key;
			this.val=val;
		}
		
		public K getKey(){return key;}
		public V getValue(){return val;}
	}



	/**
	 * @description return a submap according to the subset subKeysTab
	 * The submap is a copy and original map does not undergo changes
	 * @note we use List instead of Set just to facilitate Arrays conversions
	 * (with Arrays.asList)
	 * @param bigMap
	 * @param subKeysTab
	 * @return */
	public static Map<String,String> subMap(
			Map<String,String> bigMap,
			String...subKeysTab
	){
		Map<String,String> smallMap = new HashMap<>();
		List<String>subKeys = Arrays.asList(subKeysTab);
		for(String key : subKeys)
			if(bigMap.containsKey(key))
				smallMap.put(key, bigMap.get(key));
		return smallMap;
	}

	/**
	 * @description return a sub json according to the subset subKeysTab
	 * The sub json is a copy and original map does not undergo changes
	 * @note we use List instead of Set just to facilitate Arrays conversions
	 * (with Arrays.asList)
	 * @param bigMap
	 * @param subKeysTab
	 * @return */
	public static JSONObject subJSON(
			Map<String,String> bigMap,
			String...subKeysTab
	){
		return jsonify(subMap(bigMap,subKeysTab));
	}

	/**
	 * @Description subdivide a map in two maps following subKeyTab keys
	 * One branch will contains all entries whose key is in subKeyTab
	 * The other branch will contains all entries in initial bigMap except whose key is in subKeyTab
	 * @param bigMap
	 * @param subKeysTab
	 * @return  */
	public static List<Map<String,String>> branch(
			Map<String,String> bigMap,
			String...subKeysTab
	){
		Map<String,String> smallMap = new HashMap<>();
		Map<String,String> mediumMap = new HashMap<>(bigMap);
		List<String>subKeys = Arrays.asList(subKeysTab);
		for(String key : subKeys)
			if(bigMap.containsKey(key)){
				smallMap.put(key, bigMap.get(key));
				mediumMap.remove(key);
			}
		List<Map<String,String>>node = new ArrayList<>();
		node.add(0,mediumMap);
		node.add(1,smallMap);
		return node;
	}


	/**
	 * @Description rename some map's keys by replacing them by an alias without
	 * changing the associated values
	 * One branch will contains all entries whose key is in subKeyTab
	 * The other branch will contains all entries in initial bigMap except whose key is in subKeyTab
	 * @param bigMap
	 * @param keyMap
	 * @return  */
	public static Map<String,String> renameMapKeys(
			Map<String,String> bigMap,
			Map<String,String> keyMap
	){
		Map<String,String> aliasMap = new HashMap<>();
		for(String key : bigMap.keySet())
			if(keyMap.containsKey(key))
				aliasMap.put(keyMap.get(key), bigMap.get(key));
			else
				aliasMap.put(key, bigMap.get(key));
		return aliasMap;
	}



	public static void main(String[] args) {
		Map<String, String[]> map=new HashMap<>();
		map.put("lol",new String[]{"oui","non"});
		map.put("lol2",new String[]{"oui2","non2"});
		map.put("lol3",new String[]{"oui3","non2","jsp3"});
		map.put("lo",new String[]{"ou3"});
		Map<String, String> refinedMap= refine(map);
		System.out.println("bigmap : "+refinedMap);
		//Map<String, String> subdMap=subMap(refinedMap, new String[]{"lo","lol3"});
		//System.out.println("submap: "+subdMap);
		//System.out.println("bigmap : "+refinedMap);
		//List<Map<String, String>> branchedMap= branch(refinedMap,new String[]{"lo","lol3"});
		//System.out.println("amap[n]="+branchedMap);
		Map<String, String> kmap=new HashMap<>();
		kmap.put("lol", "newlol");
		kmap.put("loll", "newloll");
		kmap.put("lol3", "newlol3");

		renameMapKeys(refinedMap,kmap);
		System.out.println("aliasMap : "+renameMapKeys(refinedMap,kmap));

		System.out.println("bigmap : "+refinedMap);
	}

}