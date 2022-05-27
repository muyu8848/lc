package com.lc.financial.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "monetary_fund")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MonetaryFund implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String fundName;

	private String fundCode;

	private String fundCategory;

	private String operationMode;

	private Date establishedTime;

	private String fundSize;

	private String fundCustody;

	private String issuingAgency;

	private String fundIntro;

	private Date createTime;

	private Date lastModifyTime;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Version
	private Long version;

	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}

}
