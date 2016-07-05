package com.caitu99.service.transaction.domain;

/**
 * Created by Lion on 2015/11/30 0030.
 */
public class CusTransactionRecord extends TransactionRecord {
    private String iconurl;
    private String typestr;
    private String statusstr;
    private String totalstr;
    
    private String tubistr;
    private String rmbstr;
    
    


    public String getRmbstr() {
		return rmbstr;
	}

	public void setRmbstr(String rmbstr) {
		this.rmbstr = rmbstr;
	}

	public String getTubistr() {
		return tubistr;
	}

	public void setTubistr(String tubistr) {
		this.tubistr = tubistr;
	}

	public String getTotalstr() {
        return totalstr;
    }

    public void setTotalstr(String totalstr) {
        this.totalstr = totalstr;
    }

    public String getTypestr() {
        return typestr;
    }

    public void setTypestr(String typestr) {
        this.typestr = typestr;
    }

    public String getStatusstr() {
        return statusstr;
    }

    public void setStatusstr(String statusstr) {
        this.statusstr = statusstr;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }
}
