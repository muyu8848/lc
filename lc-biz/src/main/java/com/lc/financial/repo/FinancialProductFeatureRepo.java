package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialProductFeature;

public interface FinancialProductFeatureRepo
		extends JpaRepository<FinancialProductFeature, String>, JpaSpecificationExecutor<FinancialProductFeature> {

	List<FinancialProductFeature> findByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNo(
			String financialProductId);

	FinancialProductFeature findTopByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNoDesc(
			String financialProductId);

	List<FinancialProductFeature> findByIdInAndDeletedFlagIsFalse(List<String> ids);

}
