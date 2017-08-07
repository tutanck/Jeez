package com.aj.jeez.gate.representation.templates;

import java.util.HashSet;
import java.util.Set;

import com.aj.jeez.gate.core.exceptions.InconsistentParametersException;

public class TemplateParams {

	private final Set<TemplateParam> expecteds;
	private final Set<TemplateParam> optionals;

	public TemplateParams(){
		this.expecteds=new HashSet<>();
		this.optionals=new HashSet<>();
	}

	public void addExpected(
			TemplateParam expected
			) throws InconsistentParametersException{
		if(expected==null) return;
		
		if(expecteds.contains(expected))
			throw new InconsistentParametersException
			("Unable to add this expected parameter '"+expected+"' : name collision detected in expecteds!");
		
		if(optionals.contains(expected)) 
			throw new InconsistentParametersException
			("Unable to add this expected parameter '"+expected+"' : name collision detected in optionals!");

		expecteds.add(expected);
	}


	public void addOptional(
			TemplateParam optional
			) throws InconsistentParametersException{
		if(optional==null) return;
		
		if(expecteds.contains(optional))
			throw new InconsistentParametersException
			("Unable to add this optional parameter '"+optional+"' : name collision detected in expecteds!");
		
		if(optionals.contains(optional)) 
			throw new InconsistentParametersException
			("Unable to add this optional parameter '"+optional+"' : name collision detected in optionals!");
		
		optionals.add(optional);
	}

	public Set<TemplateParam> getOptionals() {return optionals;}
	public Set<TemplateParam> getExpecteds() {return expecteds;}

	public boolean isEmpty(){return optionalsEmpty() && expectedsEmpty();}
	public boolean expectedsEmpty(){return getExpecteds().isEmpty();}
	public boolean optionalsEmpty(){return getOptionals().isEmpty();}

	@Override
	public String toString() {
		return "{expecteds :"+expecteds+" - optionals:"+optionals+"}";
	}
}
