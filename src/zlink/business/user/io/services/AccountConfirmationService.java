package zlink.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import zlink.business.user.io.db.UserDB;
import zlink.conf.servletspolicy.OfflinePost;
import zlink.tools.db.DBException;
import zlink.tools.services.Response;
import zlink.tools.services.ServiceCodes;
import zlink.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class AccountConfirmationService {
	
	/**
	 * Valid url for tests  : NB : replace ckey parameter by the verification code you just received by email
	 * http://localhost:8080/ZLink/account/confirm?email=tests.zlink@gmail.com&ckey=4553&did=fK5df4Io949
	 * */
	
	public final static String url="/account/confirm";
	
	public final static String _confirmationKey="ckey";
	
	@WebService(value=url,policy=OfflinePost.class,
			requestParams=@Params({
				@Param(value=_confirmationKey,type=int.class,rules ={"([0-9]{4})"}),
				@Param(value=UserDB._email,rules={"(.+)@(.+)\\.(.+)"}) }))
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException, InvalidKeyException{ 

		JSONObject things = JR.renameKeys(
				JR.slice(params,_confirmationKey,UserDB._email),
				_confirmationKey+"->"+UserDB._verified);
		
		if(!THINGS.exists(things, UserDB.collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);	
		
		THINGS.update(things, 
				JR.wrap("$set",JR.wrap(UserDB._verified, true))
				,UserDB.collection);		

		return Response.reply();
	}

}
