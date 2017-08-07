package zlink.conf;

import org.json.JSONObject;

import com.aj.jeez.gate.core.Startup;
import com.aj.jeez.gate.representation.annotations.WebService;

/**
 * @author Joan */
public class AppRouterService {
	public final static String url="/jz/app/routes"; 
	
	@WebService(url)
	public static JSONObject getRouter(
			JSONObject params
			)  {
		return Startup.getRouter();
	}
}
