package com.lc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizError {

	请登录(999, "请登录"),

	无权访问(1000, "无权访问"),

	参数异常(1001, "参数异常"),

	业务异常(1002, "业务异常");

	private Integer code;

	private String msg;

}
