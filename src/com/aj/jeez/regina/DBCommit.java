package com.aj.jeez.regina;

import org.bson.types.ObjectId;

import com.aj.jeez.jr.JR;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import zlink.tools.db.DBException;

public class DBCommit {

	private DBCollection coll;
	private DBAction action;
	private BasicDBObject where;
	private BasicDBObject things;
	private WriteResult wr;

	public DBCommit(
			DBCollection coll,
			BasicDBObject where,
			BasicDBObject things,
			DBAction action, 
			WriteResult wr		
			){
		this(coll, things, action, wr);
		this.where=where;
	}

	public DBCommit(
			DBCollection coll,
			BasicDBObject things,
			DBAction action, 
			WriteResult wr		
			){
		this.wr=wr;
		this.action=action;
		this.things=things;
		this.coll=coll;
	}


	public WriteResult rollback() throws DBException{
		switch (action) {
		case ADD : return THINGS.remove(JR.wrap("_id",(ObjectId)things.get("_id")),coll).getWriteResult();
		default: throw new IllegalArgumentException();
		}
	}

	public DBAction getAction() {return action;} 
	public WriteResult getWriteResult() {return wr;}
	public BasicDBObject getWhere() {return where;}
	public BasicDBObject getThings() {return things;}
	public DBCollection getCollection() {return coll;}
}
