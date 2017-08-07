package com.aj.jeez.gate.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.aj.jeez.gate.core.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.gate.representation.annotations.WebService;

/**
 * The ServicesRadar is a service finder from built classes (.class extension).
 * It scans the class and returns the methods annotated with the @WebService annotation.   
 * @author Joan */
public class WebServicesRadar {

	/**
	 * Return a map containing all the methods 
	 * annotated with the @WebService annotation 
	 * for each class in the classes obtained after 
	 * performing a 'Class.forName'' operation 
	 * on each class name contained in the {classesQNSet}
	 * @param classesQNSet
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WebServiceAnnotationMisuseException */
	public static Map<Class<?>,Set<Method>> findAnnotatedServices (
			Set<String> classesQNSet
			) throws ClassNotFoundException, WebServiceAnnotationMisuseException{
		Map<Class<?>,Set<Method>> classesMethods = new HashMap<>();
		for(String classQN : classesQNSet){
			Class<?> clazz =  Class.forName(classQN);
			classesMethods.put(clazz,findAnnotatedServices(clazz));
		}
		return classesMethods;
	}

	
	/**
	 * Find all the methods annotated with the 
	 * @WebService annotation in the class obtained after 
	 * performing a 'Class.forName'({classQN})
	 * @param classQN
	 * @return
	 * @throws ClassNotFoundException
	 * @throws WebServiceAnnotationMisuseException */
	public static Set<Method> findAnnotatedServices (
			String classQN
			) throws ClassNotFoundException, WebServiceAnnotationMisuseException{
		return findAnnotatedServices(Class.forName(classQN));
	}


	/**
	 * Find all the methods annotated with the 
	 * @WebService in the class {clazz}
	 * @param clazz
	 * @return
	 * @throws WebServiceAnnotationMisuseException */
	public static Set<Method> findAnnotatedServices (
			Class<?> clazz
			) throws WebServiceAnnotationMisuseException {
		String str0="The registred WebService : 'public static JSONObject ";
		String str1 = "(JSONObject jsonObject);' must ";
		Set<Method> servicesSet = new HashSet<>();

		try {
			for ( Method m : clazz.getMethods() ) {

				if(m.getAnnotation(WebService.class)==null) continue;

				String msg =str0+clazz.getName()+"."+m.getName()+str1;

				if (Modifier.isAbstract(m.getModifiers())) 
					throw new WebServiceAnnotationMisuseException(msg+"not be abstract");

				if (!Modifier.isPublic(m.getModifiers())) 
					throw new WebServiceAnnotationMisuseException(msg+"be public");

				if (!Modifier.isStatic(m.getModifiers()) ) 
					throw new WebServiceAnnotationMisuseException(msg+"be static");

				Class<?>[] parameterTypes = m.getParameterTypes();
				if (parameterTypes.length != 1) 
					throw new WebServiceAnnotationMisuseException(msg+"have exactly one argument of type JSONObject");
				else
				if(!JSONObject.class.isAssignableFrom(parameterTypes[0]))
					throw new WebServiceAnnotationMisuseException(msg+"have exactly one argument of type JSONObject");

				servicesSet.add(m);				
			}
			return servicesSet;
		} catch (SecurityException e1) {throw new RuntimeException(e1);}
	}
}
