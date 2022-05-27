package com.lc.common.vo;

import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {

	private int pageNum;

	private int pageSize;

	private long totalPage;

	private long total;

	private int size;

	private List<T> content;

	public PageResult(List<T> content, int pageNum, int pageSize, long total) {
		this.content = content;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.totalPage = total == 0 ? 0 : total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1);
		this.total = total;
		this.size = content.size();
	}

}
