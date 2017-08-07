package com.aj.jeez.jr;

import org.json.JSONObject;

import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.mongodb.util.JSON;

import java.util.*;

/**JR : JSONRefiner
 * Revoir enti�rement apr�s avoir relu completement : 
 * http://static.javadoc.io/org.json/json/20160810/org/json/JSONObject.html#toMap--
 * http://stackoverflow.com/questions/12809779/how-do-i-clone-an-org-json-jsonobject-in-java
 * @author ANAGBLA Joan */
//TODO relire attentivement tt repose su ca
public class JR {	

	/** 
	 * Return an JSONObject equivalent of the {map}
	 * @param map
	 * @return */
	public static JSONObject jsonify(
			Map<?,?> map
			){
		return new JSONObject(JSON.serialize(map));
	}

	/** 
	 * Return an JSONObject equivalent of the {map}
	 * @param map
	 * @return */
	public static JSONObject jsonify(
			Object object
			){
		return new JSONObject(JSON.serialize(object));
	}


	/**
	 * Return an JSONObject similar to {json}
	 * @param json
	 * @return */
	public static JSONObject clone(
			JSONObject json
			){
		return new JSONObject(JSON.serialize(json));
	}




	/**
	 * Wrap a key-value into a JSONObject and return it
	 * @param key
	 * @param value
	 * @return */
	public static JSONObject wrap(String key, Object value){
		return new JSONObject().put(key, value);
	}


	/**
	 * Wrap a key-value string list into a JSONObject and return it
	 * @param keyValString
	 * @return
	 * @throws InvalidKeyException */
	public static JSONObject wrap(
			String...keyValString
			) throws InvalidKeyException{
		JSONObject jo = new JSONObject();
		for(String keyEntry : keyValString){
			if(!keyEntry.contains("->")) 
				throw new 
				InvalidKeyException("The key entry '"+keyEntry+"' does not contains the universal seprator '->'");
			String [] entry= keyEntry.split("->");
			jo.put(entry[0].trim(),entry[1].trim());
		}
		return jo;
	}

	
	/**
	 * Wrap a suite of JSONObject into a list JSONObject
	 * @param jsonSuite
	 * @return */
	public static List<JSONObject> list(
			JSONObject...jsonSuite 
			){
		List<JSONObject> list = new ArrayList<>();
		for(JSONObject json : jsonSuite)
			list.add(json);
		return list;
	}


	/**
	 * Return a sliced json according to the subset of {subKeys}
	 * The sliced json is a copy of the {whole} json that does not undergo any changes.
	 * @param whole
	 * @param subKeys
	 * @return 
	 * @throws AbsentKeyException */
	public static JSONObject slice(
			JSONObject whole,
			String...subKeys
			) throws AbsentKeyException{
		JSONObject sliced= new JSONObject();
		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			if(whole.has(key))
				sliced.put(key, whole.get(key));
			else throw new 
			AbsentKeyException("The key '"+key+"' does not exist in '"+whole+"'");
		return sliced;
	}


	/**
	 * Return a json that does not contain the subset of {subKeys}
	 * The returned json is a copy of the {whole} json that does not undergo any changes.
	 * @param whole
	 * @param subKeys
	 * @return */
	public static JSONObject clean(
			JSONObject whole,
			String...subKeys
			){
		JSONObject clean= new JSONObject(whole.toMap());
		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			clean.remove(key);
		return clean;
	}


	/**
	 * Remove the entry identified by {oldKey} in the {json} if it exists.
	 * Then put a new entry key-value : {newKey}-{newValue}
	 * @param json
	 * @param oldKey
	 * @param newKey
	 * @param newValue
	 * @return */
	public static JSONObject replace(
			JSONObject json,
			String oldKey,
			String newKey,
			Object newValue
			){
		JSONObject newjson= new JSONObject(json.toMap());
		newjson.remove(oldKey);
		return newjson.put(newKey, newValue);
	}


	/** 
	 * Subdivide a json's {trunc} in two json's branches following {subKeys} keys
	 * One branch will contains all entries whose key is in {subKeys}
	 * The other branch will contains all entries in initial {trunc} except whose key is in {subKeys}
	 * @param trunk
	 * @param subKeys
	 * @return  
	 * @throws AbsentKeyException */
	public static Node<JSONObject> branch(
			JSONObject trunk,
			String...subKeys
			) throws AbsentKeyException{
		JSONObject yellow = new JSONObject();
		JSONObject white = new JSONObject(trunk.toMap());

		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			if(trunk.has(key)){
				yellow.put(key, trunk.get(key));
				white.remove(key);
			}
			else throw new
			AbsentKeyException("The key '"+key+"' does not exist in '"+trunk+"'");

		return new Node<JSONObject>(yellow,white);
	}


	public static JSONObject evict(
			JSONObject params
			,String ...keysToRemove
			) {
		JSONObject clean = clone(params);
		for(String key : keysToRemove)
			clean.remove(key);
		return clean;
	}


	/**
	 * Merge to JSONObject together
	 * Be careful : values in {branch2} will override 
	 * values in {branch1} for the keys present in the two branches
	 * @param branch1
	 * @param branch2
	 * @return */
	public static JSONObject merge(
			JSONObject branch1,
			JSONObject branch2
			){
		JSONObject trunk = new JSONObject(branch1.toMap());

		for(String key : branch2.keySet())
			trunk.put(key, branch2.get(key));

		return trunk;
	}



	/**
	 * Rename {json}'s keys by replacing them 
	 * by the associated value in the {keyMap} without
	 * changing the associated values in the {json}.
	 * No change is performed on the keys that are not in {keyMap}.
	 * @param json
	 * @param keyMap
	 * @return  
	 * @throws AbsentKeyException */
	public static JSONObject renameKeys(
			JSONObject json,
			Map<String,String> keyMap
			) throws AbsentKeyException{
		JSONObject aliasJSON = new JSONObject(json.toMap());
		for(String key : keyMap.keySet())
			if(json.has(key)){
				aliasJSON.remove(key);
				aliasJSON.put(keyMap.get(key), json.get(key));
			}
			else throw new
			AbsentKeyException("The key '"+key+"' does not exist in '"+json+"'");
		return aliasJSON;
	}


	/**
	 * Rename {json}'s keys by replacing them 
	 * by the associated value in the {keyMapString} without
	 * changing the associated values in the {json}.
	 * No change is performed on the keys that are not in {keyMapString}.
	 * @param json
	 * @param keyMapString
	 * @return
	 * @throws AbsentKeyException
	 * @throws InvalidKeyException */
	public static JSONObject renameKeys(
			JSONObject json,
			String...keyMapString
			) throws AbsentKeyException, InvalidKeyException{
		JSONObject aliasJSON = new JSONObject(json.toMap());

		for(String keyEntry : keyMapString){
			if(!keyEntry.contains("->")) 
				throw new 
				InvalidKeyException("The key entry '"+keyEntry+"' does not contains the universal seprator '->'");

			String [] entry= keyEntry.split("->");
			String oldKey = entry[0].trim();
			String newKey = entry[1].trim();

			if(json.has(oldKey)){
				aliasJSON.remove(oldKey);
				aliasJSON.put(newKey, json.get(oldKey));
			}
			else throw new
			AbsentKeyException("The key '"+keyEntry+"' does not exist in '"+json+"'");
		}
		return aliasJSON;
	}



	public static void main(String... args) throws AbsentKeyException {
		JSONObject jo = new JSONObject()
				.put("lola","lola")
				.put("lol0","oui")
				.put("lol1",12.7)
				.put("lol2",true)
				.put("lol3",12);

		System.out.println("sliced : "+slice(jo,"lol1","lol3"));
		System.out.println("jo : "+jo+"\n");

		System.out.println("node : "+branch(jo,"lol1","lol2"));
		System.out.println("jo : "+jo+"\n");	

		Map<String, String> kmap=new HashMap<>();
		kmap.put("lol1", "newlol1");
		kmap.put("lol3", "newlol3");
		//kmap.put("lol", "newlol"); //lol don't exist in jo --> except
		System.out.println("aliasMap : "+renameKeys(jo,kmap));
		System.out.println("jo : "+jo+"\n");

		System.out.println("clean : "+clean(jo,"lola"));
		System.out.println("jo : "+jo+"\n");
	}
}