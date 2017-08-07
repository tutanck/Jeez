package com.aj.jeez.gate.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.aj.jeez.gate.core.exceptions.JEEZError;
import com.aj.jeez.gate.core.exceptions.ParamTypingException;
import com.aj.jeez.gate.core.exceptions.ServletDriverNotFoundException;
import com.aj.jeez.gate.representation.annotations.Checkout;
import com.aj.jeez.gate.representation.templates.JEEZServletDriver;
import com.aj.jeez.gate.representation.templates.TemplateParam;
import com.aj.jeez.gate.representation.templates.TemplateParams;
import com.aj.jeez.jr.JR;
import com.aj.jeez.tools.MapRefiner;
import com.aj.jeez.tools.__;


/**
 * @author Anagbla Joan */
public abstract class JEEZServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	/**
	 * The driver that drive the servlet's behavior */
	private JEEZServletDriver driver = null; 


	public final void init() throws ServletException {
		super.init();

		__.outln("JEEZServlet/init:: "+getClass().getCanonicalName()+" : new instance requested...");

		Enumeration<String> servletInitParamsNames = getInitParameterNames();
		while(servletInitParamsNames.hasMoreElements()){ 
			String paramName = servletInitParamsNames.nextElement();

			if(ServicesManager.JZID.equals(paramName)){
				driver = ServicesManager.server_router.get(getInitParameter(paramName)); 
				break;
			}
		}

		if(driver==null)
			throw new ServletDriverNotFoundException("This servlet template is not related to a WebService annotation and may not be used as an entry point");

		__.outln("JEEZServlet/init::THIS : '"+toString()+"'");
		__.outln("JEEZServlet/init::"+driver.getSC()+"."+driver.getSM()+" initialised and now waiting for call...");
	}



	/**
	 * The default method that check the connectivity of the related service
	 * default implementation returns always false
	 * @param request 
	 * @throws Exception */
	protected boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			) throws Exception{	return false; }



	/**
	 * -Set the response's content type to 'text/plain'
	 * -Performs some inspection on incoming parameters 
	 * and make sure they fit with the underlying service requirements/preconditions.
	 * -Send an HTTP error in case of some service's constraint violation.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception */
	private final JSONObject beforeBusiness(
			HttpServletRequest request,
			HttpServletResponse response
			)throws Exception {

		__.outln("JEEZServlet/beforeBusiness::"+this.driver.getSC()+"."+this.driver.getSM()+" --> before business...");

		response.setContentType("text/plain");

		JSONObject validParams = new JSONObject();

		Map<String,String>effectiveRequestParams=MapRefiner.refine(request.getParameterMap());

		__.outln("JEEZServlet/beforeBusiness::effectiveRequestParams : "+effectiveRequestParams+" - formalRequestParams : "+driver.getRequestParams());

		for(TemplateParam expected : driver.getRequestParams().getExpecteds()) {
			JSONObject res = paramIsValid(effectiveRequestParams,expected,validParams,true);
			if (!res.getBoolean("valid")){
				__.outln(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			__.outln(" -> Valid");
			validParams = JR.merge(validParams,(JSONObject) res.get("validParams"));
		}

		for(TemplateParam optional : driver.getRequestParams().getOptionals()){
			JSONObject res = paramIsValid(effectiveRequestParams,optional,validParams,false);
			if (!res.getBoolean("valid")){
				__.outln(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			__.outln(" -> Valid");
			validParams = JR.merge(validParams,(JSONObject) res.get("validParams"));
		}

		__.outln("JEEZServlet/beforeBusiness::Finally ValidParams:"+validParams);

		if(driver.requireAuth() && !isAuth(request,validParams)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USER UNAUTHENTICATED");
			return null;
		}else if(!driver.requireAuth() && isAuth(request,validParams)){
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "USER ALREADY AUTHENTICATED");
			return null;
		}

		return validParams;
	}


	/**
	 * Check if an incoming parameter is filled (exists and is not empty in the request)
	 * and properly typed according to epnIn and opnIn definitions
	 * @param effectiveParams
	 * @param formalParam
	 * @param validParams
	 * @param strict
	 * @return */
	private final JSONObject paramIsValid(
			Map<String,String>effectiveParams,
			TemplateParam formalParam,
			JSONObject validParams,
			boolean strict
			){
		//Must be at the beginning to be unchanged
		JSONObject invalid = new JSONObject().put("valid", false).put("validParams", validParams); 
		JSONObject unchanged = new JSONObject().put("valid", true).put("validParams", validParams);
		//TODO passage par ref : replce by a map copy  

		String paramName = formalParam.getName();
		Class<?> paramType = formalParam.getType();
		Set<String> paramRules = formalParam.getRules();

		//Debug
		__.out("    --->paramName:"+paramName);
		__.out(" && paramType:"+paramType);
		__.out(" = "+effectiveParams.get(paramName)+" ");

		//availability test
		if( effectiveParams.containsKey(paramName) && 
				effectiveParams.get(paramName)!=null &&
				!(effectiveParams.get(paramName).trim().length()==0) && //<=> equal("")
				!effectiveParams.get(paramName).equals("null") )
			if(__.civilized(effectiveParams.get(paramName), paramRules)) //civilization test
				try {//typing test
					if( EffectiveParamTyper.valid(
							paramName, 
							formalParam.typeToInt(),
							effectiveParams.get(paramName), validParams) )	
						//parameter added to validParams

						return new JSONObject() //updated with the valid parameter in addition
								.put("valid", true)
								.put("validParams", validParams); 

				}catch (ParamTypingException e) {
					throw new JEEZError("#SNO : internal typing error");
				}
		return strict? invalid:unchanged; 
	}

	/**
	 * Check if the result in the server response is sufficient and well formed (
	 * 	contains all needed keys each of the right type (in the JSON result) 
	 *  to considerate that the result match the service's postconditions.
	 *  )  
	 * @param request
	 * @param response
	 * @param result
	 * @param debug
	 * @throws Exception */
	private final void afterBusiness(
			HttpServletRequest request, //just a precaution (useless for now)
			HttpServletResponse response,
			Object result
			)throws Exception {

		__.outln("JEEZServlet/afterBusiness::"+this.driver.getSC()+"."+this.driver.getSM()+" --> after business...");
		__.outln("JEEZServlet/afterBusiness:: result: "+result);

		if(!resultIsOK(result)) {
			__.outln("result failed to satisfy at least one clientsafe checkouts");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE TEMPORARILY UNAVAILABLE");
			return;
		}
		__.outln("result satisfied all clientsafe checkouts");
		response.getWriter().print(result);
	}	



	/** 
	 * Check if the result contains all epnOut's key 
	 * and the corresponding values are properly typed
	 * @param result
	 * @return 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException */
	protected boolean resultIsOK(
			Object result
			) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{

		for(Map.Entry<Class<?>,Set<Method>> entry: this.driver.getCheckouts().entrySet()){
			Class<?> checkClass=entry.getKey();	

			for(Method checkout : entry.getValue()){
				String chkName=checkClass+"."+checkout;
				__.outln("JEEZServlet/resultIsOK:: static call of : "+chkName+"("+result+")");
				Checkout chk = checkout.getAnnotation(Checkout.class);

				try{
					if(!(boolean)checkout.invoke(checkClass.newInstance(), new Object[]{result,this.driver.getJsonOutParams()})){
						__.outln("result failed to satisfy checkout '"+chkName+"'");
						return !chk.value();							
					}
				}catch (Throwable t) {t.printStackTrace();return !chk.value();}
			}
		}
		return true;
	}



	/**
	 * Default doBusiness : invoke the {{serviceMethodName}} from the {{serviceClassName}}
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception */
	private final Object doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {			
		Class<?> serviceClass=Class.forName(this.driver.getSC());	
		Method m = serviceClass.getMethod(this.driver.getSM(), new Class[]{JSONObject.class});
		__.outln("JEEZServlet/doBusiness:: static call of : "+this.driver.getSC()+"."+m.getName()+"("+params+")  (formal : "+this.driver.getSM()+"."+m+")");
		return m.invoke(serviceClass.newInstance(), new Object[]{params});
	}	 


	/**
	 * Default common operations for all doMethods as basis : 
	 * beginning by beforeBusiness operation,
	 * if all is right :
	 *  then executes the doBusiness operation
	 *  and finally executes tests through the afterBusiness operation
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException */
	protected final void doDefault(
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException  {
		try{
			JSONObject params = beforeBusiness(request,response);
			if(params!=null)
				afterBusiness(
						request,response,
						doBusiness(
								request,
								response,
								doBeforeBusiness(request,response,params)
								)
						);
		}catch (Throwable t){
			t.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AN INTERNAL SERVER ERROR OCCURRED");
		}
	}



	/**
	 * DEFAULT TEMPLATING */


	/**
	 * Specify if the underlying service 
	 * need the user to be authenticated or not. 
	 * Null as default value to explicitly 
	 * say that this information is not provided by 
	 * template and will be set to false by annotation */
	public Boolean requireAuth() {return null;}

	/**
	 * The set of incoming parameters required and optional
	 * known by the underlying service  */ 
	public TemplateParams requestParams() {return new TemplateParams();}

	/**
	 * The set of outgoing parameters required and optional
	 * known by the underlying service */
	public TemplateParams jsonOutParams() {return new TemplateParams();}

	/**
	 * The set of test classes where to find 
	 * the test methods to execute after business */
	public Set<Class<?>> checkClasses() {return new HashSet<Class<?>>();}


	/**
	 * Say what to do just before business (service call)
	 * You are in the servlet (service caller) 'fais toi plaiz' */
	public JSONObject doBeforeBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			){return params;}


	/**
	 * ADDITIONAL METHODS */

	/**
	 * return the JEEZServlet String image */
	@Override public String toString(){return driver==null? "Driver not found" : this.driver.toString();}

	/** 
	 * return the driver of the servlet  */
	public JEEZServletDriver getServletDriver() {return driver;}

}