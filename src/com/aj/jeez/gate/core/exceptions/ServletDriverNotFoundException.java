package com.aj.jeez.gate.core.exceptions;

import javax.servlet.ServletException;

public class ServletDriverNotFoundException extends ServletException {

	private static final long serialVersionUID = 1L;

	public ServletDriverNotFoundException() {}
	
	public ServletDriverNotFoundException(
			String msg
			) {
		super(msg);
	}

}
