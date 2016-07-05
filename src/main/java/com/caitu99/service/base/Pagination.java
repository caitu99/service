package com.caitu99.service.base;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class Pagination<E> implements Serializable {
	
	@JSONField(serialize=false)
	private static final long serialVersionUID = 8200109925832421945L;
	
	private int curPage = 1; // 当前页
	private int pageSize = 10; // 每页多少行
	private int totalRow = 0; // 共多少行
	private int totalPage = 0; // 共多少页
	private List<E> datas;//分页查询结果
	
	@JSONField(serialize=false)
	private int start = 0;// 当前页起始行
	
	@JSONField(serialize=false)
	private int end = 0;// 结束行
	
	@JSONField(serialize=false)
	private int fromPage=0; //展示起始页
	
	@JSONField(serialize=false)
	private int toPage=0;   //展示结束页
	
	@JSONField(serialize=false)
	private int showPageNum=3; //展示页数
	
	@JSONField(serialize=false)
	private int[] showPageSize={5,10,20,50};//页面展示5

	public int[] getShowPageSize() {
		return showPageSize;
	}

	public void setShowPageSize(int[] showPageSize) {
		this.showPageSize = showPageSize;
	}

	public List<E> getDatas() {
		return datas;
	}

	public void setDatas(List<E> datas) {
		this.datas = datas;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		if (curPage < 1) {
			curPage = 1;
		} else {
			start = pageSize * (curPage - 1);
		}
		end = start + pageSize > totalRow ? totalRow : start + pageSize;
		this.curPage = curPage;
	}

	public int getStart() {
		return start;
	}
	

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {

		return end;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize < 1) {
			pageSize = 10;
		} else {
			start = pageSize * (curPage - 1);
		}
		end = start + pageSize > totalRow ? totalRow : start + pageSize;
		this.pageSize = pageSize;
	}

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		if(totalRow>0){
			totalPage = (totalRow + pageSize - 1) / pageSize;
			this.totalRow = totalRow;
			if (totalPage < curPage) {
				curPage = totalPage;
				start = pageSize * (curPage - 1);
				end = totalRow;
				
			}
			start = pageSize * (curPage - 1);
			end = start + pageSize > totalRow ? totalRow : start + pageSize;

			fromPage=curPage-(showPageNum%2!=0?showPageNum/2:(showPageNum/2-1));
		}else{
			curPage = 0;
			totalPage = 0;
		}
		if(fromPage<1){
			fromPage=1;
		}
		toPage=fromPage+showPageNum-1;
		
		if(toPage>totalPage){
			toPage=totalPage;
		}
	}

	public int getTotalPage() {

		return this.totalPage;
	}

	public int getFromPage() {
		return fromPage;
	}

	public void setFromPage(int fromPage) {
		this.fromPage = fromPage;
	}

	public int getToPage() {
		return toPage;
	}

	public void setToPage(int toPage) {
		this.toPage = toPage;
	}

	public int getShowPageNum() {
		return showPageNum;
	}

	public void setShowPageNum(int showPageNum) {
		this.showPageNum = showPageNum;
	}
	
}
