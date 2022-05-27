package com.lc.financial.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TenThousandIncomeVO {

	private Double value;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date theDate;

}
