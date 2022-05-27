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
import com.lc.memberaccount.domain.MemberAccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transaction_detail")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TransactionDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double amount;

	private Date createTime;

	private String type;

	@Version
	private Long version;

	@Column(name = "financial_product_id", length = 32)
	private String financialProductId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financial_product_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private FinancialProduct financialProduct;

	@Column(name = "account_id", length = 32)
	private String accountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MemberAccount account;
	
	@Column(name = "buy_in_record_id", length = 32)
	private String buyInRecordId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buy_in_record_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private FinancialBuyInRecord buyInRecord;
	
	@Column(name = "take_out_record_id", length = 32)
	private String takeOutRecordId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "take_out_record_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private FinancialTakeOutRecord takeOutRecord;

	public static TransactionDetail buildWithTakeOut(FinancialTakeOutRecord takeOutRecord) {
		TransactionDetail po = new TransactionDetail();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setAmount(-takeOutRecord.getAmount());
		po.setType(Constant.交易明细类型_取出);
		po.setTakeOutRecordId(takeOutRecord.getId());
		po.setFinancialProductId(takeOutRecord.getFinancialProductId());
		po.setAccountId(takeOutRecord.getAccountId());
		return po;
	}
	
	public static TransactionDetail buildWithBuyIn(FinancialBuyInRecord buyInRecord) {
		TransactionDetail po = new TransactionDetail();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setAmount(buyInRecord.getBuyInAmount());
		po.setType(Constant.交易明细类型_买入);
		po.setBuyInRecordId(buyInRecord.getId());
		po.setFinancialProductId(buyInRecord.getFinancialProductId());
		po.setAccountId(buyInRecord.getAccountId());
		return po;
	}

}
