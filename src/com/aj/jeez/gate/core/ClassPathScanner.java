package com.aj.jeez.gate.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

import com.aj.jeez.gate.core.exceptions.ClassPathScannerNotConfiguredException;

/**
 * The ClassPathScanner is a configurable .class files finder.
 * @author ANAGBLA Joan */
 class ClassPathScanner {	 

	private static String classPath;
	
	/**
	 * Return all the classes qualified names 
	 * found in the {classPath}
	 * @param context
	 * @return
	 * @throws ClassPathScannerNotConfiguredException */
	static Set<String> getClassesQualifiedNames(
			ServletContext context
			)throws ClassPathScannerNotConfiguredException {
		
		isConfigured();
		System.out.println("ClassPathScanner/getClassesQualifiedNames:: start browsing classes... ");//debug
		Set<String> clssStrSet= getClassesQNRec(classPath, context, new HashSet<>());
		System.out.println("ClassPathScanner/getClassesQualifiedNames:: "+clssStrSet.size()+" class files found. ");//debug
		return clssStrSet;
	}
	
	
	
	/**
	 * Browse recursively then return all the classes qualified names 
	 * found in the {packagePath}
	 * @param packagePath
	 * @param context
	 * @param classesQNSet
	 * @return */
	private static Set<String> getClassesQNRec(
			String packagePath,
			ServletContext context,
			Set<String> classesQNSet){
		
		Set<String> resourceSet = context.getResourcePaths(packagePath);
		//System.out.println("ClassPathScanner/getClassesQNRec::resourceSet('"+packagePath+"') = "+resourceSet);//debug
		if (resourceSet != null) 
			for (Iterator<String> iterator = resourceSet.iterator(); iterator.hasNext();) {
				String resourcePath = iterator.next();
				if (resourcePath.endsWith(".class")){
					String classRelativePath = resourcePath.replace(classPath,"");
					//System.out.println("ClassPathScanner/getClassesQNRec::classRelativePath : "+classRelativePath);
 					classesQNSet.add(classRelativePath.replace("/", ".").substring(0,classRelativePath.indexOf(".class")));
				}else 
					getClassesQNRec(resourcePath, context, classesQNSet);	
			}
		return classesQNSet;
	}
	
		

	/**
	 * Allow to know if the ClassPathScanner is initialized :
	 * i.e. if the {classPath} 
	 * attributes is initialized (not null)
	 * @throws ClassPathScannerNotConfiguredException */
	static void isConfigured() throws ClassPathScannerNotConfiguredException {
		if(classPath==null || classPath.length()==0)
		throw new ClassPathScannerNotConfiguredException("classPath is not configured");
	}
	
	/**
	 * Allow to configure the ClassPathScanner by setting the {classPath}  
	 * @param classPath */
	static void configure(
			String classPath
			) {
		ClassPathScanner.classPath=classPath;
	}

}
