package com.lc.common.param;

import lombok.Data;

@Data
public class PageParam {

	private Integer pageNum;

	private Integer pageSize;

	private String propertie;

	private String direction;

}
