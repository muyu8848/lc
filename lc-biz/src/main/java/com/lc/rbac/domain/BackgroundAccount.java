package com.lc.rbac.domain;

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
@Table(name = "background_account")
@DynamicInsert(true)
@DynamicUpdate(true)
public class BackgroundAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String userName;

	private String loginPwd;
	
	private Boolean superAdminFlag;

	private String state;

	private Date registeredTime;

	private Date latelyLoginTime;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Version
	private Long version;

	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}
	
}
