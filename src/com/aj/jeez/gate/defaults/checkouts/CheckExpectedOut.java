package com.aj.jeez.gate.defaults.checkouts;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Checkout;
import com.aj.jeez.gate.representation.templates.TemplateParam;
import com.aj.jeez.gate.representation.templates.TemplateParams;

public class CheckExpectedOut {

	@Checkout
	public static boolean checkExpectedOut(
			Object result,
			TemplateParams jsonOut
			){
		if(! (result instanceof JSONObject)) 
			return false;
		
		for(TemplateParam tp : jsonOut.getExpecteds())
			if(! ((JSONObject) result).has(tp.getName()) ) 
				return false;
		
		return true;
	}	
}
