package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FundManager;

import lombok.Data;

@Data
public class AddOrUpdateFundManagerParam {

	private String id;

	@NotBlank
	private String fullName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date practiceTime;

	@NotBlank
	private String intro;
	
	public FundManager convertToPo() {
		FundManager po = new FundManager();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
