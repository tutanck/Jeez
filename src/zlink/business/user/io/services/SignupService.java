package zlink.business.user.io.services;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.DBCommit;
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
public class SignupService{
	
	/**
	 * Valid url for tests  : 
	 * http://localhost:8080/ZLink/signup?email=tests.zlink@gmail.com&pass=Zlink1234&did=fK5df4Io949
	 * */
	
	public final static String url="/signup";	
	
	@WebService(
			value=url,policy = OfflinePost.class,
			requestParams=@Params({
				@Param(value=UserDB._email,rules={"(.+)@(.+)\\.(.+)"}),
				@Param(value=UserDB._pass,rules={"((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})"}) }))
	public static JSONObject registration(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException,JSONException {
		
		if(CheckEmailService.isEmailTaken(params).getBoolean(Response.result)==true)
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		int vkey = ToolBox.generateIntToken(4);
		
		DBCommit commit = THINGS.add(
				JR.slice(params,UserDB._pass,UserDB._email)
				.put(UserDB._verified, vkey)
				.put(UserDB._registrationDate, new Date())
				,UserDB.collection);

		try {
			Email.send(
					params.getString(UserDB._email),
					Messages.get("welcomeMailSubject","fr-FR"),
					Messages.get("welcomeMailMessage","fr-FR")+vkey);
		}catch (Exception e) {
			commit.rollback(); 
			__.explode(e);
		}

		return Response.reply();
	}

}
