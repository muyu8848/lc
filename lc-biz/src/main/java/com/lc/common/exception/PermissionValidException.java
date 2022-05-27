package com.lc.common.exception;

import lombok.Getter;

@Getter
public class PermissionValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer code;

	private String msg;
	
	public PermissionValidException(BizError bizError) {
		super(bizError.getMsg());
		this.code = bizError.getCode();
		this.msg = bizError.getMsg();
	}

}
