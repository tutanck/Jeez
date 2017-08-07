package com.aj.jeez.gate.core.exceptions;

public class InconsistentParametersException extends Exception {

	private static final long serialVersionUID = 1L;

	public InconsistentParametersException() {}
	
	public InconsistentParametersException(
			String msg
			) {
		super(msg);
	}

}
