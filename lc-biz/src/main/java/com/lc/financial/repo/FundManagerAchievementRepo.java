package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FundManagerAchievement;

public interface FundManagerAchievementRepo
		extends JpaRepository<FundManagerAchievement, String>, JpaSpecificationExecutor<FundManagerAchievement> {
	
	
	List<FundManagerAchievement> findByFundManagerIdAndDeletedFlagIsFalseOrderByStartManageTimeDesc(String fundManagerId);

}
