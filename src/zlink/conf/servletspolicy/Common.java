package zlink.conf.servletspolicy;

import org.json.JSONObject;

import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import zlink.business.user.io.db.SessionDB;
import zlink.tools.db.DBException;

public class Common {
	
	public final static String _userID="uid";

	/**
	 * Check if a session exists for a given sessionKey in params
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean sessionExists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JR.wrap(
						SessionDB._sessionKey
						,params.getString(SessionDB._sessionKey)
						)
				,SessionDB.collection);
	}

	/**
	 * Check if a device exists in session
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean deviceExists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JR.wrap(
						SessionDB._deviceID
						,params.getString(SessionDB._deviceID)
						)
				,SessionDB.collection);
	}


	/**
	 * Returns the userID associated to
	 * the given sessionKey in params 
	 * and wrap it in the 'same' JSONObject containing others/previous params  
	 * @param params
	 * @return 
	 * @throws DBException 
	 * @throws  */
	public static JSONObject decrypt(
			JSONObject params,
			String decryptedKeyName
			) throws DBException{
		return JR.replace(params,SessionDB._sessionKey,decryptedKeyName,
				THINGS.getOne(
						JR.wrap(
								SessionDB._sessionKey
								,params.getString(SessionDB._sessionKey)
								)
						,SessionDB.collection
						).get(SessionDB._userID));
	}

}
