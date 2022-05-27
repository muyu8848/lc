package com.lc.rbac.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.rbac.domain.BackgroundAccount;

public interface BackgroundAccountRepo extends JpaRepository<BackgroundAccount, String>, JpaSpecificationExecutor<BackgroundAccount> {

	BackgroundAccount findByIdAndDeletedFlagIsFalse(String id);
	
	BackgroundAccount findByUserNameAndDeletedFlagIsFalse(String userName);

	List<BackgroundAccount> findByIdNotIn(List<String> ids);
	
	List<BackgroundAccount> findBySuperAdminFlagTrueAndDeletedFlagIsFalse();

}
