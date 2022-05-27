package com.lc.financial.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.lc.common.utils.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "everyday_rate_of_return")
@DynamicInsert(true)
@DynamicUpdate(true)
public class EverydayRateOfReturn {

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double rateOfReturn;

	private Double chg;

	private Double npv;

	private Date theDate;

	private Date createTime;

	@Column(name = "financial_product_id", length = 32)
	private String financialProductId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financial_product_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private FinancialProduct financialProduct;

	public static EverydayRateOfReturn build(Double chg, Double npv, Date theDate, String financialProductId) {
		EverydayRateOfReturn po = new EverydayRateOfReturn();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setChg(chg);
		po.setNpv(npv);
		po.setTheDate(theDate);
		po.setFinancialProductId(financialProductId);
		return po;
	}

	public static EverydayRateOfReturn build(Double rateOfReturn, Date theDate, String financialProductId) {
		EverydayRateOfReturn po = new EverydayRateOfReturn();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setRateOfReturn(rateOfReturn);
		po.setTheDate(theDate);
		po.setFinancialProductId(financialProductId);
		return po;
	}

}
