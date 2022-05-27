package com.lc.financial.param;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FundManagerQueryCondParam extends PageParam {

	private String fullName;

}
