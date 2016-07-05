package com.caitu99.service.expedient.domain;

import java.util.Date;

public class ExpRecord {
	/** 积分查询 */
	public static final Integer integeral = 1;
	/**签到**/
	public static final Integer sig = 2;
	/**积分商城**/
	public static final Integer ishop = 3;
	/**变现**/
	public static final Integer realization = 4;
	/**分享**/
	public static final Integer share = 5;
	
	
    private Long id;

    private Long userId;

    private Long exp;

    /**
     * 1:积分查询
     * 2：签到
     * 3：积分商城
     * 4：变现
     * 5:分享
     */
    private Integer source;

    private String note;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    
    public static String getInfo(Integer source){
    	String info = "";
    	switch (source) {
		case 1:
			info = "积分查询";
			break;
		case 2:
			info = "签到";
			break;
		case 3:
			info = "积分商城";
			break;
		case 4:
			info = "变现";
			break;
		case 5:
			info = "分享";
			break;
		default:
			break;
		}
    	return info;
    }
    
}