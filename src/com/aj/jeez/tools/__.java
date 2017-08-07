package com.aj.jeez.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Underline __
 * @author Joan */
public class __ {

	public static boolean truthy(Object obj){return true;}

	public static boolean falsy(Object object){return false;}

	public static boolean isNull(Object object) {return object==null;}
	
	public static boolean isEmpty(String string) {return isNull(string) || string.length()==0;}
		
	public static boolean isEmpty(Object ...objects) {return isNull(objects) || objects.length==0;}

	public static boolean isVoidSpace(String string) {return isNull(string) || isEmpty(string.trim());}

	public static Set<String> toSet(String... strings) {return isEmpty((Object[])strings) ? Collections.emptySet() : new HashSet<String>(Arrays.asList(strings));}

	public static Set<Object> toSet(Object... objects) {return (isEmpty(objects)) ? Collections.emptySet() : new HashSet<Object>(Arrays.asList(objects));}

	public static List<String> toList(String... strings) {return (isEmpty((Object[])strings)) ? Collections.emptyList() : (List<String>) Arrays.asList(strings);}

	public static List<Object> toList(Object... objects) {return (isEmpty(objects)) ? Collections.emptyList() : Arrays.asList(objects);}

	public static Set<?> safe(Set<?> set) {return isNull(set) ? Collections.EMPTY_SET : set;}

	public static Set<String> stringSet(Set<String> set) {return isNull(set) ? Collections.emptySet() : set;}

	public static List<?> safe(List<?> list) {return isNull(list) ? Collections.EMPTY_LIST : list;}

	public static Map<?,?> safe(Map<?,?> map) {return isNull(map) ? Collections.EMPTY_MAP : map;}

	public static Void outln(Object object){System.out.println(object);return null;}

	public static Void out(Object object){System.out.print(object);return null;}

	public static Void errln(Object object){System.err.println(object);return null;}

	public static Void err(Object object){System.err.print(object);return null;}

	public static boolean civilized(String input,String ...rules) {return civilized(input,toSet(rules));}

	public static boolean civilized(String input,Set<String>rules) {
		for(String rule : stringSet(rules))	
			if(/*__.isVoidSpace(input) ||*/ !Pattern.compile(rule).matcher(input).matches()) 
				return falsy(outln("__/civilized : This rude '"+input+"' doesn't respect the following rule '"+rule+"'")); 
		return truthy (outln("__/civilized : This civilized '"+input+"' respect all the rules '"+rules+"'"));}

	public static void explode(Throwable e) {e.printStackTrace();throw new RuntimeException(e);}

}
