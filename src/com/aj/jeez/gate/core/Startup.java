package com.aj.jeez.gate.core;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONObject;

@WebListener 
public class Startup
implements ServletContextListener{

	private final String defaultClassPath="/WEB-INF/classes/";
	private String classPath=defaultClassPath;
	private static JSONObject router;

	
	//Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("-------------------Hello, I'm JEE-Z------------------------------");
		System.out.println("StartupListener--> ServletContextListener started");
		System.out.println("-----------------------------------------------------------------");

		try {
			ClassPathScanner.configure(classPath);

			Set<String> classesQNSet;
			Map<Class<?>,Set<Method>> servicesMap;
			
			//Project classes scan and listing
			classesQNSet = ClassPathScanner.getClassesQualifiedNames(sce.getServletContext());
			System.out.println("StartupListener/contextInitialized:: classes qualified names list : ");//debug
			for(String classQN :classesQNSet)
				System.out.println("StartupListener/contextInitialized::classQualifiedName : "+classQN);//debug
			
			//WebService annotations detection in found classes
			servicesMap = WebServicesRadar.findAnnotatedServices(classesQNSet);
			System.out.println("StartupListener/contextInitialized::services list by class : ");//debug
			for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
				System.out.println("StartupListener/contextInitialized::servicesMap : "+classServices);//debug

			//Servlet assignment for each found annotation
			System.out.println("StartupListener/contextInitialized::assignServlets starting..");//debug
			router = ServicesManager.assignServlets(sce.getServletContext(),servicesMap);
			System.out.println("StartupListener/contextInitialized::assignServlets was sucessfull");//debug
			 
			//Routes listing
			for(String ServletID : router.keySet())
				System.out.println("StartupListener/contextInitialized::services's route : {"+ServletID+":"+router.get(ServletID)+"}");//debug

		} catch (Exception e) {throw new RuntimeException(e);}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("------------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener destroyed");
		System.out.println("-------------------------Bye--------------------------");
	}
	
	public static JSONObject getRouter() {	
		return router;	
	}
}