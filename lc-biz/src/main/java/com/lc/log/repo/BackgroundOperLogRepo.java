package com.lc.log.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.log.domain.BackgroundOperLog;


public interface BackgroundOperLogRepo extends JpaRepository<BackgroundOperLog, String>, JpaSpecificationExecutor<BackgroundOperLog> {
	
}
