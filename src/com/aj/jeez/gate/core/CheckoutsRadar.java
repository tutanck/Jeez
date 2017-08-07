package com.aj.jeez.gate.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aj.jeez.gate.core.exceptions.CheckoutAnnotationMisuseException;
import com.aj.jeez.gate.representation.annotations.Checkout;
import com.aj.jeez.gate.representation.templates.TemplateParams;

/**
 * The CheckoutsRadar is a checkouts finder from built classes (.class extension).
 * It scans the class and returns the methods annotated with the @Checkout annotation.   
 * @author Joan */
 public class CheckoutsRadar {

	/**
	 * Return a map containing all the methods 
	 * annotated with the @Checkout annotation 
	 * for each class in the {classSet}
	 * @param classesSet
	 * @return 
	 * @throws CheckoutAnnotationMisuseException */
	 public static Map<Class<?>,Set<Method>> findAnnotatedCheckouts (
			Set<Class<?>> classSet
			) throws CheckoutAnnotationMisuseException{
		Map<Class<?>,Set<Method>> classesMethods = new HashMap<>();
		for(Class<?> clazz : classSet)
			classesMethods.put(clazz,findAnnotatedCheckouts(clazz));
		return classesMethods;
	}


	/**
	 * Find all the methods annotated with the 
	 * @Checkout annotation in the class {clazz}
	 * @param clazz
	 * @return
	 * @throws CheckoutAnnotationMisuseException */
	 static Set<Method> findAnnotatedCheckouts (
			Class<?> clazz
			) throws CheckoutAnnotationMisuseException {
		String str0="The registred Checkout : 'public static boolean ";
		String str1 = "(Object result,String[]expectedOut,String[]optionalOut);' must ";
		Set<Method> servicesSet = new HashSet<>();
	
		try {
			for ( Method m : clazz.getMethods() ) {

				if(m.getAnnotation(Checkout.class)==null) continue;

				String msg =str0+clazz.getName()+"."+m.getName()+str1;

				if (Modifier.isAbstract(m.getModifiers())) 
					throw new CheckoutAnnotationMisuseException(msg+"not be abstract");

				if (!Modifier.isPublic(m.getModifiers())) 
					throw new CheckoutAnnotationMisuseException(msg+"be public");

				if (!Modifier.isStatic(m.getModifiers()) ) 
					throw new CheckoutAnnotationMisuseException(msg+"be static");
				
				if (!(m.getReturnType().equals(boolean.class) || m.getReturnType().equals(Boolean.class))) 
					throw new CheckoutAnnotationMisuseException(msg+"return a boolean or a Boolean");

				if (m.getExceptionTypes().length>0) 
					throw new CheckoutAnnotationMisuseException(msg+"not throws exceptions");
				
				Class<?>[] parameterTypes = m.getParameterTypes();
				
				String str3="have exactly 2 arguments of the following types in this order :  "+Object.class.getCanonicalName()+","+TemplateParams.class.getCanonicalName();
				
				if (parameterTypes.length != 2) 
					throw new CheckoutAnnotationMisuseException(msg+str3);
				else
					if(!(	Object.class.isAssignableFrom(parameterTypes[0])
							&&TemplateParams.class.isAssignableFrom(parameterTypes[1])  ) )
						throw new CheckoutAnnotationMisuseException(msg+str3);

				servicesSet.add(m);				
			}
			return servicesSet;
		} catch (SecurityException e1) {throw new RuntimeException(e1);}
	}
}
