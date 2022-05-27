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
@Table(name = "financial_company")
@DynamicInsert(true)
@DynamicUpdate(true)
public class FinancialCompany implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String companyName;

	private String companyAddress;

	private String weChatOfficialAccount;

	private String contactNumber;

	private String websiteUrl;
	
	private String basicInformation;
	
	private String investmentCapacity;
	
	private String ownershipStructure;

	private Date createTime;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Version
	private Long version;

	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}

}
