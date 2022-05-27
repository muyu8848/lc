package com.lc.financial.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EverydayCumsumIncomeVO {

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date theDate;

	private Double value;
	
	public static EverydayCumsumIncomeVO build(Date theDate, Double value) {
		EverydayCumsumIncomeVO vo = new EverydayCumsumIncomeVO();
		vo.setTheDate(theDate);
		vo.setValue(value);
		return vo;
	}

}
