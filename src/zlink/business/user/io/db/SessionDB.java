package zlink.business.user.io.db;

import com.mongodb.DBCollection;

import zlink.tools.db.DBManager;


/**
 * @author AJoan */
public class SessionDB {

	public static DBCollection collection = DBManager.collection("sessions");

	public final static String _userID="uid";
	public final static String _deviceID="did";
	public final static String _sessionKey="skey";

}