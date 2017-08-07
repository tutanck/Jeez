package zlink.conf.servletspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.aj.jeez.gate.core.exceptions.InconsistentParametersException;
import com.aj.jeez.gate.core.exceptions.ParamNamingException;
import com.aj.jeez.gate.core.exceptions.ParamRulingException;
import com.aj.jeez.gate.core.exceptions.ParamTypingException;
import com.aj.jeez.gate.defaults.policy.Post;
import com.aj.jeez.gate.representation.templates.TemplateParam;
import com.aj.jeez.gate.representation.templates.TemplateParams;
import com.aj.jeez.tools.__;

import zlink.business.user.io.db.SessionDB;
import zlink.tools.db.DBException;

/**
 * * @author Anagbla Joan */
public class OnlinePost extends Post{
	private static final long serialVersionUID = 1L;
		
	@Override
	public Boolean requireAuth(){return true;}

	@Override
	public TemplateParams requestParams(){
		try {
			TemplateParams tp =	new TemplateParams();
			tp.addExpected(new TemplateParam(SessionDB._sessionKey));
			return tp;
		} catch (ParamTypingException | ParamNamingException | ParamRulingException | InconsistentParametersException e) {
			__.explode(e);
			return null;
		}
	}

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return Common.sessionExists(params);
	}
		
	@Override
	public JSONObject doBeforeBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			){
		try {
			return Common.decrypt(params,Common._userID);
		} catch (DBException e) {__.explode(e);}
		return params;
	}
}