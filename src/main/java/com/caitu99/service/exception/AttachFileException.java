package com.caitu99.service.exception;


public class AttachFileException extends ApiException {
	
	private static final long serialVersionUID = 1537901235563361521L;

	public AttachFileException(int code, String description) {
		super(code, description);
	}
	
	public AttachFileException(int code, String description, String data) {
		super(code, description, data);
	}
	
}
