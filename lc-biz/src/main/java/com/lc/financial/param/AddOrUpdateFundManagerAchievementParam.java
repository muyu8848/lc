package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FundManagerAchievement;

import lombok.Data;

@Data
public class AddOrUpdateFundManagerAchievementParam {
	
	private String id;

	@NotBlank
	private String fundName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startManageTime;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double rateOfReturn;
	
	@NotBlank
	private String fundManagerId;
	
	public FundManagerAchievement convertToPo() {
		FundManagerAchievement po = new FundManagerAchievement();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
