package com.lc.financial.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.TransactionDetail;

public interface TransactionDetailRepo
		extends JpaRepository<TransactionDetail, String>, JpaSpecificationExecutor<TransactionDetail> {

}
