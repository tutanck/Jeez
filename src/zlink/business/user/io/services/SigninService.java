package zlink.business.user.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;

import zlink.business.user.io.db.SessionDB;
import zlink.business.user.io.db.UserDB;
import zlink.conf.servletspolicy.OfflinePost;
import zlink.tools.db.DBException;
import zlink.tools.services.Response;
import zlink.tools.services.ServiceCodes;
import zlink.tools.services.ShouldNeverOccurException;
import zlink.tools.services.ToolBox;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

/**
 * @author AJoan */
public class SigninService {
	
	/**
	 * Valid url for tests  : 
	 * http://localhost:8080/ZLink/signin?email=tests.zlink@gmail.com&pass=Zlink1234&did=fK5df4Io949
	 * */
	
	public final static String url="/signin";
	
	/*Out*/
	public final static String _sessionKey="skey";
	
	@WebService(
			value=url,policy=OfflinePost.class,
			requestParams=@Params({
				@Param(value=UserDB._email,rules={"(.+)@(.+)\\.(.+)"}),
				@Param(value=UserDB._pass,rules={"((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})"})}))
	public static JSONObject login(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException, JSONException {

		DBObject foundUser;
		JSONObject things = JR.slice(params,UserDB._email,UserDB._pass);

		if (!THINGS.exists(things,UserDB.collection))
			return Response.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		foundUser = THINGS.getOne(things,UserDB.collection);

		if(!THINGS.exists(
				JR.wrap("_id",foundUser.get("_id")).put(UserDB._verified, true)
				,UserDB.collection))
			return Response.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(JR.wrap(SessionDB._userID, foundUser.get("_id")),SessionDB.collection);

		String sessionKey = ToolBox.generateAlphaNumToken(32);

		THINGS.add(
				JR.wrap(SessionDB._sessionKey,
						ToolBox.scramble(sessionKey+params.getString(SessionDB._deviceID)))
				.put(SessionDB._userID,foundUser.get("_id"))
				.put(SessionDB._deviceID,params.getString(SessionDB._deviceID))
				,SessionDB.collection);

		return Response.reply(JR.wrap(_sessionKey, sessionKey));
	}

}
