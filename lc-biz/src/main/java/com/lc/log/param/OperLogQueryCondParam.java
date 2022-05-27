package com.lc.log.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OperLogQueryCondParam extends PageParam {

	private String ipAddr;

	private String userName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime;

}
