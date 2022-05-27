package com.lc.log.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "background_oper_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class BackgroundOperLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String module;

	private String operate;

	private String requestMethod;

	private String requestUrl;

	private String requestParam;

	private String ipAddr;

	private String operAccountId;

	private String operAccountUserName;

	private Date operTime;

}
