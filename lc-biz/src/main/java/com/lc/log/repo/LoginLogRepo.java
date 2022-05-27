package com.lc.log.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.log.domain.LoginLog;

public interface LoginLogRepo extends JpaRepository<LoginLog, String>, JpaSpecificationExecutor<LoginLog> {
	
}
