package zlink.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.regina.THINGS;

import zlink.business.user.io.db.SessionDB;
import zlink.conf.servletspolicy.OnlinePost;
import zlink.tools.db.DBException;
import zlink.tools.services.Response;
import zlink.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class SignoutService {
	
	/**
	 * Valid url for tests  : NB : use for skey the one you got when you sign in
	 * http://localhost:8080/ZLink/signout?did=fK5df4Io949&skey=F8j1JzN0pfPdn0Nl723PThKOWYX29YHM
	 * */
	
	public final static String url="/signout";

	@WebService(value=url,policy=OnlinePost.class)
	public static JSONObject logout(
			JSONObject params
			) throws DBException, ShouldNeverOccurException {
		
		THINGS.remove(
				JR.wrap(SessionDB._deviceID,
						params.getString(SessionDB._deviceID))
				,SessionDB.collection);
	
		return Response.reply();
	}

}
