package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.MonetaryFund;

public interface MonetaryFundRepo extends JpaRepository<MonetaryFund, String>, JpaSpecificationExecutor<MonetaryFund> {
	
	List<MonetaryFund> findByDeletedFlagIsFalseOrderByCreateTimeDesc();

}
