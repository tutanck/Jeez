package zlink.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import zlink.business.user.io.db.UserDB;
import zlink.conf.servletspolicy.OfflineGet;
import zlink.tools.db.DBException;
import zlink.tools.services.Response;
import zlink.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class CheckEmailService {
	
	/**
	 * Valid url for tests  : 
	 * http://localhost:8080/ZLink/email/taken?email=tests.zlink@gmail.com&did=fK5df4Io949
	 * */
	
	public final static String url="/email/taken";

	@WebService(value=url,policy=OfflineGet.class,
			requestParams=@Params({ @Param(value=UserDB._email,rules={"(.+)@(.+)\\.(.+)"}) }))
	public static JSONObject isEmailTaken(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		return Response.reply(THINGS.exists(JR.slice(params,UserDB._email),UserDB.collection));
	}

}
