package com.aj.jeez.regina;

import java.util.Date;

import com.aj.jeez.tools.Caller;
import com.mongodb.*;

import zlink.tools.db.DBException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author AJoan
 *
 * Using THINGS allows :
 * -More logical not concerning database in services
 * -Conversion of incoming JSON into BSON (binary format)
 * before insertion into the database : https://www.mongodb.com/json-and-bson */
public class THINGS{


	/**
	 * Insert {things} in the {collection}
	 * @param things
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static DBCommit add(
			JSONObject things,
			DBCollection collection
			) throws DBException{
		WriteResult wr = null;
		DBAction action = DBAction.ADD;

		BasicDBObject doc = wrap(things).append("_date",new Date());

		try{ wr = collection.insert(doc); }
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),action);

		return new DBCommit(collection,doc,action, wr);
	}



	/** TODO update date
	 * Update {things} in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static DBCommit update(
			JSONObject where,
			JSONObject things,
			boolean upsert,
			boolean multi,
			DBCollection collection
			) throws DBException{
		WriteResult wr = null;
		DBAction action = updateAction(upsert,multi);

		BasicDBObject whr = wrap(where);
		BasicDBObject thngs = wrap(things);

		try{ wr = collection.update(whr,thngs,upsert,multi); }
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),action);

		return new DBCommit(collection,whr,thngs,action, wr);
	}



	/**
	 * Update {things} in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static DBCommit update(
			JSONObject where,
			JSONObject things,
			boolean upsert,
			DBCollection collection
			) throws DBException{
		return update(where,things,upsert,false,collection);
	}
	
	
	
	/**
	 * Update {things} in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static DBCommit update(
			JSONObject where,
			JSONObject things,
			DBCollection collection
			) throws DBException{
		return update(where,things,false,false,collection);
	}



	/**
	 * Match if {things} exists in database(
	 * NB: A thing is literally an entry in the map (<K,V>)
	 * @param things
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static boolean exists(
			JSONObject things,
			DBCollection collection
			) throws DBException{
		boolean response = false; 

		try{ response = collection.find(wrap(things)).limit(1).hasNext(); }		//limit 1 is for optimization : https://blog.serverdensity.com/checking-if-a-document-exists-mongodb-slow-findone-vs-find/
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.EXISTS);

		return response;
	}


	/**
	 * Returns things in the map from the table using SQL select requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return 
	 * @throws DBException */
	public static DBObject getOne(
			JSONObject things,
			DBCollection collection
			) throws DBException{
		DBObject dbo = null;

		try{ dbo=collection.findOne(wrap(things)); }
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.GETONE);

		return dbo;
	}



	/**
	 * Returns things in the map from the table using SQL select requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return 
	 * @throws DBException */
	public static DBCursor get(
			JSONObject things,
			DBCollection collection
			) throws DBException{
		DBCursor dbc = null;

		try{ dbc = collection.find(wrap(things)); }
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.GETALL);

		return dbc;
	}



	/**
	 * Remove things from the table using SQL delete requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return
	 * @throws DBException */
	public static DBCommit remove(
			JSONObject things,
			DBCollection collection
			) throws DBException{
		WriteResult wr =null;
		DBAction action = DBAction.REMOVE;

		BasicDBObject doc = wrap(things);

		try{ wr = collection.remove(doc); }
		catch(Exception e){DBException.forward(e);}

		logDBAction(things,collection,Caller.whoIsAsking(),action);

		return new DBCommit(collection,doc,action,wr);
	}


	/**
	 * Reformat a JSONObject into a BasicDBObject
	 * @param json
	 * @return */
	public static BasicDBObject wrap(
			JSONObject json
			){
		return new BasicDBObject(json.toMap());
	}

	/**
	 * Reformat a key-value into a BasicDBObject
	 * @param json
	 * @return */
	public static BasicDBObject wrap(
			String key,
			Object val
			){
		return new BasicDBObject(key,val);
	}

	
	/** 
	 * Return a JSONArray equivalent to the {dbList}
	 * @param map
	 * @return */
	public static JSONArray undressArray(
			BasicDBList dbList
			){
		return dbList.size() !=0 ? new JSONArray(dbList) : new JSONArray();
	}
	
	

	/**
	 * 
	 * @param upsert
	 * @param multi
	 * @return */
	private static DBAction updateAction(
			boolean upsert,
			boolean multi
			){
		return upsert?
				(multi?DBAction.UPSERTALL:DBAction.UPSERTONE)
				:(multi?DBAction.UPDATEALL:DBAction.UPDATEONE);
	}


	/**
	 * Print a database related action that happened 
	 * @param things
	 * @param collection
	 * @param caller
	 * @param action */
	private static void logDBAction(
			JSONObject things,
			DBCollection collection,
			String caller,
			DBAction action
			) {
		switch (action) {
		case ADD:
			System.out.println("This Things : '"+things+"' have been added to coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case UPDATEONE:
			System.out.println("This Things : '"+things+"' have been updated once in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case UPDATEALL:
			System.out.println("This Things : '"+things+"' have been updated everywhere in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case UPSERTONE:
			System.out.println("This Things : '"+things+"' have been upserted once in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case UPSERTALL:
			System.out.println("This Things : '"+things+"' have been upserted everywhere in coll'"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case REMOVE:
			System.out.println("This Things : '"+things+"' have been removed everywhere in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
			break;

		case EXISTS:
			System.out.println(caller +" asked if this Things : '"+things+"' are currently present in coll '"+collection.getFullName()+"'");
			break;

		case GETONE:
			System.out.println(caller +" asked to retrieve this Things : '"+things+"' once from coll '"+collection.getFullName()+"'");
			break;

		default: System.out.println("I'v no idea wtf you're searching to log for!");
		}
	}
}