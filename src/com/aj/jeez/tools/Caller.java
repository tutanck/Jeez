package com.aj.jeez.tools;


/** 
 * @author ANAGBLA Joan */

/**
 * TODO TOU KssE
 * @author Joan
 *
 */
public class Caller {

	public static boolean debug=false;

	public static String whoIsAsking(){
		StackTraceElement[] T = Thread.currentThread().getStackTrace();
		if(debug){
			String space =" ";
			for(StackTraceElement t :T){
				System.out.println(t);
				System.out.print(space+=" ");
			}
		}
		return T[T.length-1].getClassName();
	}
	
	public static int signature(){return whoIsAsking().hashCode();}


	public static void main(String[] args) {
		debug=true;
		System.out.println(whoIsAsking());
	}
}