package com.lc.dictconfig.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.lc.dictconfig.domain.DictItem;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

/**
 * 字典数据
 * @author 10257
 *
 */
@Data
public class DictItemVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;

	/**
	 * 字典编码
	 */
	private String dictItemCode;

	/**
	 * 字典名称
	 */
	private String dictItemName;

	/**
	 * 排序号
	 */
	private Double orderNo;

	public static List<DictItemVO> convertFor(List<DictItem> dictItems) {
		if (CollectionUtil.isEmpty(dictItems)) {
			return new ArrayList<>();
		}
		List<DictItemVO> vos = new ArrayList<>();
		for (DictItem dictItem : dictItems) {
			vos.add(convertFor(dictItem));
		}
		return vos;
	}

	public static DictItemVO convertFor(DictItem dictItem) {
		if (dictItem == null) {
			return null;
		}
		DictItemVO vo = new DictItemVO();
		BeanUtils.copyProperties(dictItem, vo);
		return vo;
	}

}
