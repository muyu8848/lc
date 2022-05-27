package com.lc.common.exception;

import lombok.Getter;

@Getter
public class ParamValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer code;

	private String msg;

	public ParamValidException(Integer code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

}
