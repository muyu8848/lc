package com.lc.financial.domain;

import java.io.Serializable;
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
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.lc.common.utils.IdUtils;
import com.lc.constants.Constant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "financial_income_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class FinancialIncomeRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;
	
	private Double generateIncomeAmount;

	private Double rateOfReturn;
	
	private Double generateIncomeQuota;
	
	private Double chg;

	private Double income;

	private String state;

	private Date settlementTime;

	private Date createTime;

	private Date theDate;

	@Version
	private Long version;

	@Column(name = "buy_in_record_id", length = 32)
	private String buyInRecordId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buy_in_record_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private FinancialBuyInRecord buyInRecord;

	public static FinancialIncomeRecord buildIncomeType(Double generateIncomeAmount, Double rateOfReturn, Double income,
			Date theDate, String buyInRecordId) {
		FinancialIncomeRecord po = new FinancialIncomeRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.理财收益状态_待结算);
		po.setGenerateIncomeAmount(generateIncomeAmount);
		po.setRateOfReturn(rateOfReturn);
		po.setIncome(income);
		po.setTheDate(theDate);
		po.setBuyInRecordId(buyInRecordId);
		return po;
	}
	
	public static FinancialIncomeRecord buildNpvType(Double generateIncomeQuota, Double chg, Double income,
			Date theDate, String buyInRecordId) {
		FinancialIncomeRecord po = new FinancialIncomeRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.理财收益状态_待结算);
		po.setGenerateIncomeQuota(generateIncomeQuota);
		po.setChg(chg);
		po.setIncome(income);
		po.setTheDate(theDate);
		po.setBuyInRecordId(buyInRecordId);
		return po;
	}

}
