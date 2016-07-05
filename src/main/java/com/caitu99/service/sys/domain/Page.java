package com.caitu99.service.sys.domain;

import java.io.Serializable;

public class Page implements Serializable {

    /**
     * 
     */
//    private static final long serialVersionUID = 508018938999349609L;

//    private long              currentPage      = 1;                  // 当前页
//
//    private long              pageSize         = 10;                 // 每页多少行
//
//    private long              totalRecord;                           // 共多少行
//
//    private long              startRow;                              // 当前页起始行
//
//    private long              endRow;                                // 结束行
//
//    private long              totalPage;                             // 共多少页
//
//    
//
//	public long getCurrentPage() {
//        return currentPage;
//    }
//
//    public void setCurrentPage(long currentPage) {
//        if (currentPage < 1) {
//            currentPage = 1;
//        } else {
//            startRow = pageSize * (currentPage - 1);
//        }
//        endRow = startRow + pageSize > totalRecord ? totalRecord : startRow + pageSize;
//        this.currentPage = currentPage;
//    }
//
//    public long getStartRow() {
//        return (currentPage - 1) * pageSize;
//    }
//
//    public long getEndRow() {
//        return currentPage * pageSize;
//    }
//
//    public long getPageSize() {
//        return pageSize;
//    }
//
//    public void setPageSize(long pageSize) {
//        this.pageSize = pageSize;
//    }
//
//    public long getTotalRecord() {
//        return totalRecord;
//    }
//
//    public void setTotalRecord(long totalRecord) {
//        totalPage = (totalRecord + pageSize - 1) / pageSize;
//        this.totalRecord = totalRecord;
//        if (totalPage < currentPage) {
//            currentPage = totalPage;
//            startRow = pageSize * (currentPage - 1);
//            endRow = totalRecord;
//        }
//        endRow = startRow + pageSize > totalRecord ? totalRecord : startRow + pageSize;
//    }
//
//    public long getTotalPage() {
//        return this.totalPage;
//    }
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 508018938999349609L;

	private int pageNow = 1; // 当前页数

	private int pageSize = 10; // 每页显示记录的条数

	private int totalCount; // 总的记录条数

	private int totalPageCount; // 总的页数

	@SuppressWarnings("unused")
	private int startPos; // 开始位置，从0开始

	@SuppressWarnings("unused")
	private boolean hasFirst;// 是否有首页

	@SuppressWarnings("unused")
	private boolean hasPre;// 是否有前一页

	@SuppressWarnings("unused")
	private boolean hasNext;// 是否有下一页

	@SuppressWarnings("unused")
	private boolean hasLast;// 是否有最后一页

	public Page(int totalCount, int pageNow) {
		this.totalCount = totalCount;
		this.pageNow = pageNow;
	}

	public int getTotalPageCount() {
		totalPageCount = getTotalCount() / getPageSize();
		return (totalCount % pageSize == 0) ? totalPageCount
				: totalPageCount + 1;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNow() {
		return pageNow;
	}

	public void setPageNow(int pageNow) {
		this.pageNow = pageNow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getStartPos() {
		return (pageNow - 1) * pageSize;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	/**
	 * 是否是第一页
	 * 
	 * @return
	 */
	public boolean isHasFirst() {
		return (pageNow == 1) ? false : true;
	}

	public void setHasFirst(boolean hasFirst) {
		this.hasFirst = hasFirst;
	}

	public boolean isHasPre() {
		// 如果有首页就有前一页，因为有首页就不是第一页
		return isHasFirst() ? true : false;
	}

	public void setHasPre(boolean hasPre) {
		this.hasPre = hasPre;
	}

	public boolean isHasNext() {
		// 如果有尾页就有下一页，因为有尾页表明不是最后一页
		return isHasLast() ? true : false;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public boolean isHasLast() {
		// 如果不是最后一页就有尾页
		return (pageNow == getTotalCount()) ? false : true;
	}

	public void setHasLast(boolean hasLast) {
		this.hasLast = hasLast;
	}
    
    
    
    

}
