package zlink.tools.db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DBManager {

	private static DB db=null;

	public static String mongo_host = "127.0.0.1"; //Mongo server address
	public static int mongo_port = 27017; //Mongo server port
	public static String mongo_db = "zlink"; //Mongo Database's name


	/**
	 * Mongo server connection
	 * @return 
	 * @throws UnknownHostException */
	public static DB mongoDB() {
		try { return (db!=null)? db : (db = new MongoClient(mongo_host,mongo_port).getDB(mongo_db)); }
		catch (UnknownHostException e) {e.printStackTrace();return null;}
	}

	/**
	 * Access to MongoDb collection 
	 * @param collectionName
	 * @return 
	 * @throws UnknownHostException */
	public static DBCollection collection(
			String collectionName
			){
		return mongoDB().getCollection(collectionName);
	}


	//tests
	public static void main(String[] args) {
		System.out.println("Trying to connect with MongoDB");
		mongoDB();
		System.out.println("Success...");
		String collName="comments";
		System.out.println("Trying to retrieve MongoDB Collection '"+collName+"'");
		collection(collName);
		System.out.println("Success...");
	}
}