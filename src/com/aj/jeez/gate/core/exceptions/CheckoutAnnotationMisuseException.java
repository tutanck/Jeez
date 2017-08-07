package com.aj.jeez.gate.core.exceptions;

public class CheckoutAnnotationMisuseException extends Exception {

	private static final long serialVersionUID = 1L;

	public CheckoutAnnotationMisuseException() {}
	
	public CheckoutAnnotationMisuseException(
			String msg
			) {
		super(msg);
	}

}
