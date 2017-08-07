package com.aj.jeez.gate.core.exceptions;

public class ClassPathScannerNotConfiguredException extends Exception {

	private static final long serialVersionUID = 1L;

	public ClassPathScannerNotConfiguredException() {}
	
	public ClassPathScannerNotConfiguredException(
			String msg
			) {
		super(msg);
	}

}
