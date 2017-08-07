package com.aj.jeez.gate.representation.templates;

import java.util.Set;
import java.util.regex.Pattern;

import com.aj.jeez.gate.core.exceptions.ParamNamingException;
import com.aj.jeez.gate.core.exceptions.ParamRulingException;
import com.aj.jeez.gate.core.exceptions.ParamTypingException;
import com.aj.jeez.tools.__;

public class TemplateParam {

	private final String name;
	private final Class<?> type;
	private final Set<String> rules;

	public TemplateParam(
			String name
			) throws ParamTypingException, ParamNamingException, ParamRulingException{
		this(name,String.class,(String[])null);
	}

	public TemplateParam(
			String name,
			Class<?> type
			) throws ParamTypingException, ParamNamingException, ParamRulingException{
		this(name,type,(String[])null);
	}

	public TemplateParam(
			String name,
			Class<?> type,
			String[] rules
			) throws ParamTypingException, ParamNamingException, ParamRulingException{		
		this(name,type,__.toSet(rules));
	}

	public TemplateParam(
			String name,
			Class<?> type,
			Set<String> rules
			) throws ParamTypingException, ParamNamingException, ParamRulingException{		
		valid(name, type, rules);
		this.type=type;
		this.name=name;
		this.rules=(__.stringSet(rules));
	}


	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false; 
		return obj instanceof TemplateParam ? 
				((TemplateParam)obj).name.equals(name) : false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return "(name:"+name+",type:"+type+",rules"+rules+")";
	}

	public Set<String> getRules() {return rules;}
	public Class<?> getType() {return type;}
	public String getName() {return name;}
	
	
	private void valid(
			String name,
			Class<?> type,
			Set<String> rules
			)throws ParamTypingException, ParamNamingException, ParamRulingException{

		if(__.isVoidSpace(name))
			throw new ParamNamingException("Param name '"+name+"' must not be empty");

		if(!Pattern.compile("\\w+").matcher(name).matches()) //TODO  what if we begin by a number
			throw new ParamNamingException("Param name '"+name+"' must only contain alphanumeric characters or/and _");

		if(!typeIsValid(type))
			throw new ParamTypingException("Param '"+name+"' 's type must only be int,long,float,double,boolean or String");

		for(String rule : __.stringSet(rules))
			if(__.isVoidSpace(rule))
				throw new ParamRulingException("Each Param '"+name+"''s rule must not be empty");
	}
	
	
	private boolean typeIsValid(
			Class<?> type
			){		
		if(int.class.isAssignableFrom(type)) return true;
		if(long.class.isAssignableFrom(type)) return true;
		if(float.class.isAssignableFrom(type)) return true;
		if(double.class.isAssignableFrom(type)) return true;
		if(boolean.class.isAssignableFrom(type)) return true;
		if(String.class.isAssignableFrom(type)) return true;
		return false;
	}


	public int typeToInt() throws ParamTypingException{
		if(String.class.isAssignableFrom(type)) return 0; 
		if(int.class.isAssignableFrom(type)) return 1; 
		if(long.class.isAssignableFrom(type))return 2; 
		if(float.class.isAssignableFrom(type))return 3; 
		if(double.class.isAssignableFrom(type))return 4; 
		if(boolean.class.isAssignableFrom(type))return 5;
		throw new ParamTypingException("Unknown type '"+type+"'");
	}
	
	
}
