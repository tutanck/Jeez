package zlink.tools.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @author AJoan
 * **@goodToKnow ! FLUENT STYLE CODE*/
public class ToolBox {


	/**
	 * TODO work while is run as java application but not on server
	 * no error , silent execution with no visible effects 
	 * @param e	 */
	@Deprecated
	public static void logStackTrace(Exception e){  //Dont work on ovh
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		File f=new File("./logs");
		if(!f.exists()) f.mkdir();
		FileWriter fw=null;
		try {fw=new FileWriter("./logs/errorLog"+getCurentTimeStamp().toString().replace(":", "_")+".log");
		fw.write(sw.toString());// stack trace in the log file
		} catch (IOException e1) {e1.printStackTrace();}
		finally {try {fw.close();} catch (IOException e1) {e1.printStackTrace();}}
	}



	/**
	 * @description  Generate an integer ID using Random.nextInt() Method   
	 * @return */
	public static int generateSimpleIntId() {
		return new Random().nextInt(Integer.MAX_VALUE);
	}


	/**
	 * Return the complete StackTrace of the throwable as String
	 * @param thr
	 * @return */
	public static String getStackTrace(Throwable thr){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		thr.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}




	/**
	 * @description return the current time in format day/month/year/ hour:minutes:seconds
	 * @return */
	public static String getCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/ HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date); //21/02/2015/ 13:52:11
	}




	/**
	 * @description return the current timestamp
	 * @return */
	public static Timestamp getCurentTimeStamp(){
		return new Timestamp(new Date().getTime());
	}


	/**
	 * @description
	 * Return a string corresponding to the chosen algorithm's encode
	 * @param token
	 * @param id
	 * @return */
	public static String scramble(
			String string
			){
		return DigestUtils.shaHex(string);
	}

	
	

	/**
	 * @description
	 * Generate a homemade token of n characters 
	 * @return */
	public static String generateAlphaNumToken(int n) {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		String alphanum = "azertyuiopqsdfghjklmwxcvbn1234567890AZERTYUIOPQSDFGHJKLMWXCVBN";
		for(int i = 0; i < n; i++) 
			sb.append(alphanum.charAt(r.nextInt(alphanum.length())));
		return sb.toString();
	}
	
	
	/**
	 * @description
	 * Generate a homemade token of n characters 
	 * @return */
	public static int generateIntToken(int n) {
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for(int i=0;i<n;i++)
			sb.append(r.nextInt(10));
		return Integer.valueOf(sb.toString());
	}
	
	

	/**
	 * @description 
	 * Return a predefined JSONObject containing
	 * information about the internal error that occurred.
	 * Only useful on admin mode
	 * iserror = (internal server error)'s acronym   
	 * @param thr
	 * @return */
	public static JSONObject iserror(Throwable thr) throws JSONException{
		return new JSONObject().
				put("iserror",getStackTrace(thr))
				.put("errorpage","/Momento/err.jsp");
	}





	public static void main(String[] args) {
		System.out.println(getCurrentTime());
		System.out.println(generateSimpleIntId());
	}
}
