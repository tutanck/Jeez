package com.aj.jeez.gate.representation.templates;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aj.jeez.gate.core.JEEZServlet;

public final class JEEZServletDriver {

	/**
	 * The url pattern of the servlet */
	private final String url;

	/**
	 * Servlet policy */
	private final Class<? extends JEEZServlet> policy;

	/**
	 * HTTP method code */
	private final int HTTPMethodCode;

	/**
	 * The qualified name of the class where to find 
	 * the serice's method to call */
	private final String sC; 

	/**
	 * The qualified name of the serice's method 
	 * to be called */
	private final String sM;

	/**
	 * The servlet checkouts by checkClass */
	private final Map<Class<?>,Set<Method>> checkouts;

	/**
	 * Specify if the underlying service 
	 * need the user to be authenticated or not */
	private final Boolean requireAuth;

	/**
	 * The set of incoming parameters required and optional
	 * known by the underlying service  */
	private final TemplateParams requestParams; 

	/**
	 * The set of outgoing parameters required and optional
	 * known by the underlying service */
	private final TemplateParams jsonOutParams;



	public JEEZServletDriver(
			String url,
			String sC,
			String sM,
			int HTTPMethodCode, 
			boolean requireAuth,
			Class<? extends JEEZServlet> policy,
			TemplateParams requestParams,
			TemplateParams jsonOutParams,
			Map<Class<?>, Set<Method>> checkouts
			) {
		super();
		this.url = url;
		this.sC = sC;
		this.sM = sM;
		this.HTTPMethodCode = HTTPMethodCode;
		this.requireAuth=requireAuth;
		this.policy = policy;
		this.requestParams=requestParams;
		this.jsonOutParams=jsonOutParams;
		this.checkouts = checkouts;
	}

	
	@Override
	public String toString(){
		String jz ="";
		jz+="service:"+this.sC+"."+this.sM;
		jz+=" - url:"+this.url;
		jz+=" - policy:"+this.policy;
		jz+=" - methodCode:"+this.HTTPMethodCode;
		jz+=" - auth:"+this.requireAuth;	
		List<String> ckList= new ArrayList<>();
		for(Map.Entry<Class<?>,Set<Method>> entry : checkouts.entrySet())
			for(Method m : entry.getValue())
				ckList.add(entry.getKey().getCanonicalName()+"."+m.getName());		
		jz+=" - checkouts:"+ckList;
		jz+="\n- reqestParams:{"+requestParams+"}";
		jz+="\n- jsonOutParams:{"+jsonOutParams+"}";
		return jz;
	}
	
	
	public final String getUrl() {return url;}

	public final Class<? extends JEEZServlet> getHTTPPolicy() {return policy;}

	public final int getHTTPMethodCode() {return HTTPMethodCode;}

	public final String getSC() {return sC;}

	public final String getSM() {return sM;}

	public final Map<Class<?>, Set<Method>> getCheckouts() {return checkouts;}

	public TemplateParams getJsonOutParams() {return jsonOutParams;}

	public TemplateParams getRequestParams() {return requestParams;}

	public Boolean requireAuth() {return requireAuth;}

}
