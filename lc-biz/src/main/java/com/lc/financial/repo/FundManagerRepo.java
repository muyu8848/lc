package com.lc.financial.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FundManager;

public interface FundManagerRepo extends JpaRepository<FundManager, String>, JpaSpecificationExecutor<FundManager> {

}
