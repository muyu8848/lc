package com.lc.financial.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.common.valid.ParamValid;
import com.lc.common.vo.PageResult;
import com.lc.financial.domain.FundManager;
import com.lc.financial.domain.FundManagerAchievement;
import com.lc.financial.param.AddOrUpdateFundManagerAchievementParam;
import com.lc.financial.param.AddOrUpdateFundManagerParam;
import com.lc.financial.param.FundManagerQueryCondParam;
import com.lc.financial.repo.FundManagerAchievementRepo;
import com.lc.financial.repo.FundManagerRepo;
import com.lc.financial.vo.FundManagerAchievementVO;
import com.lc.financial.vo.FundManagerVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class FundManagerService {

	@Autowired
	private FundManagerRepo fundManagerRepo;

	@Autowired
	private FundManagerAchievementRepo fundManagerAchievementRepo;

	@Transactional(readOnly = true)
	public FundManagerAchievementVO findFundManagerAchievement(@NotBlank String id) {
		return FundManagerAchievementVO.convertFor(fundManagerAchievementRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public List<FundManagerAchievementVO> findFundManagerAchievementByFundManagerId(@NotBlank String fundManagerId) {
		return FundManagerAchievementVO.convertFor(fundManagerAchievementRepo
				.findByFundManagerIdAndDeletedFlagIsFalseOrderByStartManageTimeDesc(fundManagerId));
	}

	@Transactional
	public void delFundManagerAchievement(@NotBlank String id) {
		FundManagerAchievement achievement = fundManagerAchievementRepo.getById(id);
		achievement.deleted();
		fundManagerAchievementRepo.save(achievement);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateFundManagerAchievement(AddOrUpdateFundManagerAchievementParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			FundManagerAchievement achievement = param.convertToPo();
			fundManagerAchievementRepo.save(achievement);
		}
		// 修改
		else {
			FundManagerAchievement achievement = fundManagerAchievementRepo.getById(param.getId());
			BeanUtils.copyProperties(param, achievement);
			achievement.setLastModifyTime(new Date());
			fundManagerAchievementRepo.save(achievement);
		}
	}

	@Transactional(readOnly = true)
	public FundManagerVO findFundManager(@NotBlank String id) {
		return FundManagerVO.convertFor(fundManagerRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public PageResult<FundManagerVO> findFundManagerByPage(FundManagerQueryCondParam param) {
		Specification<FundManager> spec = new Specification<FundManager>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<FundManager> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getFullName())) {
					predicates.add(builder.equal(root.get("fullName"), param.getFullName()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<FundManager> result = fundManagerRepo.findAll(spec, PageRequest.of(param.getPageNum() - 1,
				param.getPageSize(), Sort.by(Sort.Order.desc("lastModifyTime"))));
		PageResult<FundManagerVO> pageResult = new PageResult<>(FundManagerVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void delFundManager(@NotBlank String id) {
		FundManager fundManager = fundManagerRepo.getById(id);
		fundManager.deleted();
		fundManagerRepo.save(fundManager);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateFundManager(AddOrUpdateFundManagerParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			FundManager fundManager = param.convertToPo();
			fundManagerRepo.save(fundManager);
		}
		// 修改
		else {
			FundManager fundManager = fundManagerRepo.getById(param.getId());
			BeanUtils.copyProperties(param, fundManager);
			fundManager.setLastModifyTime(new Date());
			fundManagerRepo.save(fundManager);
		}
	}

}
