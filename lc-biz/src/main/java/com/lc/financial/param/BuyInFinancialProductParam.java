package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.constants.Constant;
import com.lc.financial.domain.FinancialBuyInRecord;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class BuyInFinancialProductParam {

	@NotBlank
	private String financialProductId;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double buyInAmount;

	@NotBlank
	private String accountId;

	public FinancialBuyInRecord convertToPo() {
		FinancialBuyInRecord po = new FinancialBuyInRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setBuyInDate(DateUtil.beginOfDay(po.getCreateTime()).toJdkDate());
		po.setState(Constant.理财产品买入记录状态_持有中);
		po.setAvailableAmount(po.getBuyInAmount());
		po.setIncome(0d);
		po.setBuyInNpv(0d);
		po.setBuyInQuota(0d);
		po.setAvailableQuota(0d);
		return po;
	}

}
