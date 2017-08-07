package zlink.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.aj.jeez.tools.__;

import zlink.business.user.io.db.UserDB;
import zlink.conf.servletspolicy.OfflinePost;
import zlink.tools.db.DBException;
import zlink.tools.general.Email;
import zlink.tools.general.Messages;
import zlink.tools.services.Response;
import zlink.tools.services.ServiceCodes;
import zlink.tools.services.ShouldNeverOccurException;
import zlink.tools.services.ToolBox;


/**
 * @author AJoan */
public class AccountRecoveryService {
	
	/**
	 * Valid url for tests :
	 * http://localhost:8080/ZLink/account/recovery?email=tests.zlink@gmail.com&did=fK5df4Io949
	 * */
	
	public final static String url="/account/recovery";
	
	@WebService(value=url,policy=OfflinePost.class,
			requestParams=@Params({
				@Param(value=UserDB._email,rules={"(.+)@(.+)\\.(.+)"}) }))
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
	
		JSONObject things = JR.slice(params,UserDB._email);
		
		if(!THINGS.exists(things,UserDB.collection))
			return Response.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String himitsu = ToolBox.generateAlphaNumToken(64);
		THINGS.update(things,
				JR.wrap("$set",JR.wrap(UserDB._pass, himitsu))
				,UserDB.collection);
	
		try {
			Email.send(params.getString(UserDB._email),
					Messages.get("NewAccessKeySentSubject","fr-FR"),
					Messages.get("NewAccessKeySentMessage","fr-FR")+ himitsu);
		}catch (Exception e) {__.explode(e);}
		
		return Response.reply();
	}

}
